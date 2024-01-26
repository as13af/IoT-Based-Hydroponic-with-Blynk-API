package com.example.hydroponicnewcelery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class UltrasonicActivity extends AppCompatActivity {

    private static final String AUTH_TOKEN = "v1okzVekidmclcQ0R0LwPnlK087P_TdD";
    private static final String ULTRASONIC_PIN = "V13"; // Replace with your ultrasonic pin number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultrasonic);

        Button sendUltrasonicButton = findViewById(R.id.sendUltrasonicButton);
        sendUltrasonicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDataToBlynk(AUTH_TOKEN, ULTRASONIC_PIN, "100"); // Replace "100" with actual ultrasonic data
            }
        });
    }

    private void sendDataToBlynk(String authToken, String pin, String value) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    String url = "http://blynk-cloud.com/" + authToken + "/update/" + pin + "?value=" + params[0];
                    // Perform the HTTP request (you might want to use a library like Retrofit for more robust HTTP requests)
                    // ...

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(value);
    }
}

