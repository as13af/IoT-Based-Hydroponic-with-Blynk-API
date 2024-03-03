package com.example.hydroponicnewcelery;

import android.content.pm.PackageManager;
import android.os.Build;
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

public class TemperatureActivity extends AppCompatActivity {

    private static final String BLYNK_API_BASE_URL = "https://blynk.cloud/external/api/";
    private static final String AUTH_TOKEN = "v1okzVekidmclcQ0R0LwPnlK087P_TdD";
    private static final int GAUGE_TEMPERATURE_VIRTUAL_PIN = 5;
    private static final int STATUS_TEMPERATURE_VIRTUAL_PIN = 6;
    private static final int GAUGE_WATER_TEMPERATURE_VIRTUAL_PIN = 14;
    private static final int STATUS_WATER_TEMPERATURE_VIRTUAL_PIN = 10;
    private static final int INTERNET_PERMISSION_REQUEST_CODE = 1;

    private TextView temperatureValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        temperatureValueTextView = findViewById(R.id.temperatureValueTextView);
        Button updateTemperatureButton = findViewById(R.id.updateTemperatureButton);

        updateTemperatureButton.setOnClickListener(v -> {
            // Check for INTERNET permission before making the request
            if (checkInternetPermission()) {
                // Retrieve temperature data from Blynk
                retrieveAndDisplayTemperatureValue();
            } else {
                // Request INTERNET permission
                requestInternetPermission();
            }
        });
    }

    private boolean checkInternetPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestInternetPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.INTERNET},
                INTERNET_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == INTERNET_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with retrieving data
                retrieveAndDisplayTemperatureValue();
            } else {
                // Permission denied, handle accordingly
                Log.e("Permission", "INTERNET permission denied");
            }
        }
    }

    private void retrieveAndDisplayTemperatureValue() {
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BLYNK_API_BASE_URL + "get?token=" + AUTH_TOKEN + "&V" + GAUGE_TEMPERATURE_VIRTUAL_PIN;
        Request request = new Request.Builder().url(apiUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    if (response.isSuccessful()) {
                        // Handle the successful response
                        String responseBody = response.body() != null ? response.body().string() : "";
                        float temperatureValue = parseTemperatureValue(responseBody);

                        // Update the UI on the main thread
                        runOnUiThread(() -> {
                            temperatureValueTextView.setText(String.format(Locale.getDefault(), "Temperature: %.2f °C", temperatureValue));

                            // Determine temperature status based on the retrieved value
                            String temperatureStatus = getTemperatureStatus(temperatureValue);

                            // Send values to Blynk for Gauge Temperature and Status Temperature
                            sendValueToBlynk(GAUGE_TEMPERATURE_VIRTUAL_PIN, String.valueOf(temperatureValue));
                            sendValueToBlynk(STATUS_TEMPERATURE_VIRTUAL_PIN, temperatureStatus);

                            // Determine water temperature status based on the retrieved value
                            String waterTemperatureStatus = getWaterTemperatureStatus(temperatureValue);

                            // Send values to Blynk for Gauge Water Temperature and Status Water Temperature
                            sendValueToBlynk(GAUGE_WATER_TEMPERATURE_VIRTUAL_PIN, String.valueOf(temperatureValue));
                            sendValueToBlynk(STATUS_WATER_TEMPERATURE_VIRTUAL_PIN, waterTemperatureStatus);
                        });
                    } else {
                        // Handle unsuccessful response
                        Log.e("Blynk API", "Failed to retrieve temperature value. Response code: " + response.code());
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
    }

    private float parseTemperatureValue(String responseBody) {
        // Parse the response body directly to float
        try {
            return Float.parseFloat(responseBody);
        } catch (NumberFormatException e) {
            Log.e("Blynk API", "Error parsing temperature value", e);
            return 0.0f;
        }
    }


    private String getTemperatureStatus(float temperatureValue) {
        // Define your logic to determine the status based on the temperature value
        if (temperatureValue <= 24) {
            return "Cool";
        } else if (temperatureValue >= 28 && temperatureValue <= 34) {
            return "Normal";
        } else {
            return "Hot";
        }
    }

    private String getWaterTemperatureStatus(float temperatureValue) {
        // Define your logic to determine the status based on the water temperature value
        if (temperatureValue <= 20) {
            return "Cool";
        } else if (temperatureValue >= 22 && temperatureValue <= 37) {
            return "Normal";
        } else {
            return "Hot";
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
}
