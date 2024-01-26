package com.example.hydroponicnewcelery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button temperatureButton = findViewById(R.id.temperatureButton);
        temperatureButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, TemperatureActivity.class)));

        Button humidityButton = findViewById(R.id.humidityButton);
        humidityButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, HumidityActivity.class)));

        Button turbidityButton = findViewById(R.id.turbidityButton);
        turbidityButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, TurbidityActivity.class)));

        Button phButton = findViewById(R.id.phButton);
        phButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PHActivity.class)));

        Button sensorsButton = findViewById(R.id.sensorsButton);
        sensorsButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, UltrasonicActivity.class)));
    }
}
