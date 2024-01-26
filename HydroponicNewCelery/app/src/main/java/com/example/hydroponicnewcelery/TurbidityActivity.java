package com.example.hydroponicnewcelery;

import androidx.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
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

public class TurbidityActivity extends AppCompatActivity {

    private static final String BLYNK_API_BASE_URL = "http://blynk-cloud.com/";
    private static final String AUTH_TOKEN = "Yv1okzVekidmclcQ0R0LwPnlK087P_TdD";
    private static final int TURBIDITY_GAUGE_PIN = 1; // Adjust with your Blynk turbidity gauge virtual pin
    private static final int TURBIDITY_STATUS_PIN = 11; // Adjust with your Blynk turbidity status virtual pin

    private TextView turbidityValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turbidity);

        turbidityValueTextView = findViewById(R.id.turbidityValueTextView);
        Button updateTurbidityButton = findViewById(R.id.updateTurbidityButton);

        updateTurbidityButton.setOnClickListener(v -> {
            float turbidityValue = getTurbidity(); // Replace with actual turbidity retrieval logic
            turbidityValueTextView.setText(String.format(Locale.getDefault(), getString(R.string.turbidity_value_format), turbidityValue));
            sendValueToBlynk(TURBIDITY_GAUGE_PIN, String.valueOf(turbidityValue));

            int turbidityStatusValue = getTurbidityStatus(turbidityValue); // Replace with actual turbidity status retrieval logic
            sendValueToBlynk(TURBIDITY_STATUS_PIN, String.valueOf(turbidityStatusValue));
        });
    }

    // Replace with the actual logic to retrieve turbidity
    private float getTurbidity() {
        // Placeholder, replace with actual turbidity value retrieval logic
        return 5.0f;
    }

    // Replace with the actual logic to retrieve turbidity status
    private int getTurbidityStatus(float turbidityValue) {
        if (turbidityValue >= 0 && turbidityValue <= 15) {
            return 0; // Clean
        } else if (turbidityValue > 15 && turbidityValue <= 25) {
            return 1; // Fairly Turbid
        } else if (turbidityValue > 25 && turbidityValue <= 35) {
            return 2; // Rather Turbid
        } else if (turbidityValue > 35 && turbidityValue <= 50) {
            return 3; // Turbid
        } else {
            return 4; // Very Turbid
        }
    }

    private void sendValueToBlynk(int virtualPin, String value) {
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BLYNK_API_BASE_URL + AUTH_TOKEN + "/update/V" + virtualPin + "?value=" + value;
        Request request = new Request.Builder().url(apiUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (!response.isSuccessful()) {
                    String errorMessage = getString(R.string.api_error_message_format, response.code(), response.message());
                    Log.e(getString(R.string.blynk_api_tag), errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(getString(R.string.blynk_api_tag), getString(R.string.api_failure_message), e);
            }
        });
    }
}
