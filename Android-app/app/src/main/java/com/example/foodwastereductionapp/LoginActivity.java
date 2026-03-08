package com.example.foodwastereductionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.foodwastereductionapp.auth.SessionManager;
import com.example.foodwastereductionapp.database.AppDatabase;
import com.example.foodwastereductionapp.model.Commerce;
import com.example.foodwastereductionapp.model.User;

public class LoginActivity extends AppCompatActivity {

    private AppDatabase db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "food-waste-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        sessionManager = new SessionManager(this);
        ensureDemoUsersExist();

        EditText etEmail = findViewById(R.id.etLoginEmail);
        EditText etPassword = findViewById(R.id.etLoginPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoRegister = findViewById(R.id.tvGoRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email et mot de passe obligatoires.", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = db.userDao().login(email, password);
            if (user == null) {
                Toast.makeText(this, "Identifiants invalides.", Toast.LENGTH_SHORT).show();
                return;
            }

            sessionManager.saveSession(user);
            startRoleActivity(user.role);
            finish();
        });

        tvGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private void ensureDemoUsersExist() {
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
