package com.example.hydroponicnewcelery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView temperatureTextView;
    private TextView phTextView;
    private TextView humidityTextView;
    private TextView waterLevelTextView;
    private TextView turbidityTextView;

    private final ActivityResultLauncher<Intent> temperatureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            float value = result.getData().getFloatExtra("VALUE", 0.0f);
            temperatureTextView.setText(String.format(Locale.getDefault(), "Temperature: %.2f", value));
        }
    });

    private final ActivityResultLauncher<Intent> phLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            float value = result.getData().getFloatExtra("VALUE", 0.0f);
            phTextView.setText(String.format(Locale.getDefault(), "pH: %.2f", value));
        }
    });

    private final ActivityResultLauncher<Intent> humidityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            float value = result.getData().getFloatExtra("VALUE", 0.0f);
            humidityTextView.setText(String.format(Locale.getDefault(), "Humidity: %.2f", value));
        }
    });

    private final ActivityResultLauncher<Intent> waterLevelLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            float value = result.getData().getFloatExtra("VALUE", 0.0f);
            waterLevelTextView.setText(String.format(Locale.getDefault(), "Water Level: %.2f", value));
        }
    });

    private final ActivityResultLauncher<Intent> turbidityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            float value = result.getData().getFloatExtra("VALUE", 0.0f);
            turbidityTextView.setText(String.format(Locale.getDefault(), "Turbidity: %.2f", value));
        }
    });

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
        temperatureButton.setOnClickListener(view -> temperatureLauncher.launch(new Intent(MainActivity.this, TemperatureActivity.class)));

        Button phButton = findViewById(R.id.phButton);
        phButton.setOnClickListener(view -> phLauncher.launch(new Intent(MainActivity.this, PHActivity.class)));

        Button humidityButton = findViewById(R.id.humidityButton);
        humidityButton.setOnClickListener(view -> humidityLauncher.launch(new Intent(MainActivity.this, HumidityActivity.class)));

        Button waterLevelButton = findViewById(R.id.waterLevelButton);
        waterLevelButton.setOnClickListener(view -> waterLevelLauncher.launch(new Intent(MainActivity.this, UltrasonicActivity.class)));

        Button turbidityButton = findViewById(R.id.turbidityButton);
        turbidityButton.setOnClickListener(view -> turbidityLauncher.launch(new Intent(MainActivity.this, TurbidityActivity.class)));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_temperature) {
                temperatureLauncher.launch(new Intent(MainActivity.this, TemperatureActivity.class));
                return true;
            } else if (itemId == R.id.action_ph) {
                phLauncher.launch(new Intent(MainActivity.this, PHActivity.class));
                return true;
            } else if (itemId == R.id.action_humidity) {
                humidityLauncher.launch(new Intent(MainActivity.this, HumidityActivity.class));
                return true;
            }
            return false;
        });
    }
}
