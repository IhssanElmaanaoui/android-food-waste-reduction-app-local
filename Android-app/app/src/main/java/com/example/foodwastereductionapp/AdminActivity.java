package com.example.foodwastereductionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.foodwastereductionapp.auth.SessionManager;
import com.example.foodwastereductionapp.database.AppDatabase;
import com.example.foodwastereductionapp.model.Commerce;
import com.example.foodwastereductionapp.model.Reservation;
import com.example.foodwastereductionapp.model.User;
import com.example.foodwastereductionapp.service.AdminManager;

import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private AppDatabase db;
    private SessionManager sessionManager;
    private AdminManager adminManager;
    private TextView tvAdminUsers;
    private TextView tvAdminCommerces;
    private TextView tvAdminReservations;
    private EditText etAdminUserEmail;
    private EditText etAdminCommerceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "food-waste-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        sessionManager = new SessionManager(this);
        adminManager = new AdminManager(db);

        bindViews();
        setupActions();
        refreshScreen();
    }

    private void bindViews() {
        tvAdminUsers = findViewById(R.id.tvAdminUsers);
        tvAdminCommerces = findViewById(R.id.tvAdminCommerces);
        tvAdminReservations = findViewById(R.id.tvAdminReservations);
        etAdminUserEmail = findViewById(R.id.etAdminUserEmail);
        etAdminCommerceName = findViewById(R.id.etAdminCommerceName);
    }

    private void setupActions() {
        Button btnAdminSetClient = findViewById(R.id.btnAdminSetClient);
        Button btnAdminSetMerchant = findViewById(R.id.btnAdminSetMerchant);
        Button btnAdminDeleteCommerce = findViewById(R.id.btnAdminDeleteCommerce);
        ImageButton btnBackAuth = findViewById(R.id.btnBackAuth);

        btnAdminSetClient.setOnClickListener(v -> changeUserRole("client"));
        btnAdminSetMerchant.setOnClickListener(v -> changeUserRole("merchant"));
        btnAdminDeleteCommerce.setOnClickListener(v -> deleteCommerce());
        btnBackAuth.setOnClickListener(v -> backToAuth());
    }

    private void changeUserRole(String role) {
        String email = etAdminUserEmail.getText().toString().trim();
        if (email.isEmpty()) {
            showToast("Saisis email utilisateur.");
            return;
        }
        boolean ok = adminManager.changerRoleUtilisateur(email, role);
        showToast(ok ? "Role mis a jour." : "Utilisateur introuvable.");
        refreshScreen();
    }

    private void deleteCommerce() {
        String name = etAdminCommerceName.getText().toString().trim();
        if (name.isEmpty()) {
            showToast("Saisis nom commerce.");
            return;
        }
        boolean ok = adminManager.supprimerCommerceParNom(name);
        showToast(ok ? "Commerce supprime." : "Commerce introuvable.");
        refreshScreen();
    }

    private void refreshScreen() {
        tvAdminUsers.setText(buildUsersText());
        tvAdminCommerces.setText(buildCommercesText());
        tvAdminReservations.setText(buildReservationsText());
    }

    private String buildUsersText() {
        List<User> users = adminManager.voirTousLesUtilisateurs();
        if (users.isEmpty()) return "Aucun utilisateur";
        StringBuilder sb = new StringBuilder();
        for (User u : users) {
            sb.append(u.name).append(" | ").append(u.email).append(" | role: ").append(u.role).append("\n");
        }
        return sb.toString().trim();
    }

    private String buildCommercesText() {
        List<Commerce> commerces = adminManager.voirTousLesCommerces();
        if (commerces.isEmpty()) return "Aucun commerce";
        StringBuilder sb = new StringBuilder();
        for (Commerce c : commerces) {
            sb.append(c.name).append(" | ").append(c.address).append("\n");
        }
        return sb.toString().trim();
    }

    private String buildReservationsText() {
        List<Reservation> reservations = adminManager.voirToutesLesReservations();
        if (reservations.isEmpty()) return "Aucune reservation";
        StringBuilder sb = new StringBuilder();
        for (Reservation r : reservations) {
            sb.append("Res ").append(r.id).append(" | User ").append(r.userId)
                    .append(" | Panier ").append(r.panierId).append(" | ").append(r.date).append("\n");
        }
        return sb.toString().trim();
    }

    private void backToAuth() {
        sessionManager.clearSession();
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
