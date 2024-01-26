package com.example.hydroponicnewcelery;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TemperatureActivity extends AppCompatActivity {

    private static final String AUTH_TOKEN = "v1okzVekidmclcQ0R0LwPnlK087P_TdD";
    private static final String TEMPERATURE_PIN = "V6"; // Replace with your temperature pin number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        Button updateTemperatureButton = findViewById(R.id.updateTemperatureButton);
        updateTemperatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Replace "25.5" with the actual temperature value
                sendDataToBlynk(AUTH_TOKEN, TEMPERATURE_PIN, "25.5");
            }
        });
    }

    private void sendDataToBlynk(String authToken, String pin, String value) {
        // AsyncTask code similar to the previous example
    }
}

