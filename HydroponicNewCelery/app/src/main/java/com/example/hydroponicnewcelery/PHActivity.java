package com.example.hydroponicnewcelery;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

public class PHActivity extends AppCompatActivity {

    private static final String BLYNK_API_BASE_URL = "http://blynk-cloud.com/";
    private static final String AUTH_TOKEN = "Yv1okzVekidmclcQ0R0LwPnlK087P_TdD";
    private static final int PH_VIRTUAL_PIN = 0; // Adjust with your Blynk pH virtual pin
    private static final int ACID_PUMP_VIRTUAL_PIN = 2; // Blynk virtual pin for Acid Pump
    private static final int BASE_PUMP_VIRTUAL_PIN = 3; // Blynk virtual pin for Base Pump

    private TextView phValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ph);

        phValueTextView = findViewById(R.id.phValueTextView);
        Button updatePhButton = findViewById(R.id.updatePhButton);
        Button acidPumpButton = findViewById(R.id.acidPumpButton);
        Button basePumpButton = findViewById(R.id.basePumpButton);

        // Retrieve and display the current pH value from Blynk
        retrieveAndDisplayPhValue();

        updatePhButton.setOnClickListener(v -> {
            // Logic to update the pH value
            float newPhValue = 7.0f; // Replace with actual pH update logic
            phValueTextView.setText(String.format(Locale.getDefault(), "pH Value: %.2f", newPhValue));

            // Determine water acidity level and control pumps accordingly
            handleWaterAcidityLevel(newPhValue);

            // Send the updated pH value to Blynk
            sendValueToBlynk(PH_VIRTUAL_PIN, String.valueOf(newPhValue));
        });

        acidPumpButton.setOnClickListener(v -> {
            // Logic to handle turning on Acid Pump
            sendValueToBlynk(ACID_PUMP_VIRTUAL_PIN, "1");
        });

        basePumpButton.setOnClickListener(v -> {
            // Logic to handle turning on Base Pump
            sendValueToBlynk(BASE_PUMP_VIRTUAL_PIN, "1");
        });
    }

    private void retrieveAndDisplayPhValue() {
        // Make a GET request to Blynk API to read the pH value
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BLYNK_API_BASE_URL + AUTH_TOKEN + "/get/V" + PH_VIRTUAL_PIN;
        Request request = new Request.Builder().url(apiUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        // Handle the successful response
                        String responseBody = response.body() != null ? response.body().string() : "";
                        float phValue = parsePhValue(responseBody);

                        // Update the UI on the main thread
                        runOnUiThread(() -> {
                            phValueTextView.setText(String.format(Locale.getDefault(), "pH Value: %.2f", phValue));
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
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Handle failure to connect to the Blynk API
                Log.e("Blynk API", "Failed to connect to Blynk API", e);
            }
        });
    }

    private float parsePhValue(String responseBody) {
        // Parse the JSON response to extract the pH value
        try {
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            return jsonObject.get("0").getAsFloat(); // Assuming the pH value is at key "0"
        } catch (Exception e) {
            Log.e("Blynk API", "Error parsing pH value", e);
            return 0.0f;
        }
    }

    private void sendValueToBlynk(int virtualPin, String value) {
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BLYNK_API_BASE_URL + AUTH_TOKEN + "/update/V" + virtualPin + "?value=" + value;
        Request request = new Request.Builder().url(apiUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                // Handle the Blynk API response
                if (!response.isSuccessful()) {
                    // Handle unsuccessful response
                    String errorMessage = "Handle Unsuccessful: " + response.code() + " - " + response.message();
                    Log.e("Blynk API", errorMessage);
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
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
