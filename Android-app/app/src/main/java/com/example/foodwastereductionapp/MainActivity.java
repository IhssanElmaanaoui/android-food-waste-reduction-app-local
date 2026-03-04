package com.example.foodwastereductionapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.foodwastereductionapp.database.AppDatabase;
import com.example.foodwastereductionapp.model.User;

public class MainActivity extends AppCompatActivity {

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "food-waste-db"
        ).allowMainThreadQueries().build();


        // TEST : ajouter un utilisateur dans la base

        User user = new User();
        user.name = "Test User";
        user.email = "test@gmail.com";
        user.password = "1234";
        user.role = "client";

        db.userDao().insert(user);
    }
}