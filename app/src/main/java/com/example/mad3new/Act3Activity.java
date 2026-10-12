package com.example.mad3new;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Act3Activity extends AppCompatActivity {
    private final ArrayList<String> bmiHistory = new ArrayList<>();
    private int historyIndex = -1;

    private EditText weightInput;
    private EditText heightInput;

    private Button calculateButton;

    private TextView bmiResult;
    private TextView bmiCategory;

    private LinearLayout bmiHistoryLayout;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_act3);

        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);

        calculateButton = findViewById(R.id.calculateButton);

        bmiResult = findViewById(R.id.bmiResult);
        bmiCategory = findViewById(R.id.bmiCategory);



        databaseHelper = new DatabaseHelper(this);


        calculateButton.setOnClickListener(v -> calculateBMI());
    }


    private void calculateBMI() {
        String weightText = weightInput.getText().toString().trim();
        String heightText = heightInput.getText().toString().trim();

        if (weightText.isEmpty() || heightText.isEmpty()) {
            Toast.makeText(this,
                    "Please enter your weight and height.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double weight;
        double heightCm;

        try {
            weight = Double.parseDouble(weightText);
            heightCm = Double.parseDouble(heightText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid numbers.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (weight <= 0 || heightCm <= 0) {
            Toast.makeText(this, "Values must be greater than zero.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double heightMeters = heightCm / 100.0;
        double heightSquared = heightMeters * heightMeters;
        double bmi = weight / heightSquared;

        String category;
        String guidance;

        double underweightLimit = 18.5 * heightSquared;
        double normalLimit = 25.0 * heightSquared;
        double overweightLimit = 30.0 * heightSquared;

        if (bmi < 18.5) {
            category = "Underweight";

            double needed = underweightLimit - weight;
            guidance = String.format(Locale.getDefault(),
                    "You need %.1f kg more to reach the normal-weight range.",
                    needed);

        } else if (bmi < 25.0) {
            category = "Normal weight";

            double toUnderweight = weight - underweightLimit;
            double toOverweight = normalLimit - weight;

            guidance = String.format(Locale.getDefault(),
                    "You would need to lose %.1f kg to reach the " +
                            "underweight boundary, or gain %.1f kg to reach " +
                            "the overweight boundary.",
                    toUnderweight, toOverweight);

        } else if (bmi < 30.0) {
            category = "Overweight";

            double toNormal = weight - normalLimit;
            double toObese = overweightLimit - weight;

            guidance = String.format(Locale.getDefault(),
                    "You would need to lose %.1f kg to reach the " +
                            "normal-weight boundary, or gain %.1f kg to " +
                            "reach the obesity boundary.",
                    toNormal, toObese);

        } else {
            category = "Obese";

            double toOverweight = weight - overweightLimit;

            guidance = String.format(Locale.getDefault(),
                    "You would need to lose %.1f kg to reach the " +
                            "overweight boundary.",
                    toOverweight);
        }

        bmiResult.setText(String.format(Locale.getDefault(),
                "BMI: %.2f", bmi));

        bmiCategory.setText("Category: " + category);

        // Display the weight-range guidance in a separate TextView.
        TextView guidanceView = findViewById(R.id.bmiGuidance);
        guidanceView.setText(guidance);

        bmiResult.setScaleX(0.7f);
        bmiResult.setScaleY(0.7f);
        bmiResult.setAlpha(0f);

        bmiResult.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(300)
                .start();

        bmiCategory.setAlpha(0f);
        bmiCategory.animate()
                .alpha(1f)
                .setDuration(400)
                .start();



            String result = bmiResult.getText().toString();

        bmiHistory.add(result);
        historyIndex = bmiHistory.size() - 1;
    }

}