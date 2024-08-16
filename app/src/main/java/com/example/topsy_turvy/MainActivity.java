package com.example.topsy_turvy;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    View system_ui_view;
    MaterialButton login_home, signup_home, back_icon_home;
    AppCompatButton login_signup_button;
    TextInputEditText username_extract, password_extract, confirm_password_extract;
    TextInputLayout username_layout, password_layout, confirm_password_layout;
    String username_extracted, password_extracted, confirm_password_extracted, authenticate_mode;
    MediaPlayer button_click_sound, first_open_sound;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login_home = findViewById(R.id.login_home);
        signup_home = findViewById(R.id.signup_home);
        back_icon_home = findViewById(R.id.home_back_icon);
        login_signup_button = findViewById(R.id.login_signUp_button);
        username_extract = findViewById(R.id.name_extract_text);
        password_extract = findViewById(R.id.password_extract_text);
        confirm_password_extract = findViewById(R.id.signup_password_confirm_extract_text);
        username_layout = findViewById(R.id.name_edit_layout);
        password_layout = findViewById(R.id.password_edit_layout);
        confirm_password_layout = findViewById(R.id.signup_password_confirm_edit_layout);
        button_click_sound = MediaPlayer.create(this, R.raw.button_click_sound);
        first_open_sound = MediaPlayer.create(this, R.raw.landing_page_sound_cut);
        databaseHelper = DatabaseHelper.getDB(this);
        login_home.setOnClickListener(this);
        signup_home.setOnClickListener(this);
        back_icon_home.setOnClickListener(this);
        login_signup_button.setOnClickListener(this);
        baseValue();
    }

    @Override
    public void onClick(View v) {
        button_click_sound.start();
        if (v == login_home) {
            newClickValue();
            confirm_password_layout.setVisibility(View.GONE);
            login_signup_button.setText(R.string.login);
            authenticate_mode = "login";
        } else if (v == signup_home) {
            newClickValue();
            confirm_password_layout.setVisibility(View.VISIBLE);
            login_signup_button.setText(R.string.signup);
            authenticate_mode = "signUp";
        } else if (v == login_signup_button) {
            username_extracted = Objects.requireNonNull(username_extract.getText()).toString();
            password_extracted = Objects.requireNonNull(password_extract.getText()).toString();
            confirm_password_extracted = Objects.requireNonNull(confirm_password_extract.getText()).toString();
            if (authenticate_mode.equals("login")) {
                if (username_extract.getText().toString().isEmpty() || password_extract.getText().toString().isEmpty()) {
                    customToast();
                } else if (!username_extract.getText().toString().isEmpty() && !password_extract.getText().toString().isEmpty()) {
                    if (databaseHelper.modelDAO().isAuthenticationSuccessful(username_extracted, password_extracted)) {
                        sharedPreferenceCall();
                        Intent intent = new Intent(this, ModeSelectionPage.class);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        errorDialog("⚠️ Incorrect Username or Password!", "Login");
                    }
                }
            } else if (authenticate_mode.equals("signUp")) {
                if (username_extract.getText().toString().isEmpty() || password_extract.getText().toString().isEmpty()) {
                    customToast();
                } else if (!username_extract.getText().toString().isEmpty() && !password_extract.getText().toString().isEmpty()) {
                    if (password_extracted.equals(confirm_password_extracted)) {
                        if (databaseHelper.modelDAO().isAuthenticationSuccessful(username_extracted, password_extracted)) {
                            errorDialog("⚠️ Username Already Exists!", "SignUp");
                        } else {
                            databaseHelper.modelDAO().addDetails(
                                    new UserModel(username_extracted, password_extracted, 0, 0, 0, 0)
                            );
                            sharedPreferenceCall();
                            Intent signUp_intent = new Intent(this, ModeSelectionPage.class);
                            startActivity(signUp_intent);
                            finishAffinity();
                        }
                    } else {
                        confirm_password_extract.setError("Password doesn't match!");
                        confirm_password_layout.setEndIconMode(TextInputLayout.END_ICON_NONE);
                        new Handler().postDelayed(() -> {
                            confirm_password_extract.setError(null);
                            confirm_password_layout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                        }, 2000);
                    }
                }
            }
        } else if (v == back_icon_home)
            baseValue();
    }

    private void sharedPreferenceCall() {
        SharedPreferences preferences = getSharedPreferences("loggedUser", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (!preferences.contains("User")) {
            editor.putString("User", username_extracted);
            editor.apply();
        }
        SharedPreferences authentication_flag = getSharedPreferences("authenticator", MODE_PRIVATE);
        SharedPreferences.Editor authentication_editor = authentication_flag.edit();
        authentication_editor.putBoolean("authenticate", true);
        authentication_editor.apply();
    }

    private void customToast() {
        Toast toast = new Toast(getApplicationContext());
        View view = getLayoutInflater().inflate(R.layout.custom_toast_layout_1, findViewById(R.id.custom_toast_layout_1));
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    private void errorDialog(String error_text, String error_source) {
        Dialog error_dialog = new Dialog(this);
        error_dialog.setContentView(R.layout.login_signup_error_dialog);
        error_dialog.setCancelable(false);
        TextView error_message = error_dialog.findViewById(R.id.error_heading);
        error_message.setText(error_text);
        MaterialButton error_action_button = error_dialog.findViewById(R.id.error_action_button);
        MaterialButton error_dialog_back_button = error_dialog.findViewById(R.id.error_dialog_back_button);
        if (error_source.equals("Login")) {
            error_action_button.setText(R.string.signup);
            error_action_button.setOnClickListener(v -> {
                button_click_sound.start();
                newClickValue();
                confirm_password_layout.setVisibility(View.VISIBLE);
                login_signup_button.setText(R.string.signup);
                authenticate_mode = "signUp";
                error_dialog.dismiss();
            });
        } else if (error_source.equals("SignUp")) {
            error_action_button.setText(R.string.login);
            error_action_button.setOnClickListener(v -> {
                button_click_sound.start();
                newClickValue();
                confirm_password_layout.setVisibility(View.GONE);
                login_signup_button.setText(R.string.login);
                authenticate_mode = "login";
                error_dialog.dismiss();
            });
        }
        error_dialog_back_button.setOnClickListener(v -> {
            button_click_sound.start();
            error_dialog.dismiss();
        });
        Objects.requireNonNull(error_dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        error_dialog.show();
    }

    private void newClickValue() {
        login_home.setVisibility(View.GONE);
        signup_home.setVisibility(View.GONE);
        username_layout.setVisibility(View.VISIBLE);
        password_layout.setVisibility(View.VISIBLE);
        back_icon_home.setVisibility(View.VISIBLE);
        login_signup_button.setVisibility(View.VISIBLE);
    }

    private void baseValue() {
        login_home.setVisibility(View.VISIBLE);
        signup_home.setVisibility(View.VISIBLE);
        login_signup_button.setVisibility(View.GONE);
        username_layout.setVisibility(View.GONE);
        password_layout.setVisibility(View.GONE);
        confirm_password_layout.setVisibility(View.GONE);
        back_icon_home.setVisibility(View.GONE);
    }

    @Override
    protected void onPostResume() {
        system_ui_view = findViewById(R.id.imageView);
        system_ui_view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        first_open_sound.start();
        super.onPostResume();
    }
}