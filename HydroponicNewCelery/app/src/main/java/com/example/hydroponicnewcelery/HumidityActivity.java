package com.example.hydroponicnewcelery;

import android.util.Log;
import org.jetbrains.annotations.NotNull;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.Locale;

public class HumidityActivity extends AppCompatActivity {

    private static final String BLYNK_API_BASE_URL = "http://blynk-cloud.com/";
    private static final String AUTH_TOKEN = "Yv1okzVekidmclcQ0R0LwPnlK087P_TdD";
    private static final int GAUGE_HUMIDITY_VIRTUAL_PIN = 4;
    private static final int STATUS_HUMIDITY_VIRTUAL_PIN = 9;

    private TextView humidityValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidity);

        humidityValueTextView = findViewById(R.id.humidityValueTextView);
        Button updateHumidityButton = findViewById(R.id.updateHumidityButton);

        updateHumidityButton.setOnClickListener(v -> {
            float humidityValue = 60.0f; // Replace with actual humidity retrieval logic
            humidityValueTextView.setText(String.format(Locale.getDefault(), "Humidity: %.2f %%", humidityValue));

            // Send values to Blynk for Gauge Humidity and Status Humidity
            sendValueToBlynk(GAUGE_HUMIDITY_VIRTUAL_PIN, String.valueOf(humidityValue));
            sendValueToBlynk(STATUS_HUMIDITY_VIRTUAL_PIN, getStatusValue(humidityValue));
        });
    }

    private String getStatusValue(float humidityValue) {
        // Define your logic to determine the status based on the humidity value
        // For example, if humidity is above a certain threshold, consider it "OK"; otherwise, "High" or "Low"
        return (humidityValue > 50.0f) ? "OK" : "High"; // Modify this logic as per your requirement
    }

    private void sendValueToBlynk(int virtualPin, String value) {
        OkHttpClient client = new OkHttpClient();

        // Construct the Blynk API URL for writing virtual pin data
        String apiUrl = BLYNK_API_BASE_URL + AUTH_TOKEN + "/update/V" + virtualPin + "?value=" + value;

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

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
