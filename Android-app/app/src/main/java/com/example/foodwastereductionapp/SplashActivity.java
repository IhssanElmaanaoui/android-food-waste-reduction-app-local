package com.example.foodwastereductionapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.foodwastereductionapp.auth.SessionManager;
import com.example.foodwastereductionapp.database.AppDatabase;
import com.example.foodwastereductionapp.model.Commerce;
import com.example.foodwastereductionapp.model.User;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SessionManager session = new SessionManager(this);
        ensureDemoUsersExist();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (session.isLoggedIn()) {
                startRoleActivity(session.getRole());
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 700);
    }

    private void ensureDemoUsersExist() {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "food-waste-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        if (db.userDao().findByEmail("admin@demo.com") == null) {
            User adminUser = new User();
            adminUser.name = "Admin";
            adminUser.email = "admin@demo.com";
            adminUser.password = "1234";
            adminUser.role = "admin";
            db.userDao().insert(adminUser);
        }
        if (db.userDao().findByEmail("client@demo.com") == null) {
            User clientUser = new User();
            clientUser.name = "Client Demo";
            clientUser.email = "client@demo.com";
            clientUser.password = "1234";
            clientUser.role = "client";
            db.userDao().insert(clientUser);
        }
        if (db.userDao().findByEmail("merchant@demo.com") == null) {
            User merchantUser = new User();
            merchantUser.name = "Commerçant Demo";
            merchantUser.email = "merchant@demo.com";
            merchantUser.password = "1234";
            merchantUser.role = "merchant";
            long merchantId = db.userDao().insert(merchantUser);
            Commerce commerce = new Commerce();
            commerce.name = "Boulangerie Demo";
            commerce.address = "1 rue de la Paix";
            commerce.ownerId = (int) merchantId;
            db.commerceDao().insert(commerce);
        }
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
