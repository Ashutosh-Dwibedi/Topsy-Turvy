package com.example.topsy_turvy;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ModelDAO {
    @Query("SELECT * FROM game_users")
    LiveData<List<UserModel>> getAllUsers();

    @Insert
    void addDetails(UserModel userModel);

    @Update
    void updateDetails(UserModel userModel);

    @Query("SELECT EXISTS(SELECT * FROM game_users WHERE username = :username AND password = :password)")
    boolean isAuthenticationSuccessful(String username, String password);

    @Query("SELECT * FROM game_users WHERE username = :username")
    List<UserModel> getUserDetail(String username);

    @Query("DELETE FROM game_users WHERE username= :username")
    void deleteUser(String username);
}
