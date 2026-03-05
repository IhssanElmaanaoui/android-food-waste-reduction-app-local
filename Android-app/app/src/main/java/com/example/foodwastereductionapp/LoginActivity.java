package com.example.foodwastereductionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.foodwastereductionapp.auth.SessionManager;
import com.example.foodwastereductionapp.database.AppDatabase;
import com.example.foodwastereductionapp.databinding.ActivityLoginBinding;
import com.example.foodwastereductionapp.model.User;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AppDatabase db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "food-waste-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        sessionManager = new SessionManager(this);

        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        binding.linkRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private void attemptLogin() {
        String email = binding.inputEmail.getEditText() != null
                ? binding.inputEmail.getEditText().getText().toString().trim()
                : "";
        String password = binding.inputPassword.getEditText() != null
                ? binding.inputPassword.getEditText().getText().toString()
                : "";

        if (email.isEmpty() || password.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.login_error_empty)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }

        User user = db.userDao().login(email, password);
        if (user == null) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.login_error_invalid)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }

        sessionManager.saveSession(user);
        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
