package com.example.hydroponicnewcelery;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HumidityActivity extends AppCompatActivity {

    private static final String AUTH_TOKEN = "v1okzVekidmclcQ0R0LwPnlK087P_TdD";
    private static final String HUMIDITY_PIN = "V9"; // Replace with your humidity pin number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidity);

        Button updateHumidityButton = findViewById(R.id.updateHumidityButton);
        updateHumidityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Replace "60.0" with the actual humidity value
                sendDataToBlynk(AUTH_TOKEN, HUMIDITY_PIN, "60.0");
            }
        });
    }

    private void sendDataToBlynk(String authToken, String pin, String value) {
        // AsyncTask code similar to the previous example
    }
}

