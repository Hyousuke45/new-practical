
package com.example.mad3new;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Act4Activity extends AppCompatActivity {

    private Button rollButton, previousButton, nextButton, exitButton;
    private TextView resultText, name;
    private TextView previousPlayerName, nextPlayerName;
    private GridLayout diceGrid;

    private ArrayList<UserData> players = new ArrayList<>();
    private final List<DiceView> diceViews = new ArrayList<>();
    private final Random random = new Random();

    private int currentPlayerIndex = 0;
    private int numberOfDice = 1;
    private boolean isRolling = false;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act4);

        name = findViewById(R.id.playername);
        rollButton = findViewById(R.id.rollButton);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        exitButton = findViewById(R.id.exitButton);
        resultText = findViewById(R.id.resultText);
        diceGrid = findViewById(R.id.diceGrid);
        previousPlayerName = findViewById(R.id.previousPlayerName);
        nextPlayerName = findViewById(R.id.nextPlayerName);

        Object playerData = getIntent().getSerializableExtra("PLAYER_LIST");

        if (playerData instanceof ArrayList<?>) {
            players = (ArrayList<UserData>) playerData;
        }

        numberOfDice = getIntent().getIntExtra("totaldice", 1);
        numberOfDice = Math.max(1, Math.min(4, numberOfDice));

        if (players == null || players.isEmpty()) {
            Toast.makeText(this, "No players found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Center dice in the available grid area.
        diceGrid.setAlignmentMode(GridLayout.ALIGN_MARGINS);

        rollButton.setOnClickListener(v -> rollDice());

        previousButton.setOnClickListener(v -> {
            if (!isRolling && currentPlayerIndex > 0) {
                currentPlayerIndex--;
                showPlayerTurn();
            }
        });

        nextButton.setOnClickListener(v -> {
            if (!isRolling && currentPlayerIndex < players.size() - 1) {
                currentPlayerIndex++;
                showPlayerTurn();
            }
        });

        exitButton.setOnClickListener(v -> finish());

        createDice();
        showPlayerTurn();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources()
                .getDisplayMetrics().density + 0.5f);
    }


    private void createDice() {
        diceGrid.removeAllViews();
        diceViews.clear();
        diceGrid.setColumnCount(2);

        for (int i = 0; i < numberOfDice; i++) {
            DiceView die = new DiceView(this);

            GridLayout.LayoutParams params;

            if (numberOfDice == 1) {
                // One die centered across both columns.
                params = new GridLayout.LayoutParams(
                        GridLayout.spec(0),
                        GridLayout.spec(0, 2)
                );

            } else if (numberOfDice == 3 && i == 2) {
                // Third die centered on the bottom row.
                params = new GridLayout.LayoutParams(
                        GridLayout.spec(1),
                        GridLayout.spec(0, 2)
                );

            } else {
                // Two dice per row.
                int row = i / 2;
                int column = i % 2;

                params = new GridLayout.LayoutParams(
                        GridLayout.spec(row),
                        GridLayout.spec(column)
                );
            }

            params.width = dpToPx(100);
            params.height = dpToPx(100);
            params.setGravity(Gravity.CENTER);
            params.setMargins(
                    dpToPx(6), dpToPx(6),
                    dpToPx(6), dpToPx(6)
            );

            diceGrid.addView(die, params);
            diceViews.add(die);
        }

        // IMPORTANT:
        // Call your existing method here that applies the
        // current player's color and refreshes the dice.
        // Use the actual method name from your project.
    }


    private void showPlayerTurn() {
        if (players == null || players.isEmpty()) {
            return;
        }

        UserData current = players.get(currentPlayerIndex);

        name.setText(current.getName());

        for (DiceView die : diceViews) {
            die.setDieColor(current.getColor());
            die.setNumber(1);
            die.setRotationX(0f);
            die.setRotationY(0f);
        }

        // Do not wrap around: Player 1 has no previous player.
        if (currentPlayerIndex == 0) {
            previousPlayerName.setText("Previous: None");
        } else {
            previousPlayerName.setText(
                    "Previous: " +
                            players.get(currentPlayerIndex - 1).getName());
        }

        // The last player has no next player.
        if (currentPlayerIndex == players.size() - 1) {
            nextPlayerName.setText("Next: None");
        } else {
            nextPlayerName.setText(
                    "Next: " +
                            players.get(currentPlayerIndex + 1).getName());
        }

        previousButton.setEnabled(currentPlayerIndex > 0);
        nextButton.setEnabled(currentPlayerIndex < players.size() - 1);
        rollButton.setEnabled(true);

        resultText.setText("Result: -");
    }

    private void rollDice() {
        if (isRolling || diceViews.isEmpty()) {
            return;
        }

        isRolling = true;
        rollButton.setEnabled(false);
        previousButton.setEnabled(false);
        nextButton.setEnabled(false);

        resultText.setText("Rolling...");

        List<Integer> results = new ArrayList<>();
        List<Animator> animations = new ArrayList<>();

        for (DiceView die : diceViews) {
            results.add(random.nextInt(6) + 1);

            ObjectAnimator rotationX = ObjectAnimator.ofFloat(
                    die, "rotationX", 0f, 360f, 720f);

            ObjectAnimator rotationY = ObjectAnimator.ofFloat(
                    die, "rotationY", 0f, 360f, 720f);

            AnimatorSet animation = new AnimatorSet();
            animation.playTogether(rotationX, rotationY);
            animation.setDuration(900);

            animations.add(animation);
        }

        AnimatorSet allAnimations = new AnimatorSet();
        allAnimations.playTogether(animations);

        allAnimations.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                StringBuilder values = new StringBuilder();
                int total = 0;

                for (int i = 0; i < diceViews.size(); i++) {
                    int value = results.get(i);
                    diceViews.get(i).setNumber(value);

                    if (i > 0) {
                        values.append(", ");
                    }

                    values.append(value);
                    total += value;
                }

                resultText.setText(
                        "You rolled: " + values + "\nTotal: " + total);

                isRolling = false;
                previousButton.setEnabled(currentPlayerIndex > 0);
                nextButton.setEnabled(
                        currentPlayerIndex < players.size() - 1);
                rollButton.setEnabled(true);
            }
        });

        allAnimations.start();
    }
}
