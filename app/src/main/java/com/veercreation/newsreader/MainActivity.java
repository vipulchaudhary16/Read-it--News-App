package com.veercreation.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timer timer = new Timer("Timer");;
        timer.schedule(task , 1400L);

        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    TimerTask task = new TimerTask() {
        public void run() {
            Intent intent = new Intent(getApplicationContext() , NewsActivity.class);
            startActivity(intent);
            finish();
        }
    };
}