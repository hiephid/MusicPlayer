package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicplayer.User.LoginActivity;

public class Bg extends AppCompatActivity {
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bg);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Bg.this, LoginActivity.class);
                    startActivity(intent);
                    finish();


                }
            },5000);

    }
}