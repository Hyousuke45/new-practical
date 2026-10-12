package com.example.mad3new;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button act3Button;
    Button act4Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        act3Button = findViewById(R.id.act3Button);
        act4Button = findViewById(R.id.act4Button);

        act3Button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Act3Activity.class);
            startActivity(intent);
        });

        act4Button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, dicepanel.class);
            startActivity(intent);
        });
    }
}