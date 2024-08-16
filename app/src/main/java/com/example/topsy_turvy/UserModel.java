package com.example.topsy_turvy;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "game_users")
public class UserModel {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "username")
    private final String user_name;
    @ColumnInfo(name = "password")
    private final String user_password;
    @ColumnInfo(name = "game_played")
    private final int game_played;
    @ColumnInfo(name = "game_won")
    private final int game_won;
    @ColumnInfo(name = "total_score")
    private final int total_points_scored;
    @ColumnInfo(name = "personal_best")
    private final int personal_best;

    UserModel(@NonNull String user_name, String user_password, int game_played, int game_won, int total_points_scored, int personal_best) {
        this.user_name = user_name;
        this.user_password = user_password;
        this.game_played = game_played;
        this.game_won = game_won;
        this.total_points_scored = total_points_scored;
        this.personal_best = personal_best;
    }

    @NonNull
    public String getUser_name() {
        return user_name;
    }

    public String getUser_password() {
        return user_password;
    }

    public int getGame_played() {
        return game_played;
    }

    public int getGame_won() {
        return game_won;
    }

    public int getTotal_points_scored() {
        return total_points_scored;
    }

    public int getPersonal_best() {
        return personal_best;
    }

}
