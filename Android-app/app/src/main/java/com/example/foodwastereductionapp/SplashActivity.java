package com.example.foodwastereductionapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.foodwastereductionapp.auth.SessionManager;
import com.example.foodwastereductionapp.database.AppDatabase;
import com.example.foodwastereductionapp.model.User;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SessionManager session = new SessionManager(this);
        ensureAdminExists();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (session.isLoggedIn()) {
                startRoleActivity(session.getRole());
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 700);
    }

    private void ensureAdminExists() {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "food-waste-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        User admin = db.userDao().findByEmail("admin@demo.com");
        if (admin != null) {
            return;
        }

        User adminUser = new User();
        adminUser.name = "Admin";
        adminUser.email = "admin@demo.com";
        adminUser.password = "1234";
        adminUser.role = "admin";
        db.userDao().insert(adminUser);
    }

    private void startRoleActivity(String role) {
        Intent intent;
        if ("admin".equals(role)) {
            intent = new Intent(this, AdminActivity.class);
        } else if ("merchant".equals(role)) {
            intent = new Intent(this, MerchantActivity.class);
        } else {
            intent = new Intent(this, ClientActivity.class);
        }
        startActivity(intent);
    }
}
