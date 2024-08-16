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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

public class GamePlayPage extends AppCompatActivity implements View.OnClickListener {
    View close_navigation_ui;
    AppCompatImageView detail_image_1, detail_image_2, detail_image_3, detail_image_4;
    MaterialButton game_page_home_button, game_page_new_game_button;
    TextView game_page_player_name, game_page_ai_guest_name, game_page_player_score, game_page_ai_guest_score, player_score_incrementer, guest_ai_score_incrementer, turn_number;
    String chosen_mode, guest_username, player_name, selected_card;
    GridLayout game_card_grid;
    ArrayList<View> all_cards = new ArrayList<>();
    ArrayList<Integer> unselected_cards_position = new ArrayList<>();
    ArrayList<View> combo_buttons = new ArrayList<>();
    ArrayList<UserModel> game_value_update = new ArrayList<>();
    int player = 0, player_score = 0, guest_ai_score = 0, score_incrementation = 0, card_clicked_counter = 0, turn_counter = 1;
    int toss_random_value, weapon_random_value;
    Animation card_select_animation, dialog_bottom_raise_animation;
    MediaPlayer game_page_bg_music, unselected_card_clicked_sound, button_clicked_sound, combo_sound, toss_sound, win_sound, lose_sound, win_voice, lose_voice;
    DatabaseHelper databaseHelper;
    private final int[][] card_board = new int[5][10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play_page);
        Intent received_intent = getIntent();
        chosen_mode = received_intent.getStringExtra("Mode Choice");
        guest_username = received_intent.getStringExtra("Guest Username");
        selected_card = received_intent.getStringExtra("Card Selected");
        idFinder();
        dynamicCardMaker();
        button_clicked_sound = MediaPlayer.create(this, R.raw.button_click_sound);
        unselected_card_clicked_sound = MediaPlayer.create(this, R.raw.card_click_sound);
        combo_sound = MediaPlayer.create(this, R.raw.combo_sound);
        toss_sound = MediaPlayer.create(this, R.raw.random_toss_sound_trimmed);
        win_sound = MediaPlayer.create(this, R.raw.winner_bg_sound_effect);
        lose_sound = MediaPlayer.create(this, R.raw.lost);
        win_voice = MediaPlayer.create(this, R.raw.congratulations_deep_voice);
        lose_voice = MediaPlayer.create(this, R.raw.you_lose_deep_voice);
        game_page_bg_music = MediaPlayer.create(this, R.raw.main_page_bg_music_reduced_volume);
        game_page_bg_music.start();
        game_page_bg_music.setLooping(true);
        SharedPreferences sharedPreferences = getSharedPreferences("loggedUser", MODE_PRIVATE);
        player_name = sharedPreferences.getString("User", "");
        all_cards = game_card_grid.getTouchables();
        game_page_home_button.setOnClickListener(this);
        game_page_new_game_button.setOnClickListener(this);
        detailsImageSetter();
        randomToss();
        game_page_ai_guest_name.setText(MessageFormat.format("{0}:", guest_username));
        game_page_player_name.setText(MessageFormat.format("{0}:", player_name));
        card_select_animation = AnimationUtils.loadAnimation(this, R.anim.card_select_animation);
        dialog_bottom_raise_animation = AnimationUtils.loadAnimation(this, R.anim.raise_from_bottom_animation);
        databaseHelper = DatabaseHelper.getDB(this);
        game_value_update = (ArrayList<UserModel>) databaseHelper.modelDAO().getUserDetail(player_name);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                newGameExitDialog("Exit Match?");
            }
        });
    }

    // Basic operations

    private void dynamicCardMaker(){
        for (int i=0;i<50;i++){
            AppCompatImageButton appCompatImageButton=new AppCompatImageButton(this);
            GridLayout.LayoutParams params=new GridLayout.LayoutParams();
            params.width=dpToPx(36);
            params.height=dpToPx(36);
            params.setMargins(4,4,4,4 );
            appCompatImageButton.setLayoutParams(params);
            appCompatImageButton.setContentDescription("Weapon is not assigned");
            appCompatImageButton.setElevation(0);
            appCompatImageButton.setImageResource(R.drawable.svg_card_flip_2);
            appCompatImageButton.setOnClickListener(this);
            game_card_grid.addView(appCompatImageButton);
        }
    }

    public int dpToPx(int dp) {
        float density = this.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void cardBoardInitializer() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                card_board[i][j] = 0;
            }
        }
    }

    private void unselectedCardListMaker() {
        unselected_cards_position.clear();
        for (int i = 0; i < 50; i++) {
            unselected_cards_position.add(i);
        }
    }

    private void idFinder() {
        game_page_home_button = findViewById(R.id.game_page_home_button);
        game_page_new_game_button = findViewById(R.id.game_page_new_game_button);
        detail_image_1 = findViewById(R.id.detail_image_1);
        detail_image_2 = findViewById(R.id.detail_image_2);
        detail_image_3 = findViewById(R.id.detail_image_3);
        detail_image_4 = findViewById(R.id.detail_image_4);
        game_page_player_name = findViewById(R.id.game_page_player_name);
        game_page_ai_guest_name = findViewById(R.id.game_page_guest_or_ai_name);
        game_page_player_score = findViewById(R.id.game_page_player_score);
        game_page_ai_guest_score = findViewById(R.id.game_page_guest_or_ai_score);
        player_score_incrementer = findViewById(R.id.player_score_incrementer);
        guest_ai_score_incrementer = findViewById(R.id.guest_or_ai_score_incrementer);
        game_card_grid = findViewById(R.id.game_gridLayout);
        turn_number = findViewById(R.id.turn_number);
    }

    private void detailsImageSetter() {
        switch (selected_card) {
            case "Ancient":
                detail_image_1.setImageResource(R.drawable.knife_transparent);
                detail_image_2.setImageResource(R.drawable.arrow_transparent);
                detail_image_3.setImageResource(R.drawable.spear_tri_shape);
                detail_image_4.setImageResource(R.drawable.sword_transparent);
                break;
            case "Modern_Light":
                detail_image_1.setImageResource(R.drawable.pistol);
                detail_image_2.setImageResource(R.drawable.shot_gun);
                detail_image_3.setImageResource(R.drawable.sniper_rifle);
                detail_image_4.setImageResource(R.drawable.assault_rifle);
                detail_image_1.setRotation(0);
                detail_image_2.setRotation(0);
                detail_image_3.setRotation(0);
                break;
            case "Modern_Heavy":
                detail_image_1.setImageResource(R.drawable.armoured_vehicle_transparent);
                detail_image_2.setImageResource(R.drawable.tank_transparent);
                detail_image_3.setImageResource(R.drawable.artillery_transparent);
                detail_image_4.setImageResource(R.drawable.fighter_aircraft_transparent);
                detail_image_1.setRotation(0);
                detail_image_2.setRotation(0);
                detail_image_1.setScaleX(-1);
                detail_image_2.setScaleX(-1);
                detail_image_3.setRotation(0);
                break;
        }
    }

    //Support Logic functions

    private void randomToss() {
        player_score = 0;
        guest_ai_score = 0;
        unselectedCardListMaker();
        cardBoardInitializer();
        game_page_player_score.setText(String.valueOf(player_score));
        game_page_ai_guest_score.setText(String.valueOf(guest_ai_score));
        toss_random_value = new Random().nextInt(2);
        if (toss_random_value == 0) {
            player = 1;
            game_page_player_name.setBackgroundColor(getColor(R.color.door_color));
            game_page_ai_guest_name.setBackgroundColor(Color.TRANSPARENT);
            new Handler().postDelayed(this::randomTossDialog, 1000);
        } else {
            player = 2;
            game_page_ai_guest_name.setBackgroundColor(getColor(R.color.door_color));
            game_page_player_name.setBackgroundColor(Color.TRANSPARENT);
            new Handler().postDelayed(this::randomTossDialog, 1000);
        }
    }

    private void updateCardBoard(int clicked_card_index, int card_weight) {
        if (clicked_card_index <= 9) {
            card_board[0][clicked_card_index] = card_weight;
        } else {
            int column_index = clicked_card_index % 10;
            int row_index = clicked_card_index / 10;
            card_board[row_index][column_index] = card_weight;
        }
    }

    private int randomWeaponDecider(AppCompatImageButton clicked_card, int clicked_card_index) {
        int card_weight;
        weapon_random_value = new Random().nextInt(100);
        if (weapon_random_value < 20) {
            clicked_card.setContentDescription("Assigned weapon_1");
            card_weight = 1;
            switch (selected_card) {
                case "Ancient":
                    clicked_card.setImageResource(R.drawable.knife_transparent);
                    break;
                case "Modern_Light":
                    clicked_card.setImageResource(R.drawable.pistol);
                    break;
                case "Modern_Heavy":
                    clicked_card.setImageResource(R.drawable.armoured_vehicle_transparent);
                    clicked_card.setScaleX(-1);
                    break;
            }
        } else if (weapon_random_value < 50) {
            clicked_card.setContentDescription("Assigned weapon_2");
            card_weight = 4;
            switch (selected_card) {
                case "Ancient":
                    clicked_card.setImageResource(R.drawable.arrow_transparent);
                    break;
                case "Modern_Light":
                    clicked_card.setImageResource(R.drawable.shot_gun);
                    break;
                case "Modern_Heavy":
                    clicked_card.setImageResource(R.drawable.tank_transparent);
                    clicked_card.setScaleX(-1);
                    break;
            }
        } else if (weapon_random_value < 75) {
            clicked_card.setContentDescription("Assigned weapon_3");
            card_weight = 13;
            switch (selected_card) {
                case "Ancient":
                    clicked_card.setImageResource(R.drawable.spear_tri_shape);
                    break;
                case "Modern_Light":
                    clicked_card.setImageResource(R.drawable.sniper_rifle);
                    break;
                case "Modern_Heavy":
                    clicked_card.setImageResource(R.drawable.artillery_transparent);
                    break;
            }
        } else {
            clicked_card.setContentDescription("Assigned weapon_4");
            card_weight = 40;
            switch (selected_card) {
                case "Ancient":
                    clicked_card.setImageResource(R.drawable.sword_transparent);
                    break;
                case "Modern_Light":
                    clicked_card.setImageResource(R.drawable.assault_rifle);
                    break;
                case "Modern_Heavy":
                    clicked_card.setImageResource(R.drawable.fighter_aircraft_transparent);
                    break;
            }
        }
        updateCardBoard(clicked_card_index, card_weight);
        return card_weight;
    }

    private void disableClick() {
        for (View view : all_cards)
            if (view instanceof AppCompatImageButton) {
                view.setClickable(false);
                view.setAlpha(0.85f);
            }
    }

    private void enableClick() {
        for (View view : all_cards)
            if (view instanceof AppCompatImageButton) {
                view.setClickable(true);
                view.setAlpha(1);
            }
    }

    private void comboCardReset() {
        for (View iButton : combo_buttons) {
            int reset_card_index = all_cards.indexOf(iButton);
            ((AppCompatImageButton) iButton).setImageResource(R.drawable.svg_card_flip_2);
            iButton.setContentDescription("Weapon is not assigned");
            unselected_cards_position.add(reset_card_index);
            updateCardBoard(reset_card_index, 0);
            iButton.startAnimation(card_select_animation);
            new Handler().postDelayed(iButton::clearAnimation, 200);
        }
    }

    private void allCardsReset() {
        for (View view : all_cards)
            if (view instanceof AppCompatImageButton) {
                ((AppCompatImageButton) view).setImageResource(R.drawable.svg_card_flip_2);
                view.setContentDescription("Weapon is not assigned");
            }
        cardBoardInitializer();
        unselectedCardListMaker();
        if (player == 2)
            new Handler().postDelayed(this::aiChoicePredictor, 300);
    }

    private void normalIncrementSetter(int card_weight) {
        switch (card_weight) {
            case 1:
                score_incrementation = 1;
                break;
            case 4:
                score_incrementation = 2;
                break;
            case 13:
                score_incrementation = 3;
                break;
            case 40:
                score_incrementation = 4;
                break;
        }
        playersScoreSetter();
        callAI();
    }

    private void comboIncrementationSetter(int card_weight, int i1, int i2, int i3, int i4) {
        switch (card_weight) {
            case 1:
                score_incrementation = i1;
                break;
            case 4:
                score_incrementation = i2;
                break;
            case 13:
                score_incrementation = i3;
                break;
            case 40:
                score_incrementation = i4;
                break;
        }
        playersScoreSetter();
    }

    private void playersScoreSetter() {
        if (player == 1) {
            guest_ai_score = guest_ai_score + score_incrementation;
            game_page_ai_guest_score.setText(String.valueOf(guest_ai_score));
            guest_ai_score_incrementer.setText(MessageFormat.format("+ {0}", score_incrementation));
            guest_ai_score_incrementer.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> guest_ai_score_incrementer.setVisibility(View.GONE), 500);
        } else if (player == 2) {
            player_score = player_score + score_incrementation;
            game_page_player_score.setText(String.valueOf(player_score));
            player_score_incrementer.setText(MessageFormat.format("+ {0}", score_incrementation));
            player_score_incrementer.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> player_score_incrementer.setVisibility(View.GONE), 500);
        }
        winnerCondition();
    }

    private void newGameValueSetter() {
        randomToss();
        turn_counter = 1;
        card_clicked_counter = 0;
        turn_number.setText(MessageFormat.format("TURN: {0}", turn_counter));
        for (View view : all_cards) {
            if (!view.getContentDescription().equals("Weapon is not assigned")) {
                ((AppCompatImageButton) view).setImageResource(R.drawable.svg_card_flip_2);
                view.setContentDescription("Weapon is not assigned");
            }
        }
    }

    private void winnerCondition() {
        if (player_score >= 100) {
            int personal_best = game_value_update.get(0).getPersonal_best();
            if (personal_best == 0)
                personal_best = turn_counter;
            else if (turn_counter < personal_best)
                personal_best = turn_counter;
            databaseHelper.modelDAO().updateDetails(new UserModel(game_value_update.get(0).getUser_name(), game_value_update.get(0).getUser_password(),
                    game_value_update.get(0).getGame_played() + 1, game_value_update.get(0).getGame_won() + 1,
                    game_value_update.get(0).getTotal_points_scored() + player_score, personal_best));
            game_value_update = (ArrayList<UserModel>) databaseHelper.modelDAO().getUserDetail(player_name);
            winnerDialog(player_name);
            win_voice.start();
            win_sound.start();
        } else if (guest_ai_score >= 100) {
            databaseHelper.modelDAO().updateDetails(new UserModel(game_value_update.get(0).getUser_name(), game_value_update.get(0).getUser_password(),
                    game_value_update.get(0).getGame_played() + 1, game_value_update.get(0).getGame_won(),
                    game_value_update.get(0).getTotal_points_scored() + player_score, game_value_update.get(0).getPersonal_best()));
            game_value_update = (ArrayList<UserModel>) databaseHelper.modelDAO().getUserDetail(player_name);
            winnerDialog(guest_username);
            if (guest_username.equals("AI")) {
                lose_voice.start();
                lose_sound.start();
            } else {
                win_voice.start();
                win_sound.start();
            }
        }
    }

    //Customization UI functions

    private void comboDialog(String combo_type) {
        Dialog combo_dialog = new Dialog(this);
        combo_dialog.setContentView(R.layout.combo_toss_dialog_box);
        TextView combo_dialog_text = combo_dialog.findViewById(R.id.combo_toss_text);
        if (player == 2)
            combo_dialog_text.setText(MessageFormat.format("{0} Score: {1} \n {2}", combo_type, score_incrementation, player_name));
        else if (player == 1)
            combo_dialog_text.setText(MessageFormat.format("{0} Score: {1} \n {2}", combo_type, score_incrementation, guest_username));
        Objects.requireNonNull(combo_dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        combo_dialog.setOnDismissListener(dialog -> {
            comboCardReset();
            callAI();
        });
        combo_sound.start();
        combo_dialog.show();
        new Handler().postDelayed(combo_dialog::dismiss, 2000);
    }

    private void randomTossDialog() {
        Dialog toss_dialog = new Dialog(this);
        toss_dialog.setContentView(R.layout.combo_toss_dialog_box);
        TextView toss_dialog_text = toss_dialog.findViewById(R.id.combo_toss_text);
        if (toss_random_value == 0)
            toss_dialog_text.setText(MessageFormat.format("Random toss decision: {0} play first", player_name));
        else if (toss_random_value == 1) {
            toss_dialog_text.setText(MessageFormat.format("Random toss decision: {0} play first", guest_username));
            if (chosen_mode.equals("Play With AI"))
                toss_dialog.setOnDismissListener(dialog -> aiChoicePredictor());
        }
        toss_sound.start();
        Objects.requireNonNull(toss_dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MaterialCardView toss_card_view = toss_dialog.findViewById(R.id.toss_combo_dialog_card);
        toss_card_view.startAnimation(dialog_bottom_raise_animation);
        toss_dialog.show();
        new Handler().postDelayed(toss_dialog::dismiss, 3000);
    }

    private void newGameExitDialog(String heading) {
        Dialog new_game_exit_dialog = new Dialog(this);
        new_game_exit_dialog.setContentView(R.layout.new_game_exit_confirmation_dialog);
        TextView heading_textview = new_game_exit_dialog.findViewById(R.id.new_game_exit_heading);
        heading_textview.setText(heading);
        new_game_exit_dialog.setCancelable(false);
        MaterialButton dialog_yes_button = new_game_exit_dialog.findViewById(R.id.new_game_exit_yes_button);
        MaterialButton dialog_back_button = new_game_exit_dialog.findViewById(R.id.new_game_exit_back_button);
        dialog_yes_button.setOnClickListener(v -> {
            button_clicked_sound.start();
            if (heading.equals("Start New Game?")) {
                newGameValueSetter();
                new_game_exit_dialog.dismiss();
            } else if (heading.equals("Exit Match?")) {
                Intent back_to_home_intent = new Intent(this, ModeSelectionPage.class);
                startActivity(back_to_home_intent);
                new_game_exit_dialog.dismiss();
                finishAffinity();
            }
            databaseHelper.modelDAO().updateDetails(new UserModel(game_value_update.get(0).getUser_name(), game_value_update.get(0).getUser_password(),
                    game_value_update.get(0).getGame_played() + 1, game_value_update.get(0).getGame_won(),
                    game_value_update.get(0).getTotal_points_scored() + player_score, game_value_update.get(0).getPersonal_best()));
            game_value_update = (ArrayList<UserModel>) databaseHelper.modelDAO().getUserDetail(player_name);
        });
        dialog_back_button.setOnClickListener(v -> new_game_exit_dialog.dismiss());
        Objects.requireNonNull(new_game_exit_dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        new_game_exit_dialog.show();
    }

    private void winnerDialog(String winner_name) {
        Dialog winner_dialog = new Dialog(this);
        winner_dialog.setContentView(R.layout.winner_dialog);
        TextView winner_display = winner_dialog.findViewById(R.id.winner_name);
        winner_display.setText(winner_name);
        winner_display.startAnimation(dialog_bottom_raise_animation);
        MaterialButton winner_dialog_new_game = winner_dialog.findViewById(R.id.winner_dialog_new_game_button);
        MaterialButton winner_dialog_home_button = winner_dialog.findViewById(R.id.winner_dialog_back_to_home_button);
        winner_dialog_new_game.setOnClickListener(v -> {
            newGameValueSetter();
            winner_dialog.dismiss();
        });
        winner_dialog_home_button.setOnClickListener(v -> {
            Intent home_page_intent = new Intent(this, ModeSelectionPage.class);
            startActivity(home_page_intent);
            winner_dialog.dismiss();
            finishAffinity();
        });
        winner_dialog.setCancelable(false);
        Objects.requireNonNull(winner_dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        winner_dialog.show();
    }

    private void onCardClick(View clicked_card) {
        int card_clicked_index = all_cards.indexOf(clicked_card);
        int card_weight = randomWeaponDecider((AppCompatImageButton) clicked_card, card_clicked_index);
        unselected_cards_position.remove((Integer) card_clicked_index);
        unselected_card_clicked_sound.start();
        clicked_card.setBackgroundColor(getColor(R.color.mutated_yellow_1));
        clicked_card.startAnimation(card_select_animation);
        new Handler().postDelayed(clicked_card::clearAnimation, 200);
        if (player == 1) {
            player = 2;
            game_page_player_name.setBackgroundColor(Color.TRANSPARENT);
            game_page_ai_guest_name.setBackgroundColor(getColor(R.color.door_color));
        } else if (player == 2) {
            player = 1;
            game_page_player_name.setBackgroundColor(getColor(R.color.door_color));
            game_page_ai_guest_name.setBackgroundColor(Color.TRANSPARENT);
        }
        card_clicked_counter++;
        if (card_clicked_counter % 2 == 0) {
            turn_counter++;
            turn_number.setText(MessageFormat.format("TURN: {0}", turn_counter));
        }
        if (turn_counter > 1)
            comboMethod(card_clicked_index, card_weight);
        else
            normalIncrementSetter(card_weight);
        disableClick();
        new Handler().postDelayed(this::enableClick, 500);

    }

    // Combo Logic Section

    private ArrayList<Integer> scoreBoard(int clicked_card_index) {
        int i, j;
        ArrayList<Integer> score_board_list = new ArrayList<>(Collections.nCopies(6, 0));
        if (clicked_card_index <= 9) {
            i = 0;
            j = clicked_card_index;

        } else {
            j = clicked_card_index % 10;
            i = clicked_card_index / 10;
        }
        if (j - 1 >= 0) {
            if (j - 2 >= 0) {
                score_board_list.set(0, card_board[i][j - 2] + card_board[i][j] + card_board[i][j - 1]);
                if (j + 1 <= 9) {
                    score_board_list.set(4, card_board[i][j + 1] + card_board[i][j] + card_board[i][j - 1]);
                    if (j + 2 <= 9)
                        score_board_list.set(2, card_board[i][j + 2] + card_board[i][j] + card_board[i][j + 1]);
                }
            } else {
                score_board_list.set(2, card_board[i][j + 2] + card_board[i][j] + card_board[i][j + 1]);
                score_board_list.set(4, card_board[i][j - 1] + card_board[i][j] + card_board[i][j + 1]);
            }
        } else
            score_board_list.set(2, card_board[i][j + 2] + card_board[i][j] + card_board[i][j + 1]);
        if (i - 1 >= 0) {
            if (i - 2 >= 0) {
                score_board_list.set(1, card_board[i - 2][j] + card_board[i][j] + card_board[i - 1][j]);
                if (i + 1 <= 4) {
                    score_board_list.set(5, card_board[i + 1][j] + card_board[i][j] + card_board[i - 1][j]);
                    if (i + 2 <= 4)
                        score_board_list.set(3, card_board[i + 2][j] + card_board[i][j] + card_board[i + 1][j]);
                }
            } else {
                score_board_list.set(3, card_board[i + 2][j] + card_board[i][j] + card_board[i + 1][j]);
                score_board_list.set(5, card_board[i + 1][j] + card_board[i][j] + card_board[i - 1][j]);
            }
        } else
            score_board_list.set(3, card_board[i + 2][j] + card_board[i][j] + card_board[i + 1][j]);
        return score_board_list;
    }

    private void comboMethod(int card_clicked_index, int card_weight) {
        ArrayList<Integer> score_board_list = scoreBoard(card_clicked_index);
        int card_weight_3x = card_weight * 3;
        ArrayList<Integer> combo_indexes = new ArrayList<>();
        if (score_board_list.contains(card_weight_3x)) {
            for (int i = 0; i < score_board_list.size(); i++)
                if (score_board_list.get(i) == card_weight_3x)
                    combo_indexes.add(i);
            int combo_indexes_size = combo_indexes.size();
            if (combo_indexes_size == 1)
                comboHandler(combo_indexes, card_clicked_index, card_weight, "Combo");
            else if (combo_indexes_size == 2) {
                if (singleCombo(combo_indexes)) {
                    combo_indexes.remove(1);
                    comboHandler(combo_indexes, card_clicked_index, card_weight, "Combo");
                } else
                    comboHandler(combo_indexes, card_clicked_index, card_weight, "Dual Combo");
            } else if (combo_indexes_size == 3) {
                combo_indexes.remove(2);
                comboHandler(combo_indexes, card_clicked_index, card_weight, "Dual Combo");
            } else if (combo_indexes_size == 4) {
                if (tripleComboChecker(combo_indexes))
                    comboHandler(combo_indexes, card_clicked_index, card_weight, "Triple Combo");
                else {
                    combo_indexes.remove((Integer) 4);
                    combo_indexes.remove((Integer) 5);
                    comboHandler(combo_indexes, card_clicked_index, card_weight, "Dual Combo");
                }
            } else if (combo_indexes_size == 5) {
                if (combo_indexes.contains(0) && combo_indexes.contains(2))
                    combo_indexes.remove((Integer) 5);
                else
                    combo_indexes.remove((Integer) 4);
                comboHandler(combo_indexes, card_clicked_index, card_weight, "Triple Combo");
            } else {
                comboHandler(combo_indexes, card_clicked_index, card_weight, "Quad Combo");
            }
        } else
            normalIncrementSetter(card_weight);
    }

    private boolean tripleComboChecker(ArrayList<Integer> combo_indexes) {
        if (combo_indexes.containsAll(Arrays.asList(0, 1, 2)))
            return true;
        else if (combo_indexes.containsAll(Arrays.asList(0, 1, 3)))
            return true;
        else if (combo_indexes.containsAll(Arrays.asList(0, 2, 3)))
            return true;
        else if (combo_indexes.containsAll(Arrays.asList(1, 2, 3)))
            return true;
        else if (combo_indexes.containsAll(Arrays.asList(1, 3, 4)))
            return true;
        else return combo_indexes.containsAll(Arrays.asList(0, 2, 5));
    }

    private boolean singleCombo(ArrayList<Integer> combo_indexes) {
        if (combo_indexes.containsAll(Arrays.asList(0, 4)))
            return true;
        else if (combo_indexes.containsAll(Arrays.asList(1, 5)))
            return true;
        else if (combo_indexes.containsAll(Arrays.asList(2, 4)))
            return true;
        else return combo_indexes.containsAll(Arrays.asList(3, 5));
    }

    private void comboHandler(ArrayList<Integer> combo_indexes, int clicked_card_index, int card_weight, String combo_type) {
        combo_buttons.clear();
        combo_buttons.add(all_cards.get(clicked_card_index));
        for (int index : combo_indexes)
            switch (index) {
                case 0:
                    combo_buttons.add(all_cards.get(clicked_card_index - 1));
                    combo_buttons.add(all_cards.get(clicked_card_index - 2));
                    break;
                case 1:
                    combo_buttons.add(all_cards.get(clicked_card_index - 10));
                    combo_buttons.add(all_cards.get(clicked_card_index - 20));
                    break;
                case 2:
                    combo_buttons.add(all_cards.get(clicked_card_index + 1));
                    combo_buttons.add(all_cards.get(clicked_card_index + 2));
                    break;
                case 3:
                    combo_buttons.add(all_cards.get(clicked_card_index + 10));
                    combo_buttons.add(all_cards.get(clicked_card_index + 20));
                    break;
                case 4:
                    if (!combo_buttons.containsAll(Arrays.asList(all_cards.get(clicked_card_index - 1), all_cards.get(clicked_card_index + 1)))) {
                        combo_buttons.add(all_cards.get(clicked_card_index - 1));
                        combo_buttons.add(all_cards.get(clicked_card_index + 1));
                    }
                    break;
                case 5:
                    if (!combo_buttons.containsAll(Arrays.asList(all_cards.get(clicked_card_index - 10), all_cards.get(clicked_card_index + 10)))) {
                        combo_buttons.add(all_cards.get(clicked_card_index - 10));
                        combo_buttons.add(all_cards.get(clicked_card_index + 10));
                    }
                    break;
            }
        switch (combo_type) {
            case "Combo":
                comboIncrementationSetter(card_weight, 5, 10, 15, 20);
                break;
            case "Dual Combo":
                comboIncrementationSetter(card_weight, 15, 25, 35, 45);
                break;
            case "Triple Combo":
                comboIncrementationSetter(card_weight, 25, 40, 55, 70);
                break;
            case "Quad Combo":
                comboIncrementationSetter(card_weight, 35, 55, 75, 95);
                break;
        }
        new Handler().postDelayed(() -> comboDialog(combo_type), 300);
    }

    // AI logic functions

    private void callAI() {
        if (unselected_cards_position.isEmpty())
            new Handler().postDelayed(this::allCardsReset, 500);
        else if (chosen_mode.equals("Play With AI")) {
            if (player == 2 && player_score < 100) {
                new Handler().postDelayed(this::aiChoicePredictor, 500);
            }
        }
    }

    private void aiChoicePredictor() {
        boolean possibility_flag = false;
        Collections.shuffle(unselected_cards_position);
        if (turn_counter > 1) {
            for (int position : unselected_cards_position) {
                ArrayList<Integer> score_board_list = scoreBoard(position);
                if (score_board_list.contains(2) || score_board_list.contains(8) || score_board_list.contains(26) || score_board_list.contains(80)) {
                    possibility_flag = true;
                    onCardClick(all_cards.get(position));
                    break;
                }
            }
            if (!possibility_flag)
                onCardClick(all_cards.get(unselected_cards_position.get(0)));
        } else
            onCardClick(all_cards.get(unselected_cards_position.get(0)));
    }

    @Override
    protected void onPostResume() {
        close_navigation_ui = findViewById(R.id.game_page_base_constraint_layout);
        close_navigation_ui.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        game_page_bg_music.start();
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        game_page_bg_music.pause();
        if (win_sound.isPlaying())
            win_sound.release();
        if (win_voice.isPlaying())
            win_voice.release();
        if (lose_sound.isPlaying())
            lose_sound.release();
        if (lose_voice.isPlaying())
            lose_voice.release();
        if (combo_sound.isPlaying())
            combo_sound.release();
        if (toss_sound.isPlaying())
            toss_sound.release();
        if (unselected_card_clicked_sound.isPlaying())
            unselected_card_clicked_sound.release();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (v == game_page_home_button) {
            button_clicked_sound.start();
            newGameExitDialog("Exit Match?");
        } else if (v == game_page_new_game_button) {
            button_clicked_sound.start();
            newGameExitDialog("Start New Game?");
        } else if (v.getContentDescription().equals("Weapon is not assigned"))
            if (all_cards.contains(v))
                onCardClick(v);
    }
}