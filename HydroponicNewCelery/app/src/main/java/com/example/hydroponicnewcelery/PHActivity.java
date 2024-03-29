package com.example.hydroponicnewcelery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PHActivity extends AppCompatActivity {

    private static final String BLYNK_API_BASE_URL = "https://blynk.cloud/external/api/";
    private static final String AUTH_TOKEN = "v1okzVekidmclcQ0R0LwPnlK087P_TdD";
    private static final int PH_VIRTUAL_PIN = 0; // Adjust with your Blynk pH virtual pin
    private static final int ACID_PUMP_VIRTUAL_PIN = 8; // Blynk virtual pin for Acid Pump
    private static final int BASE_PUMP_VIRTUAL_PIN = 7; // Blynk virtual pin for Base Pump

    private TextView phValueTextView;

    // Request code for INTERNET permission
    private static final int INTERNET_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ph);

        phValueTextView = findViewById(R.id.phValueTextView);
        Button acidPumpButton = findViewById(R.id.acidPumpButton);
        Button basePumpButton = findViewById(R.id.basePumpButton);
        Button acidoffPumpButton = findViewById(R.id.acidoffPumpButton);
        Button baseoffPumpButton = findViewById(R.id.baseoffPumpButton);

        acidPumpButton.setOnClickListener(v -> {
            // Logic to handle turning on Acid Pump
            sendValueToBlynk(ACID_PUMP_VIRTUAL_PIN, "1");
        });

        acidoffPumpButton.setOnClickListener(v -> {
            // Logic to handle turning off Acid Pump
            sendValueToBlynk(ACID_PUMP_VIRTUAL_PIN, "0");
        });

        basePumpButton.setOnClickListener(v -> {
            // Logic to handle turning on Base Pump
            sendValueToBlynk(BASE_PUMP_VIRTUAL_PIN, "1");
        });

        baseoffPumpButton.setOnClickListener(v -> {
            // Logic to handle turning off Base Pump
            sendValueToBlynk(BASE_PUMP_VIRTUAL_PIN, "0");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Retrieve and display the current pH value from Blynk
        if (hasInternetPermission()) {
            retrieveAndDisplayPhValue();
        } else {
            requestInternetPermission();
        }
    }

    // Check if the app has INTERNET permission
    private boolean hasInternetPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
    }

    // Request INTERNET permission
    private void requestInternetPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION_REQUEST_CODE);
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == INTERNET_PERMISSION_REQUEST_CODE) {
            // Check if the permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with the network request
                retrieveAndDisplayPhValue();
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
                Log.e("Blynk API", "Internet permission denied");
            }
        }
    }

    private void retrieveAndDisplayPhValue() {
        // Make a GET request to Blynk API to read the pH value
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BLYNK_API_BASE_URL + "get?token=" + AUTH_TOKEN + "&V" + PH_VIRTUAL_PIN;
        Request request = new Request.Builder().url(apiUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    if (response.isSuccessful()) {
                        // Handle the successful response
                        String responseBody = response.body() != null ? response.body().string() : "";
                        float phValue = parsePhValue(responseBody);

                        // Update the UI on the main thread
                        runOnUiThread(() -> {
                            phValueTextView.setText(String.format(Locale.getDefault(), "pH Value: %.2f", phValue));

                            // Pass pH value to MainActivity
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("PH_VALUE", phValue);
                            setResult(RESULT_OK, resultIntent);
                            finish();

                            // Determine water acidity level and control pumps accordingly
                            handleWaterAcidityLevel(phValue);
                        });
                    } else {
                        // Handle unsuccessful response
                        Log.e("Blynk API", "Failed to retrieve pH value. Response code: " + response.code());
                    }
                } catch (IOException e) {
                    Log.e("Blynk API", "Error reading response", e);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle failure to connect to the Blynk API
                Log.e("Blynk API", "Failed to connect to Blynk API", e);
            }
        });
    }

    private float parsePhValue(String responseBody) {
        // Parse the response body directly to float
        try {
            return Float.parseFloat(responseBody);
        } catch (NumberFormatException e) {
            Log.e("Blynk API", "Error parsing pH value", e);
            return 0.0f;
        }
    }

    private void sendValueToBlynk(int virtualPin, String value) {
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BLYNK_API_BASE_URL + "update?token=" + AUTH_TOKEN + "&V" + virtualPin + "=" + value;
        Request request = new Request.Builder().url(apiUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                // Handle the Blynk API response
                if (!response.isSuccessful()) {
                    // Handle unsuccessful response
                    String errorMessage = "Handle Unsuccessful: " + response.code() + " - " + response.message();
                    Log.e("Blynk API", errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle failure to connect to the Blynk API
                Log.e("Blynk API", "Failed to connect to Blynk API", e);
            }
        });
    }

    private void handleWaterAcidityLevel(float pHValue) {
        // Logic to determine water acidity level and control pumps accordingly
        if (pHValue >= 1 && pHValue <= 5) {
            // Water contains Acid, you can implement logic to control Acid Pump here
            Log.d("Water Acidity", "Water contains Acid");
        } else if (pHValue >= 6 && pHValue <= 7) {
            // Water is normal
            Log.d("Water Acidity", "Water is normal");
        } else {
            // Water contains Base, you can implement logic to control Base Pump here
            Log.d("Water Acidity", "Water contains Base");
        }
    }
}
