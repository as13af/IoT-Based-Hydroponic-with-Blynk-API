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

        updatePhButton.setOnClickListener(v -> {
            float phValue = 7.0f; // Replace with actual pH retrieval logic
            phValueTextView.setText(String.format(Locale.getDefault(), "pH Value: %.2f", phValue));
            sendValueToBlynk(PH_VIRTUAL_PIN, String.valueOf(phValue));
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

    private void sendValueToBlynk(int virtualPin, String value) {
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BLYNK_API_BASE_URL + AUTH_TOKEN + "/update/V" + virtualPin + "?value=" + value;
        Request request = new Request.Builder().url(apiUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
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
}
