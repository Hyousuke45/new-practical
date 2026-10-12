package com.example.mad3new;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mad3new.db";

    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {

        super(
                context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Dice history
        String diceQuery =
                "CREATE TABLE roll_history (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "result INTEGER, " +
                        "roll_date TEXT)";

        db.execSQL(diceQuery);

        // BMI history
        String bmiQuery =
                "CREATE TABLE bmi_history (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "weight REAL, " +
                        "height REAL, " +
                        "bmi REAL, " +
                        "category TEXT, " +
                        "calculation_date TEXT)";

        db.execSQL(bmiQuery);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        db.execSQL(
                "DROP TABLE IF EXISTS roll_history"
        );

        db.execSQL(
                "DROP TABLE IF EXISTS bmi_history"
        );

        onCreate(db);
    }

    // =========================
    // DICE
    // =========================

    public void saveRoll(
            int result,
            String date) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put("result", result);
        values.put("roll_date", date);

        db.insert(
                "roll_history",
                null,
                values
        );

        db.close();
    }

    public Cursor getRollHistory() {

        SQLiteDatabase db =
                getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM roll_history " +
                        "ORDER BY id DESC",
                null
        );
    }

    // =========================
    // BMI
    // =========================

    public void saveBMI(
            double weight,
            double height,
            double bmi,
            String category,
            String date) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put("weight", weight);
        values.put("height", height);
        values.put("bmi", bmi);
        values.put("category", category);
        values.put("calculation_date", date);

        db.insert(
                "bmi_history",
                null,
                values
        );

        db.close();
    }

    public Cursor getBMIHistory() {

        SQLiteDatabase db =
                getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM bmi_history " +
                        "ORDER BY id DESC",
                null
        );
    }
}