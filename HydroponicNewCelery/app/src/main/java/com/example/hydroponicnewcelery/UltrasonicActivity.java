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
            float ultrasonicValue = 10.0f; // Replace with actual ultrasonic retrieval logic
            ultrasonicValueTextView.setText(String.format(Locale.getDefault(), "Ultrasonic: %.2f cm", ultrasonicValue));

            // Send Ultrasonic value to Blynk
            sendValueToBlynk(ULTRASONIC_VIRTUAL_PIN, String.valueOf(ultrasonicValue));

            // Determine Water Level Status based on Ultrasonic value
            float waterLevelStatus = getWaterLevelStatus(ultrasonicValue);

            // Send Water Level Status to Blynk
            sendValueToBlynk(WATER_LEVEL_STATUS_VIRTUAL_PIN, String.valueOf(waterLevelStatus));
        });
    }

    // Replace with the actual logic to determine Water Level Status
    private float getWaterLevelStatus(float ultrasonicValue) {
        if (ultrasonicValue <= 30.0f) {
            return 1.0f; // Low water level
        } else if (ultrasonicValue <= 80.0f) {
            return 2.0f; // Normal water level
        } else {
            return 3.0f; // High water level
        }
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
