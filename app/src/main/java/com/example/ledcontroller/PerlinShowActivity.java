package com.example.ledcontroller;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class PerlinShowActivity extends AppCompatActivity {
    private static HashMap<String, int[]> profiles = new HashMap<>();
    private static ArrayList<String> profilesNames = new ArrayList<>();
    public static String currProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perlin_show);
    }
    public static void setProfileSettings(String profileName, byte[] settings){

    }
}