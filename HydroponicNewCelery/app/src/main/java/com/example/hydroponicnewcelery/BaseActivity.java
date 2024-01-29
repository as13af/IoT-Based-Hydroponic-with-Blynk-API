package com.example.hydroponicnewcelery;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_temperature) {
                startActivity(new Intent(BaseActivity.this, TemperatureActivity.class));
                return true;
            } else if (itemId == R.id.action_ph) {
                startActivity(new Intent(BaseActivity.this, PHActivity.class));
                return true;
            } else if (itemId == R.id.action_humidity) {
                startActivity(new Intent(BaseActivity.this, HumidityActivity.class));
                return true;
            }
            return false;
        });
    }
}
