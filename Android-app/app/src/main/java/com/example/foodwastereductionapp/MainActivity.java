package com.example.foodwastereductionapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodwastereductionapp.auth.SessionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);
        if ("admin".equals(session.getRole())) {
            startActivity(new Intent(this, AdminActivity.class));
        } else if ("merchant".equals(session.getRole())) {
            startActivity(new Intent(this, MerchantActivity.class));
        } else {
            startActivity(new Intent(this, ClientActivity.class));
        }
        finish();
    }
}
