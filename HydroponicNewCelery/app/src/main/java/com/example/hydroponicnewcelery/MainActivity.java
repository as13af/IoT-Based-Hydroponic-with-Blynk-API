package com.example.hydroponicnewcelery;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TextView temperatureTextView;
    private TextView phTextView;
    private TextView humidityTextView;
    private TextView waterLevelTextView;
    private TextView turbidityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TextViews
        temperatureTextView = findViewById(R.id.temperatureValueTextView);
        phTextView = findViewById(R.id.pHValueTextView);
        humidityTextView = findViewById(R.id.HumidityValueTextView);
        waterLevelTextView = findViewById(R.id.WaterLevelValueTextView);
        turbidityTextView = findViewById(R.id.TurbidityValueTextView);

        // Check if MainActivity was started by another activity with data
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();

            // Retrieve and display temperature value
            if (extras.containsKey("TEMPERATURE_VALUE")) {
                float temperatureValue = extras.getFloat("TEMPERATURE_VALUE");
                temperatureTextView.setText(String.format("Temperature: %.2f", temperatureValue));
            }

            // Retrieve and display pH value
            if (extras.containsKey("PH_VALUE")) {
                float phValue = extras.getFloat("PH_VALUE");
                phTextView.setText(String.format("pH: %.2f", phValue));
            }

            // Retrieve and display humidity value
            if (extras.containsKey("HUMIDITY_VALUE")) {
                float humidityValue = extras.getFloat("HUMIDITY_VALUE");
                humidityTextView.setText(String.format("Humidity: %.2f", humidityValue));
            }

            // Retrieve and display water level value
            if (extras.containsKey("WATER_LEVEL_VALUE")) {
                float waterLevelValue = extras.getFloat("WATER_LEVEL_VALUE");
                waterLevelTextView.setText(String.format("Water Level: %.2f", waterLevelValue));
            }

            // Retrieve and display turbidity value
            if (extras.containsKey("TURBIDITY_VALUE")) {
                float turbidityValue = extras.getFloat("TURBIDITY_VALUE");
                turbidityTextView.setText(String.format("Turbidity: %.2f", turbidityValue));
            }
        }

        Button temperatureButton = findViewById(R.id.temperatureButton);
        temperatureButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, TemperatureActivity.class)));

        Button phButton = findViewById(R.id.phButton);
        phButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PHActivity.class)));

        Button humidityButton = findViewById(R.id.humidityButton);
        humidityButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, HumidityActivity.class)));

        Button waterLevelButton = findViewById(R.id.waterLevelButton);
        waterLevelButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, UltrasonicActivity.class)));

        Button turbidityButton = findViewById(R.id.turbidityButton);
        turbidityButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, TurbidityActivity.class)));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_temperature) {
                startActivity(new Intent(MainActivity.this, TemperatureActivity.class));
                return true;
            } else if (itemId == R.id.action_ph) {
                startActivity(new Intent(MainActivity.this, PHActivity.class));
                return true;
            } else if (itemId == R.id.action_humidity) {
                startActivity(new Intent(MainActivity.this, HumidityActivity.class));
                return true;
            }
            return false;
        });
    }
}
