package com.example.topsy_turvy;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {

    MaterialButton delete_account_button, edit_profile_button, reset_account_button, logout_button, back_button;
    MediaPlayer button_clicked_sound;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    BiometricManager biometricManager;
    ArrayList<UserModel> logged_user_details = new ArrayList<>();
    DatabaseHelper databaseHelper;
    String logged_username;
    TextView match_played_textview, game_won_textview, total_score_textview, personal_best_textview, username_textview;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        button_clicked_sound = MediaPlayer.create(this, R.raw.button_click_sound);
        delete_account_button = findViewById(R.id.profile_delete_button);
        edit_profile_button = findViewById(R.id.profile_edit_button);
        reset_account_button = findViewById(R.id.profile_reset_progress_button);
        logout_button = findViewById(R.id.profile_logout_button);
        back_button = findViewById(R.id.back_to_home_page_Profile_page_button);
        match_played_textview = findViewById(R.id.match_played_text);
        game_won_textview = findViewById(R.id.match_won_text);
        total_score_textview = findViewById(R.id.point_scored_total_text);
        personal_best_textview = findViewById(R.id.personal_best_text);
        username_textview = findViewById(R.id.profile_username);
        back_button.setOnClickListener(this);
        logout_button.setOnClickListener(this);
        reset_account_button.setOnClickListener(this);
        delete_account_button.setOnClickListener(this);
        edit_profile_button.setOnClickListener(this);
        SharedPreferences preferences = getSharedPreferences("loggedUser", MODE_PRIVATE);
        logged_username = preferences.getString("User", "");
        databaseHelper = DatabaseHelper.getDB(this);
        logged_user_details = (ArrayList<UserModel>) databaseHelper.modelDAO().getUserDetail(logged_username);
        username_textview.setText(logged_user_details.get(0).getUser_name());
        playerProgressSet();
    }

    private void playerProgressSet() {
        match_played_textview.setText(String.valueOf(logged_user_details.get(0).getGame_played()));
        game_won_textview.setText(String.valueOf(logged_user_details.get(0).getGame_won()));
        total_score_textview.setText(String.valueOf(logged_user_details.get(0).getTotal_points_scored()));
        personal_best_textview.setText(String.valueOf(logged_user_details.get(0).getPersonal_best()));
        personal_best_textview.setText(MessageFormat.format("{0} turns", String.valueOf(logged_user_details.get(0).getPersonal_best())));
    }

    private void delete_resetDialog(String heading, String sub_heading) {
        Dialog delete_reset_dialog = new Dialog(this);
        delete_reset_dialog.setContentView(R.layout.new_game_exit_confirmation_dialog);
        LinearLayoutCompat dialog_background_layout = delete_reset_dialog.findViewById(R.id.new_exit_dialog_linear_layout);
        dialog_background_layout.setBackgroundColor(getColor(R.color.tree_leaves_night));
        TextView heading_textview = delete_reset_dialog.findViewById(R.id.new_game_exit_heading);
        TextView sub_heading_textview = delete_reset_dialog.findViewById(R.id.dialog_sub_text);
        heading_textview.setText(heading);
        sub_heading_textview.setText(sub_heading);
        delete_reset_dialog.setCancelable(false);
        MaterialButton dialog_yes_button = delete_reset_dialog.findViewById(R.id.new_game_exit_yes_button);
        MaterialButton dialog_back_button = delete_reset_dialog.findViewById(R.id.new_game_exit_back_button);
        dialog_yes_button.setOnClickListener(v -> {
            button_clicked_sound.start();
            if (heading.equals("Reset Progress?")) {
                UserModel reset_model = new UserModel(logged_user_details.get(0).getUser_name(), logged_user_details.get(0).getUser_password(),
                        0, 0, 0, 0);
                databaseHelper.modelDAO().updateDetails(reset_model);
                logged_user_details = (ArrayList<UserModel>) databaseHelper.modelDAO().getUserDetail(logged_username);
                playerProgressSet();
                delete_reset_dialog.dismiss();
            } else if (heading.equals("Delete Account?")) {
                databaseHelper.modelDAO().deleteUser(logged_username);
                shardPreferenceLogoutValues();
                Intent delete_intent = new Intent(this, MainActivity.class);
                delete_reset_dialog.dismiss();
                startActivity(delete_intent);
                finishAffinity();
            }
        });
        dialog_back_button.setOnClickListener(v -> {
            button_clicked_sound.start();
            delete_reset_dialog.dismiss();
        });
        Objects.requireNonNull(delete_reset_dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        delete_reset_dialog.show();
    }

    private void shardPreferenceLogoutValues() {
        SharedPreferences preferences = getSharedPreferences("loggedUser", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("User");
        editor.apply();
        SharedPreferences authentication_flag = getSharedPreferences("authenticator", MODE_PRIVATE);
        SharedPreferences.Editor authentication_editor = authentication_flag.edit();
        authentication_editor.putBoolean("authenticate", false);
        authentication_editor.apply();
    }

    private void userProfileToast(String toast_message) {
        Toast toast = new Toast(getApplicationContext());
        View view = getLayoutInflater().inflate(R.layout.custom_toast_layout_1, findViewById(R.id.custom_toast_layout_1));
        TextView toast_message_display = view.findViewById(R.id.toast_textview);
        toast_message_display.setText(toast_message);
        toast_message_display.setTextColor(getColor(R.color.mutated_yellow_1));
        LinearLayoutCompat toast_layout_1 = view.findViewById(R.id.custom_toast_layout_1);
        toast_layout_1.setBackgroundColor(getColor(R.color.wood_brown));
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    private void editProfileDialog() {
        Dialog edit_profile_dialog = new Dialog(this);
        edit_profile_dialog.setContentView(R.layout.profile_edit_dialog);
        edit_profile_dialog.setCancelable(false);
        MaterialButton edit_ok_button = edit_profile_dialog.findViewById(R.id.profile_edit_dialog_ok_button);
        MaterialButton edit_cancel_button = edit_profile_dialog.findViewById(R.id.profile_edit_dialog_cancel_button);
        TextInputEditText username_edit = edit_profile_dialog.findViewById(R.id.profile_edit_dialog_name_extract_text);
        TextInputEditText password_edit = edit_profile_dialog.findViewById(R.id.profile_edit_dialog_password_extract_text);
        username_edit.setText(logged_user_details.get(0).getUser_name());
        password_edit.setText(logged_user_details.get(0).getUser_password());
        edit_ok_button.setOnClickListener(v -> {
            button_clicked_sound.start();
            String edited_username = Objects.requireNonNull(username_edit.getText()).toString();
            String edited_password = Objects.requireNonNull(password_edit.getText()).toString();
            if (username_edit.getText().toString().isEmpty() || password_edit.getText().toString().isEmpty()) {
                userProfileToast("Please fill all data!");
            } else if (edited_username.equals(logged_user_details.get(0).getUser_name())) {
                databaseHelper.modelDAO().updateDetails(new UserModel(edited_username, edited_password, logged_user_details.get(0).getGame_played(),
                        logged_user_details.get(0).getGame_won(), logged_user_details.get(0).getTotal_points_scored(),
                        logged_user_details.get(0).getPersonal_best()));
                logged_user_details = (ArrayList<UserModel>) databaseHelper.modelDAO().getUserDetail(logged_username);
                edit_profile_dialog.dismiss();
            } else {
                databaseHelper.modelDAO().addDetails(new UserModel(edited_username, edited_password, logged_user_details.get(0).getGame_played(),
                        logged_user_details.get(0).getGame_won(), logged_user_details.get(0).getTotal_points_scored(),
                        logged_user_details.get(0).getPersonal_best()));
                databaseHelper.modelDAO().deleteUser(logged_username);
                SharedPreferences preferences = getSharedPreferences("loggedUser", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("User", edited_username);
                editor.apply();
                logged_username = edited_username;
                logged_user_details = (ArrayList<UserModel>) databaseHelper.modelDAO().getUserDetail(logged_username);
                username_textview.setText(logged_user_details.get(0).getUser_name());
                edit_profile_dialog.dismiss();
            }
        });
        edit_cancel_button.setOnClickListener(v -> {
            button_clicked_sound.start();
            edit_profile_dialog.dismiss();
        });
        Objects.requireNonNull(edit_profile_dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        edit_profile_dialog.show();
    }

    @Override
    public void onClick(View v) {
        button_clicked_sound.start();
        if (v == back_button) {
            Intent back_intent = new Intent(this, ModeSelectionPage.class);
            startActivity(back_intent);
            finishAffinity();
        } else if (v == logout_button) {
            shardPreferenceLogoutValues();
            Intent logout_intent = new Intent(this, MainActivity.class);
            startActivity(logout_intent);
            finishAffinity();
        } else if (v == reset_account_button) {
            delete_resetDialog("Reset Progress?", "All stats will be lost!");
        } else if (v == delete_account_button) {
            biometricHandler("DELETE");
        } else if (v == edit_profile_button) {
            biometricHandler("EDIT_PROFILE");
        }
    }
    private void biometricHandler(String button_type){
        biometricManager = BiometricManager.from(getApplicationContext());
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                userProfileToast("Device doesn't have fingerprint sensor!");
                callDialog(button_type);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                userProfileToast("Device fingerprint sensor is not working!");
                callDialog(button_type);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                userProfileToast("No fingerprint assigned!");
                callDialog(button_type);
                break;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                userProfileToast("Security update required!");
                callDialog(button_type);
                break;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                userProfileToast("Fingerprint is unsupported!");
                callDialog(button_type);
                break;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                userProfileToast("Fingerprint status unknown!");
                callDialog(button_type);
                break;
            case BiometricManager.BIOMETRIC_SUCCESS:
                Executor executor = ContextCompat.getMainExecutor(this);
                biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        callDialog(button_type);
                    }
                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }
                });
                promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Biometric Authentication")
                        .setSubtitle("Verify yourself to proceed for editing of your game profile!").setNegativeButtonText("Cancel")
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG).build();
                biometricPrompt.authenticate(promptInfo);
                break;
        }
    }
    private void callDialog(String button_type){
        if (button_type.equals("EDIT_PROFILE"))
            editProfileDialog();
        else if (button_type.equals("DELETE"))
            delete_resetDialog("Delete Account?", "Your account will be permanently deleted!");
    }
}