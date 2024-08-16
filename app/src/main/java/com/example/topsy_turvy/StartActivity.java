package com.example.topsy_turvy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    SurfaceView surfaceView;
    MediaPlayer mediaPlayer, intro_song;
    View system_ui_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        surfaceView = findViewById(R.id.intro_animation_view);
        surfaceView.setKeepScreenOn(true);
        intro_song = MediaPlayer.create(this, R.raw.intro_sound);
        intro_song.start();
        mediaPlayer = MediaPlayer.create(this, R.raw.developer_intro_animation_1);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                mediaPlayer.setDisplay(surfaceHolder);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });
        mediaPlayer.start();
        SharedPreferences authentication_flag = getSharedPreferences("authenticator", MODE_PRIVATE);
        boolean flag_value = authentication_flag.getBoolean("authenticate", false);
        Intent intent;
        if (flag_value) {
            intent = new Intent(this, ModeSelectionPage.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 4200);
    }

    @Override
    protected void onPause() {
        if (intro_song.isPlaying())
            intro_song.pause();
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        system_ui_view = findViewById(R.id.constraint_layout_splash_screen);
        system_ui_view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        intro_song.start();
        super.onPostResume();
    }
}