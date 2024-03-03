package com.example.hydroponicnewcelery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class UltrasonicActivity extends AppCompatActivity {

    private static final String BLYNK_API_BASE_URL = "https://blynk.cloud/external/api/";
    private static final String AUTH_TOKEN = "v1okzVekidmclcQ0R0LwPnlK087P_TdD";
    private static final int ULTRASONIC_VIRTUAL_PIN = 12;
    private static final int WATER_LEVEL_STATUS_VIRTUAL_PIN = 13;
    private static final int INTERNET_PERMISSION_REQUEST_CODE = 1;

    private TextView ultrasonicValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultrasonic);

        ultrasonicValueTextView = findViewById(R.id.ultrasonicValueTextView);
        Button updateUltrasonicButton = findViewById(R.id.updateUltrasonicButton);

        updateUltrasonicButton.setOnClickListener(v -> {
            // Check for internet permission before making the request
            if (checkInternetPermission()) {
                // Retrieve ultrasonic data from Blynk
                retrieveAndDisplayUltrasonicValue();
            } else {
                // Request internet permission
                requestInternetPermission();
            }
        });
    }

    private boolean checkInternetPermission() {
        // Check if the app has internet permission
        return ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestInternetPermission() {
        // Request internet permission if not granted
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Handle the result of the permission request
        if (requestCode == INTERNET_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, retrieve ultrasonic data
                retrieveAndDisplayUltrasonicValue();
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Internet permission denied. Unable to retrieve data.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void retrieveAndDisplayUltrasonicValue() {
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BLYNK_API_BASE_URL + "get?token=" + AUTH_TOKEN + "&V" + ULTRASONIC_VIRTUAL_PIN;
        Request request = new Request.Builder().url(apiUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    if (response.isSuccessful()) {
                        // Handle the successful response
                        String responseBody = response.body() != null ? response.body().string() : "";
                        float ultrasonicValue = parseUltrasonicValue(responseBody);

                        // Update the UI on the main thread
                        runOnUiThread(() -> {
                            ultrasonicValueTextView.setText(String.format(Locale.getDefault(), "Ultrasonic: %.2f cm", ultrasonicValue));

                            // Pass ultrasonic value to MainActivity
                            Intent intent = new Intent(UltrasonicActivity.this, MainActivity.class);
                            intent.putExtra("ULTRASONIC_VALUE", ultrasonicValue);
                            startActivity(intent);

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
        // Parse the response body directly to float
        try {
            return Float.parseFloat(responseBody);
        } catch (NumberFormatException e) {
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
