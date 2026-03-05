package com.example.foodwastereductionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.foodwastereductionapp.auth.SessionManager;
import com.example.foodwastereductionapp.database.AppDatabase;
import com.example.foodwastereductionapp.databinding.ActivityRegisterBinding;
import com.example.foodwastereductionapp.model.User;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AppDatabase db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "food-waste-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        sessionManager = new SessionManager(this);

        binding.btnRegister.setOnClickListener(v -> attemptRegister());
        binding.linkLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        String name = getTextFromInput(binding.inputName);
        String email = getTextFromInput(binding.inputEmail).trim();
        String password = getTextFromInput(binding.inputPassword);
        String confirmPassword = getTextFromInput(binding.inputConfirmPassword);

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError(getString(R.string.register_error_empty));
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError(getString(R.string.register_error_password_mismatch));
            return;
        }

        if (password.length() < 4) {
            showError(getString(R.string.register_error_password_short));
            return;
        }

        if (db.userDao().findByEmail(email) != null) {
            showError(getString(R.string.register_error_email_used));
            return;
        }

        User user = new User();
        user.name = name;
        user.email = email;
        user.password = password;
        user.role = binding.roleClient.isChecked() ? "client" : "merchant";

        long id = db.userDao().insert(user);
        user.id = (int) id;
        sessionManager.saveSession(user);
        Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private String getTextFromInput(com.google.android.material.textfield.TextInputLayout input) {
        return input.getEditText() != null ? input.getEditText().getText().toString() : "";
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
