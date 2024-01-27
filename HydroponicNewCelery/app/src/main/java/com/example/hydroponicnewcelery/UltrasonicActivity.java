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

public class UltrasonicActivity extends AppCompatActivity {

    private static final String BLYNK_API_BASE_URL = "http://blynk-cloud.com/";
    private static final String AUTH_TOKEN = "Yv1okzVekidmclcQ0R0LwPnlK087P_TdD";
    private static final int ULTRASONIC_VIRTUAL_PIN = 13;
    private static final int WATER_LEVEL_STATUS_VIRTUAL_PIN = 10;

    private TextView ultrasonicValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultrasonic);

        ultrasonicValueTextView = findViewById(R.id.ultrasonicValueTextView);
        Button updateUltrasonicButton = findViewById(R.id.updateUltrasonicButton);

        updateUltrasonicButton.setOnClickListener(v -> {
            // Retrieve ultrasonic data from Blynk
            retrieveAndDisplayUltrasonicValue();
        });
    }

    private void retrieveAndDisplayUltrasonicValue() {
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BLYNK_API_BASE_URL + AUTH_TOKEN + "/get/V" + ULTRASONIC_VIRTUAL_PIN;
        Request request = new Request.Builder().url(apiUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        // Handle the successful response
                        String responseBody = response.body() != null ? response.body().string() : "";
                        float ultrasonicValue = parseUltrasonicValue(responseBody);

                        // Update the UI on the main thread
                        runOnUiThread(() -> {
                            ultrasonicValueTextView.setText(String.format(Locale.getDefault(), "Ultrasonic: %.2f cm", ultrasonicValue));

                            // Determine Water Level Status based on Ultrasonic value
                            String waterLevelStatus = getWaterLevelStatus(ultrasonicValue);

                            // Send Water Level Status to Blynk
                            sendValueToBlynk(WATER_LEVEL_STATUS_VIRTUAL_PIN, waterLevelStatus);
                        });
                    } else {
                        // Handle unsuccessful response
                        Log.e("Blynk API", "Failed to retrieve ultrasonic value. Response code: " + response.code());
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

    private float parseUltrasonicValue(String responseBody) {
        // Parse the JSON response to extract the ultrasonic value
        try {
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            return jsonObject.get("0").getAsFloat(); // Assuming the ultrasonic value is at key "0"
        } catch (Exception e) {
            Log.e("Blynk API", "Error parsing ultrasonic value", e);
            return 0.0f;
        }
    }

    private String getWaterLevelStatus(float ultrasonicValue) {
        // Define your logic to determine the Water Level Status based on the ultrasonic value
        if (ultrasonicValue <= 8) {
            return "Full";
        } else if (ultrasonicValue >= 9 && ultrasonicValue <= 15) {
            return "Enough";
        } else {
            return "Empty";
        }
    }

    private void sendValueToBlynk(int virtualPin, String value) {
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BLYNK_API_BASE_URL + AUTH_TOKEN + "/update/V" + virtualPin + "?value=" + value;
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
