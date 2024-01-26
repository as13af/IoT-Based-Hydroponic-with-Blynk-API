package com.example.hydroponicnewcelery;

import android.util.Log;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;

public class TemperatureActivity extends AppCompatActivity {

    private static final String BLYNK_API_BASE_URL = "http://blynk-cloud.com/";
    private static final String AUTH_TOKEN = "Yv1okzVekidmclcQ0R0LwPnlK087P_TdD";
    private static final int TEMPERATURE_GAUGE_PIN = 5; // Adjust with your Blynk temperature gauge virtual pin
    private static final int TEMPERATURE_STATUS_PIN = 6; // Adjust with your Blynk temperature status virtual pin
    private static final int WATER_TEMPERATURE_STATUS_PIN = 14; // Adjust with your Blynk water temperature status virtual pin
    private static final int WATER_TEMPERATURE_PIN = 10; // Adjust with your Blynk water temperature virtual pin

    private TextView temperatureValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        temperatureValueTextView = findViewById(R.id.temperatureValueTextView);

        Button updateTemperatureButton = findViewById(R.id.updateTemperatureButton);
        updateTemperatureButton.setOnClickListener(v -> {
            float temperatureValue = getTemperature(); // Replace with actual temperature retrieval logic
            temperatureValueTextView.setText(String.format(Locale.getDefault(), "Temperature: %.2f °C", temperatureValue));

            // Send temperature value to Blynk using okhttp3
            sendValueToBlynk(TEMPERATURE_GAUGE_PIN, String.valueOf(temperatureValue));
        });

        Button updateTemperatureStatusButton = findViewById(R.id.updateTemperatureStatusButton);
        updateTemperatureStatusButton.setOnClickListener(v -> {
            float temperatureStatusValue = getTemperatureStatus(); // Replace with actual temperature status retrieval logic
            sendValueToBlynk(TEMPERATURE_STATUS_PIN, String.valueOf(temperatureStatusValue));
        });

        Button updateWaterTemperatureStatusButton = findViewById(R.id.updateWaterTemperatureStatusButton);
        updateWaterTemperatureStatusButton.setOnClickListener(v -> {
            float waterTemperatureStatusValue = getWaterTemperatureStatus();
            sendValueToBlynk(WATER_TEMPERATURE_STATUS_PIN, String.valueOf(waterTemperatureStatusValue));
            // Optionally, you can also update the water temperature itself
            float waterTemperatureValue = getWaterTemperature(); // Fix: Correct method name
            sendValueToBlynk(WATER_TEMPERATURE_PIN, String.valueOf(waterTemperatureValue));
        });

        // Add additional controls and listeners for other temperature-related features
        // For example, you can add controls for status temperatures and water temperature
        // and send their values to Blynk using the corresponding virtual pins.
    }

    // Replace with the actual logic to retrieve temperature
    private float getTemperature() {
        // For example, you might get the value from a sensor or another data source.
        return 25.0f; // Placeholder, replace with actual value retrieval logic
    }

    // Replace with the actual logic to retrieve temperature status
    private float getTemperatureStatus() {
        float currentTemperature = getTemperature();

        if (currentTemperature > 30.0f) {
            return 3.0f; // Hot temperature status
        } else if (currentTemperature >= 25.0f) {
            return 2.0f; // Normal temperature status
        } else {
            return 1.0f; // Cool temperature status
        }
    }

    // Replace with the actual logic to retrieve water temperature status
    private float getWaterTemperatureStatus() {
        float waterTemperature = getWaterTemperatureStatus();

        if (waterTemperature > 30.0f) {
            return 3.0f; // Hot temperature status
        } else if (waterTemperature >= 25.0f) {
            return 2.0f; // Normal temperature status
        } else {
            return 1.0f; // Cool temperature status
        }
    }

    // Replace with the actual logic to retrieve water temperature
    private float getWaterTemperature() {
        // For example, you might get the value from a sensor or another data source.
        return 22.5f; // Placeholder, replace with actual value retrieval logic
    }

    private void sendValueToBlynk(int virtualPin, String value) {
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BLYNK_API_BASE_URL + AUTH_TOKEN + "/update/V" + virtualPin + "?value=" + value;
        Request request = new Request.Builder().url(apiUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (!response.isSuccessful()) {
                    String errorMessage = "Handle Unsuccessful: " + response.code() + " - " + response.message();
                    Log.e("Blynk API", errorMessage);
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("Blynk API", "Failed to connect to Blynk API", e);
            }
        });
    }
}
