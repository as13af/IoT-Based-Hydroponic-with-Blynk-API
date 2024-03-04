package com.example.hydroponicnewcelery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView temperatureTextView;
    private TextView phTextView;
    private TextView humidityTextView;
    private TextView waterLevelTextView;
    private TextView turbidityTextView;

    private static final int REQUEST_CODE_TEMPERATURE = 1;
    private static final int REQUEST_CODE_PH = 2;
    private static final int REQUEST_CODE_HUMIDITY = 3;
    private static final int REQUEST_CODE_WATER_LEVEL = 4;
    private static final int REQUEST_CODE_TURBIDITY = 5;

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

        Button temperatureButton = findViewById(R.id.temperatureButton);
        temperatureButton.setOnClickListener(view -> startActivityForResult(new Intent(MainActivity.this, TemperatureActivity.class), REQUEST_CODE_TEMPERATURE));

        Button phButton = findViewById(R.id.phButton);
        phButton.setOnClickListener(view -> startActivityForResult(new Intent(MainActivity.this, PHActivity.class), REQUEST_CODE_PH));

        Button humidityButton = findViewById(R.id.humidityButton);
        humidityButton.setOnClickListener(view -> startActivityForResult(new Intent(MainActivity.this, HumidityActivity.class), REQUEST_CODE_HUMIDITY));

        Button waterLevelButton = findViewById(R.id.waterLevelButton);
        waterLevelButton.setOnClickListener(view -> startActivityForResult(new Intent(MainActivity.this, UltrasonicActivity.class), REQUEST_CODE_WATER_LEVEL));

        Button turbidityButton = findViewById(R.id.turbidityButton);
        turbidityButton.setOnClickListener(view -> startActivityForResult(new Intent(MainActivity.this, TurbidityActivity.class), REQUEST_CODE_TURBIDITY));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_temperature) {
                startActivityForResult(new Intent(MainActivity.this, TemperatureActivity.class), REQUEST_CODE_TEMPERATURE);
                return true;
            } else if (itemId == R.id.action_ph) {
                startActivityForResult(new Intent(MainActivity.this, PHActivity.class), REQUEST_CODE_PH);
                return true;
            } else if (itemId == R.id.action_humidity) {
                startActivityForResult(new Intent(MainActivity.this, HumidityActivity.class), REQUEST_CODE_HUMIDITY);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            float value = data.getFloatExtra("VALUE", 0.0f);
            switch (requestCode) {
                case REQUEST_CODE_TEMPERATURE:
                    temperatureTextView.setText(String.format(Locale.getDefault(), "Temperature: %.2f", value));
                    break;
                case REQUEST_CODE_PH:
                    phTextView.setText(String.format(Locale.getDefault(), "pH: %.2f", value));
                    break;
                case REQUEST_CODE_HUMIDITY:
                    humidityTextView.setText(String.format(Locale.getDefault(), "Humidity: %.2f", value));
                    break;
                case REQUEST_CODE_WATER_LEVEL:
                    waterLevelTextView.setText(String.format(Locale.getDefault(), "Water Level: %.2f", value));
                    break;
                case REQUEST_CODE_TURBIDITY:
                    turbidityTextView.setText(String.format(Locale.getDefault(), "Turbidity: %.2f", value));
                    break;
            }
        }
    }
}
