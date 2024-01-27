package com.example.hydroponicnewcelery;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
