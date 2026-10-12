
package com.example.mad3new;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class dicepanel extends AppCompatActivity {

    private Button goButton;
    private Spinner diceSpinnerPlayer, diceSpinnerDice;
    private LinearLayout playerSelection;

    private final Random random = new Random();
    private final List<View> rowViewList = new ArrayList<>();

    private int numberOfDice = 1;
    private int numberOfPlayer = 1;

    private final String[] randomNames = {
            "Alex", "Blake", "Casey", "Drew", "Elliot"
    };

    private final int[] randomColors = {
            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.rgb(255, 140, 0),
            Color.MAGENTA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dicepanel);

        diceSpinnerPlayer = findViewById(R.id.diceSpinnerPlayer);
        diceSpinnerDice = findViewById(R.id.diceSpinnerDice);
        playerSelection = findViewById(R.id.playerSelection);
        goButton = findViewById(R.id.goButton);

        String[] diceOptions = {"1", "2", "3", "4"};
        String[] playerOptions = {"1", "2", "3", "4", "5"};

        diceSpinnerDice.setAdapter(createSpinnerAdapter(diceOptions));
        diceSpinnerPlayer.setAdapter(createSpinnerAdapter(playerOptions));

        diceSpinnerDice.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent, View view,
                            int position, long id) {
                        numberOfDice = position + 1;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        diceSpinnerPlayer.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent, View view,
                            int position, long id) {
                        numberOfPlayer = position + 1;
                        generateFormFields(numberOfPlayer);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        addRandomButton();

        goButton.setOnClickListener(v -> goAction());
    }

    private ArrayAdapter<String> createSpinnerAdapter(String[] options) {
        return new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, options) {

            @Override
            public View getView(
                    int position, View convertView, ViewGroup parent) {
                TextView text = (TextView) super.getView(
                        position, convertView, parent);
                text.setTextColor(Color.BLACK);
                text.setTextSize(16);
                text.setGravity(Gravity.CENTER_VERTICAL);
                return text;
            }

            @Override
            public View getDropDownView(
                    int position, View convertView, ViewGroup parent) {
                TextView text = (TextView) super.getDropDownView(
                        position, convertView, parent);
                text.setTextColor(Color.BLACK);
                text.setBackgroundColor(Color.WHITE);
                text.setTextSize(16);
                text.setPadding(24, 24, 24, 24);
                return text;
            }
        };
    }

    private void addRandomButton() {
        ViewGroup parent = (ViewGroup) goButton.getParent();

        Button randomButton = new Button(this);
        randomButton.setText("Randomize Players");
        randomButton.setTextColor(Color.WHITE);
        randomButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(103, 80, 164)));

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        parent.addView(randomButton,
                parent.indexOfChild(goButton), params);

        randomButton.setOnClickListener(v -> randomizePlayers());
    }

    private void generateFormFields(int numberOfPlayers) {
        playerSelection.removeAllViews();
        rowViewList.clear();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < numberOfPlayers; i++) {
            // Inflate your existing row_input.xml.
            View row = inflater.inflate(
                    R.layout.row_input,
                    playerSelection,
                    false
            );

            TextView playerId = row.findViewById(R.id.playerId);
            EditText nameInput = row.findViewById(R.id.et_name);
            Button colorButton = row.findViewById(R.id.btn_pick_color);

            playerId.setText("Player " + (i + 1));

            // Give each player a different initial color.
            int[] colors = {
                    android.graphics.Color.RED,
                    android.graphics.Color.BLUE,
                    android.graphics.Color.GREEN,
                    android.graphics.Color.YELLOW,
                    android.graphics.Color.MAGENTA
            };

            int selectedColor = colors[i % colors.length];
            colorButton.setTag(selectedColor);
            colorButton.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(selectedColor)
            );

            // Open your existing color-picker method here.
            final int playerIndex = i;
            colorButton.setOnClickListener(v ->
                    showColorWheel(colorButton, playerIndex)
            );

            playerSelection.addView(row);
            rowViewList.add(row);
        }
    }


    private void showColorWheel(Button button, int playerIndex) {
        ColorWheelView wheel = new ColorWheelView(this);

        int padding = dpToPx(16);
        LinearLayout container = new LinearLayout(this);
        container.setPadding(padding, padding, padding, padding);
        container.setGravity(Gravity.CENTER);
        container.addView(wheel, new LinearLayout.LayoutParams(
                dpToPx(260), dpToPx(260)));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Choose a Color")
                .setView(container)
                .setPositiveButton("Use Color", (d, which) -> {
                    int selected = wheel.getSelectedColor();

                    if (isColorUsedByAnotherPlayer(selected, playerIndex)) {
                        Toast.makeText(this,
                                "That color is already taken. Choose another.",
                                Toast.LENGTH_LONG).show();
                        showColorWheel(button, playerIndex);
                        return;
                    }

                    button.setTag(selected);
                    button.setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(selected));
                })
                .setNegativeButton("Cancel", null)
                .create();

        wheel.setOnColorSelectedListener(color ->
                button.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(color)));

        dialog.show();
    }

    private boolean isColorUsedByAnotherPlayer(
            int color, int currentIndex) {
        for (int i = 0; i < rowViewList.size(); i++) {
            if (i == currentIndex) {
                continue;
            }

            Button otherButton =
                    rowViewList.get(i).findViewWithTag(null);

            // Locate the color button by scanning the row's children.
            View row = rowViewList.get(i);
            if (row instanceof LinearLayout) {
                LinearLayout layout = (LinearLayout) row;
                for (int j = 0; j < layout.getChildCount(); j++) {
                    View child = layout.getChildAt(j);
                    if (child instanceof Button) {
                        Object tag = child.getTag();
                        if (tag instanceof Integer &&
                                ((Integer) tag) == color) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void randomizePlayers() {
        ArrayList<String> names = new ArrayList<>();
        for (String name : randomNames) {
            names.add(name);
        }

        // Shuffle so each player receives a different random name.
        java.util.Collections.shuffle(names, random);

        for (int i = 0; i < rowViewList.size(); i++) {
            LinearLayout row = (LinearLayout) rowViewList.get(i);

            EditText nameInput = null;
            Button colorButton = null;

            for (int j = 0; j < row.getChildCount(); j++) {
                View child = row.getChildAt(j);

                if (child instanceof EditText) {
                    nameInput = (EditText) child;
                } else if (child instanceof Button) {
                    colorButton = (Button) child;
                }
            }

            if (nameInput != null) {
                nameInput.setText(names.get(i));
            }

            if (colorButton != null) {
                int color = randomColors[i];
                colorButton.setTag(color);
                colorButton.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(color));
            }
        }

        Toast.makeText(this, "Players randomized!",
                Toast.LENGTH_SHORT).show();
    }

    private void goAction() {
        ArrayList<UserData> savedPlayers = new ArrayList<>();
        Set<String> names = new HashSet<>();
        Set<Integer> colors = new HashSet<>();

        for (int i = 0; i < rowViewList.size(); i++) {
            LinearLayout row = (LinearLayout) rowViewList.get(i);

            EditText etName = null;
            Button colorButton = null;

            for (int j = 0; j < row.getChildCount(); j++) {
                View child = row.getChildAt(j);

                if (child instanceof EditText) {
                    etName = (EditText) child;
                } else if (child instanceof Button) {
                    colorButton = (Button) child;
                }
            }

            if (etName == null || colorButton == null) {
                continue;
            }

            String playerName = etName.getText().toString().trim();

            if (playerName.isEmpty()) {
                etName.setError("Enter a player name");
                etName.requestFocus();
                return;
            }

            String normalized = playerName.toLowerCase(Locale.ROOT);
            if (!names.add(normalized)) {
                etName.setError("Name already used");
                etName.requestFocus();
                Toast.makeText(this,
                        "Player names must be unique.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Object tag = colorButton.getTag();
            int color = tag instanceof Integer
                    ? (Integer) tag : randomColors[i];

            if (!colors.add(color)) {
                Toast.makeText(this,
                        "Each player needs a different color.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            savedPlayers.add(new UserData(
                    playerName, color, String.valueOf(i + 1)));
        }

        if (savedPlayers.isEmpty()) {
            Toast.makeText(this, "Add at least one player.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(dicepanel.this, Act4Activity.class);
        intent.putExtra("PLAYER_LIST", savedPlayers);
        intent.putExtra("totaldice", numberOfDice);
        startActivity(intent);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    // Simple touch-based hue wheel.
    private static class ColorWheelView extends View {

        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int[] colors = {
                Color.RED, Color.MAGENTA, Color.BLUE,
                Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED
        };

        private int selectedColor = Color.RED;
        private float centerX, centerY, radius;
        private OnColorSelectedListener listener;

        interface OnColorSelectedListener {
            void onColorSelected(int color);
        }

        ColorWheelView(android.content.Context context) {
            super(context);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        void setOnColorSelectedListener(OnColorSelectedListener listener) {
            this.listener = listener;
        }

        int getSelectedColor() {
            return selectedColor;
        }





        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            centerX = getWidth() / 2f;
            centerY = getHeight() / 2f;
            radius = Math.min(centerX, centerY) * 0.88f;

            paint.setShader(new SweepGradient(
                    centerX, centerY, colors, null));
            canvas.drawCircle(centerX, centerY, radius, paint);
            paint.setShader(null);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3f);
            paint.setColor(Color.DKGRAY);
            canvas.drawCircle(centerX, centerY, radius, paint);
            paint.setStyle(Paint.Style.FILL);

            float[] hsv = new float[3];
            Color.colorToHSV(selectedColor, hsv);

            double angle = Math.toRadians(hsv[0]);
            float markerX = centerX +
                    (float) Math.cos(angle) * radius * 0.72f;
            float markerY = centerY +
                    (float) Math.sin(angle) * radius * 0.72f;

            paint.setColor(Color.WHITE);
            canvas.drawCircle(markerX, markerY, 13f, paint);

            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3f);
            canvas.drawCircle(markerX, markerY, 13f, paint);
            paint.setStyle(Paint.Style.FILL);
        }



        @Override
        public boolean onTouchEvent(android.view.MotionEvent event) {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN ||
                    event.getAction() == android.view.MotionEvent.ACTION_MOVE ||
                    event.getAction() == android.view.MotionEvent.ACTION_UP) {

                float dx = event.getX() - centerX;
                float dy = event.getY() - centerY;

                float angle = (float) Math.atan2(dy, dx);
                float hue = (float) Math.toDegrees(angle);
                if (hue < 0) {
                    hue += 360f;
                }

                selectedColor = Color.HSVToColor(
                        new float[]{hue, 1f, 1f});

                if (listener != null) {
                    listener.onColorSelected(selectedColor);
                }

                invalidate();
                return true;
            }

            return true;
        }
    }
}
