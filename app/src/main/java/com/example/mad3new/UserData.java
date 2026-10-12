package com.example.mad3new;
import java.io.Serializable;

public class UserData implements Serializable {
    private String name;
    private int color;
    private String id;

    public UserData(String name, int color, String id) {
        this.name = name;
        this.color = color;
        this.id = id;
    }

    // Getters
    public String getName() { return name; }
    public int getColor() { return color; }
    public String getId() { return id; }
}
