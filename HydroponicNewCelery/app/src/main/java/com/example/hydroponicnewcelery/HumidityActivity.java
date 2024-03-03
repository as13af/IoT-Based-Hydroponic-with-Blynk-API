package com.example.hydroponicnewcelery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HumidityActivity extends AppCompatActivity {

    private static final String BLYNK_API_BASE_URL = "https://blynk.cloud/external/api/";
    private static final String AUTH_TOKEN = "v1okzVekidmclcQ0R0LwPnlK087P_TdD";
    private static final int GAUGE_HUMIDITY_VIRTUAL_PIN = 4;
    private static final int STATUS_HUMIDITY_VIRTUAL_PIN = 9;

    private TextView humidityValueTextView;

    // Request code for INTERNET permission
    private static final int INTERNET_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidity);

        humidityValueTextView = findViewById(R.id.humidityValueTextView);
        Button updateHumidityButton = findViewById(R.id.updateHumidityButton);

        updateHumidityButton.setOnClickListener(v -> {
            // Check for INTERNET permission before making the network request
            if (hasInternetPermission()) {
                retrieveAndDisplayHumidityValue();
            } else {
                // Request INTERNET permission
                requestInternetPermission();
            }
        });
    }

    private void retrieveAndDisplayHumidityValue() {
        // Check for INTERNET permission before proceeding
        if (hasInternetPermission()) {
            OkHttpClient client = new OkHttpClient();
            String apiUrl = BLYNK_API_BASE_URL + "get?token=" + AUTH_TOKEN + "&V" + GAUGE_HUMIDITY_VIRTUAL_PIN;
            Request request = new Request.Builder().url(apiUrl).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try {
                        if (response.isSuccessful()) {
                            // Handle the successful response
                            String responseBody = response.body() != null ? response.body().string() : "";
                            float humidityValue = parseHumidityValue(responseBody);

                            // Update the UI on the main thread
                            runOnUiThread(() -> {
                                humidityValueTextView.setText(String.format(Locale.getDefault(), "Humidity: %.2f %%", humidityValue));

                                // Determine humidity status based on the retrieved value
                                String humidityStatus = getHumidityStatus(humidityValue);

                                // Send values to Blynk for Gauge Humidity and Status Humidity
                                sendValueToBlynk(GAUGE_HUMIDITY_VIRTUAL_PIN, String.valueOf(humidityValue));
                                sendValueToBlynk(STATUS_HUMIDITY_VIRTUAL_PIN, humidityStatus);
                            });
                        } else {
                            // Handle unsuccessful response
                            Log.e("Blynk API", "Failed to retrieve humidity value. Response code: " + response.code());
                        }
                    } catch (IOException e) {
                        Log.e("Blynk API", "Error reading response", e);
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // Handle failure to connect to the Blynk API
                    Log.e("Blynk API", "Failed to connect to Blynk API", e);
                }
            });
        } else {
            // Request INTERNET permission
            requestInternetPermission();
        }
    }

    private float parseHumidityValue(String responseBody) {
        // Parse the response body directly to float
        try {
            return Float.parseFloat(responseBody);
        } catch (NumberFormatException e) {
            Log.e("Blynk API", "Error parsing humidity value", e);
            return 0.0f;
        }
    }

    private String getHumidityStatus(float humidityValue) {
        // Define your logic to determine the status based on the humidity value
        if (humidityValue <= 30) {
            return "Dry";
        } else if (humidityValue > 30 && humidityValue <= 60) {
            return "Normal";
        } else {
            return "Moist";
        }
    }

    private void sendValueToBlynk(int virtualPin, String value) {
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BLYNK_API_BASE_URL + "update?token=" + AUTH_TOKEN + "&V" + virtualPin + "=" + value;
        Request request = new Request.Builder().url(apiUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                // Handle the Blynk API response if needed
                if (!response.isSuccessful()) {
                    String errorMessage = "Handle Unsuccessful: " + response.code() + " - " + response.message();
                    Log.e("Blynk API", errorMessage);
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Handle failure to connect to the Blynk API
                Log.e("Blynk API", "Failed to connect to Blynk API", e);
            }
        });
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
                retrieveAndDisplayHumidityValue();
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
                Log.e("Blynk API", "Internet permission denied");
            }
        }
    }
}

