package com.example.topsy_turvy;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class ModeSelectionPage extends AppCompatActivity implements View.OnClickListener {
    SurfaceView base_video;
    View close_navigation_ui;
    MediaPlayer surface_video, base_bg, rain_effect, button_click_sound, card_click_sound;
    MaterialButton play_with_ai, multiplayer, home_play_button;
    TextView menu_heading,loading_text;
    CardView ancient_cardView_menu, modern_light_cardView_menu, modern_heavy_cardView_menu, overall_menu_cardView;
    ExtendedFloatingActionButton info_action, exit_action, profile_action, menu_action;
    SpannableString underlined_mode_choice;
    String mode_choice, card_selected = "", guest_user;
    Animation rotate_open, rotate_close, raise_from_bottom, collapse_to_bottom, raise_from_side, click_scale;
    ConstraintLayout ancient_mode_select_layout, modern_light_select_layout, modern_heavy_select_layout;
    Boolean menu_opened = false, mode_selected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_selection_page);
        base_video = findViewById(R.id.base_mode_video);
        base_video.setKeepScreenOn(true);
        base_bg = MediaPlayer.create(this, R.raw.main_page_bg_music);
        rain_effect = MediaPlayer.create(this, R.raw.rain_thunder_sound_effect);
        button_click_sound = MediaPlayer.create(this, R.raw.button_click_sound);
        card_click_sound = MediaPlayer.create(this, R.raw.card_click_sound);
        play_with_ai = findViewById(R.id.play_ai_with_button);
        multiplayer = findViewById(R.id.multiplayer_button);
        info_action = findViewById(R.id.info_floating_icon);
        exit_action = findViewById(R.id.exit_floating_icon);
        profile_action = findViewById(R.id.profile_floating_icon);
        menu_action = findViewById(R.id.extended_base_menu_button);
        menu_heading = findViewById(R.id.menu_heading_textview);
        ancient_cardView_menu = findViewById(R.id.ancient_mode_cardView);
        modern_light_cardView_menu = findViewById(R.id.modern_light_mode_cardView);
        modern_heavy_cardView_menu = findViewById(R.id.modern_heavy_mode_cardView);
        overall_menu_cardView = findViewById(R.id.menu_cardView);
        home_play_button = findViewById(R.id.homepage_play_button);
        ancient_mode_select_layout = findViewById(R.id.ancient_mode_selection_layout);
        modern_light_select_layout = findViewById(R.id.modern_mode_light_selection_layout);
        modern_heavy_select_layout = findViewById(R.id.modern_mode_heavy_selection_layout);
        loading_text=findViewById(R.id.loading_text);
        rotate_open = AnimationUtils.loadAnimation(this, R.anim.rotate_open_animation);
        rotate_close = AnimationUtils.loadAnimation(this, R.anim.rotate_close_animation);
        raise_from_bottom = AnimationUtils.loadAnimation(this, R.anim.raise_from_bottom_animation);
        collapse_to_bottom = AnimationUtils.loadAnimation(this, R.anim.collapse_to_bottom_animation);
        raise_from_side = AnimationUtils.loadAnimation(this, R.anim.raise_from_side_animation);
        click_scale = AnimationUtils.loadAnimation(this, R.anim.click_scale_animation);
        play_with_ai.setOnClickListener(this);
        multiplayer.setOnClickListener(this);
        info_action.setOnClickListener(this);
        exit_action.setOnClickListener(this);
        profile_action.setOnClickListener(this);
        menu_action.setOnClickListener(this);
        ancient_cardView_menu.setOnClickListener(this);
        modern_light_cardView_menu.setOnClickListener(this);
        modern_heavy_cardView_menu.setOnClickListener(this);
        home_play_button.setOnClickListener(this);
        base_bg.start();
        base_bg.setLooping(true);
        rain_effect.start();
        rain_effect.setLooping(true);
        surface_video = MediaPlayer.create(this, R.raw.homepage_template_video);
        SurfaceHolder surfaceHolder = base_video.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                surface_video.setDisplay(surfaceHolder);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });
        surface_video.start();
        surface_video.setLooping(true);
    }

    void menuCloseOption() {
        exit_action.startAnimation(collapse_to_bottom);
        info_action.startAnimation(collapse_to_bottom);
        profile_action.startAnimation(collapse_to_bottom);
        menu_action.startAnimation(rotate_close);
        exit_action.setVisibility(View.GONE);
        info_action.setVisibility(View.GONE);
        profile_action.setVisibility(View.GONE);
        exit_action.setClickable(false);
        info_action.setClickable(false);
        profile_action.setClickable(false);
        menu_opened = false;
    }

    void menuOpenOption() {
        exit_action.startAnimation(raise_from_bottom);
        info_action.startAnimation(raise_from_bottom);
        profile_action.startAnimation(raise_from_bottom);
        menu_action.startAnimation(rotate_open);
        exit_action.setVisibility(View.VISIBLE);
        info_action.setVisibility(View.VISIBLE);
        profile_action.setVisibility(View.VISIBLE);
        exit_action.setClickable(true);
        info_action.setClickable(true);
        profile_action.setClickable(true);
        menu_opened = true;
    }

    private void infoDialogBox() {
        Dialog info_dialog = new Dialog(ModeSelectionPage.this);
        info_dialog.setContentView(R.layout.info_dialog_layout);
        MaterialButton close_info_dialog = info_dialog.findViewById(R.id.cancel_info_dialog);
        close_info_dialog.setOnClickListener(v -> {
            button_click_sound.start();
            info_dialog.dismiss();
        });
        info_dialog.setCancelable(false);
        Objects.requireNonNull(info_dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        info_dialog.show();
    }

    private void guestUserDialogBox() {
        Dialog guest_user_dialog = new Dialog(ModeSelectionPage.this);
        guest_user_dialog.setContentView(R.layout.guest_player_dialog);
        guest_user_dialog.setCancelable(false);
        Objects.requireNonNull(guest_user_dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MaterialButton ok_button = guest_user_dialog.findViewById(R.id.guest_dialog_ok_button);
        MaterialButton cancel_button = guest_user_dialog.findViewById(R.id.guest_dialog_cancel_button);
        cancel_button.setOnClickListener(v -> {
            button_click_sound.start();
            guest_user_dialog.dismiss();
        });
        ok_button.setOnClickListener(v -> {
            button_click_sound.start();
            TextInputEditText guest_username = guest_user_dialog.findViewById(R.id.guest_name_dialog_extract_text);
            if (!Objects.requireNonNull(guest_username.getText()).toString().isEmpty()) {
                guest_user = guest_username.getText().toString();
                Intent game_page_intent = new Intent(this, GamePlayPage.class);
                game_page_intent.putExtra("Mode Choice", mode_choice);
                game_page_intent.putExtra("Guest Username", guest_user);
                game_page_intent.putExtra("Card Selected", card_selected);
                guest_user_dialog.dismiss();
                loading_text.setVisibility(View.VISIBLE);
                startActivity(game_page_intent);
            } else
                customModeSelectionPageToast("Please Enter Guest Name!");
        });
        guest_user_dialog.show();
    }

    private void customModeSelectionPageToast(String text_to_set) {
        Toast toast = new Toast(getApplicationContext());
        View view = getLayoutInflater().inflate(R.layout.custom_toast_layout_1, findViewById(R.id.custom_toast_layout_1));
        TextView toast_text_set_view = view.findViewById(R.id.toast_textview);
        toast_text_set_view.setText(text_to_set);
        toast_text_set_view.setTextColor(getColor(R.color.mutated_yellow_1));
        LinearLayoutCompat toast_layout_1 = view.findViewById(R.id.custom_toast_layout_1);
        toast_layout_1.setBackgroundColor(getColor(R.color.wood_brown));
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onClick(View v) {
        if (v == menu_action) {
            button_click_sound.start();
            if (menu_opened) {
                menuCloseOption();
            } else {
                menuOpenOption();
            }
        } else if (v == exit_action) {
            button_click_sound.start();
            finishAffinity();
        } else if (v == info_action) {
            button_click_sound.start();
            infoDialogBox();
        } else if (v == profile_action) {
            Intent user_profile_intent = new Intent(this, UserProfile.class);
            startActivity(user_profile_intent);
            button_click_sound.start();
        } else if (v == play_with_ai) {
            button_click_sound.start();
            underlined_mode_choice = new SpannableString("Play With AI");
            underlined_mode_choice.setSpan(new UnderlineSpan(), 0, underlined_mode_choice.length(), 0);
            menu_heading.setText(underlined_mode_choice);
            mode_choice = "Play With AI";
            play_with_ai.setStrokeWidth(2);
            multiplayer.setStrokeWidth(0);
            if (!mode_selected) {
                overall_menu_cardView.setVisibility(View.VISIBLE);
                overall_menu_cardView.startAnimation(raise_from_side);
                mode_selected = true;
            }
        } else if (v == multiplayer) {
            button_click_sound.start();
            underlined_mode_choice = new SpannableString("Multiplayer");
            underlined_mode_choice.setSpan(new UnderlineSpan(), 0, underlined_mode_choice.length(), 0);
            menu_heading.setText(underlined_mode_choice);
            mode_choice = "Multiplayer";
            multiplayer.setStrokeWidth(2);
            play_with_ai.setStrokeWidth(0);
            if (!mode_selected) {
                overall_menu_cardView.setVisibility(View.VISIBLE);
                overall_menu_cardView.startAnimation(raise_from_side);
                mode_selected = true;
            }
        } else if (v == ancient_cardView_menu) {
            card_click_sound.start();
            card_selected = "Ancient";
            ancient_mode_select_layout.setBackgroundColor(getColor(R.color.mutated_yellow_3));
            modern_light_select_layout.setBackgroundColor(getColor(R.color.mutated_yellow_1));
            modern_heavy_select_layout.setBackgroundColor(getColor(R.color.mutated_yellow_1));
            ancient_cardView_menu.startAnimation(click_scale);
        } else if (v == modern_light_cardView_menu) {
            card_click_sound.start();
            card_selected = "Modern_Light";
            modern_light_select_layout.setBackgroundColor(getColor(R.color.mutated_yellow_3));
            ancient_mode_select_layout.setBackgroundColor(getColor(R.color.mutated_yellow_1));
            modern_heavy_select_layout.setBackgroundColor(getColor(R.color.mutated_yellow_1));
            modern_light_cardView_menu.startAnimation(click_scale);
        } else if (v == modern_heavy_cardView_menu) {
            card_click_sound.start();
            card_selected = "Modern_Heavy";
            modern_heavy_select_layout.setBackgroundColor(getColor(R.color.mutated_yellow_3));
            modern_light_select_layout.setBackgroundColor(getColor(R.color.mutated_yellow_1));
            ancient_mode_select_layout.setBackgroundColor(getColor(R.color.mutated_yellow_1));
            modern_heavy_cardView_menu.startAnimation(click_scale);
        } else if (v == home_play_button) {
            button_click_sound.start();
            Intent game_page_intent = new Intent(this, GamePlayPage.class);
            if (mode_choice.equals("Play With AI")) {
                if (!card_selected.isEmpty()) {
                    game_page_intent.putExtra("Mode Choice", mode_choice);
                    game_page_intent.putExtra("Guest Username", "AI");
                    game_page_intent.putExtra("Card Selected", card_selected);
                    loading_text.setVisibility(View.VISIBLE);
                    startActivity(game_page_intent);
                } else
                    customModeSelectionPageToast("Select Card Mode to Continue!");
            } else if (mode_choice.equals("Multiplayer")) {
                if (!card_selected.isEmpty())
                    guestUserDialogBox();
                else
                    customModeSelectionPageToast("Select Card Mode to Continue!");
            }
        }
    }

    @Override
    protected void onPause() {
        base_bg.pause();
        rain_effect.pause();
        surface_video.release();
        overall_menu_cardView.setVisibility(View.GONE);
        mode_selected = false;
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        base_bg.start();
        rain_effect.start();
        close_navigation_ui = findViewById(R.id.base_mode_video);
        close_navigation_ui.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        multiplayer.setStrokeWidth(0);
        play_with_ai.setStrokeWidth(0);
        card_selected = "";
        modern_heavy_select_layout.setBackgroundColor(getColor(R.color.mutated_yellow_1));
        modern_light_select_layout.setBackgroundColor(getColor(R.color.mutated_yellow_1));
        ancient_mode_select_layout.setBackgroundColor(getColor(R.color.mutated_yellow_1));
        super.onPostResume();
    }
}