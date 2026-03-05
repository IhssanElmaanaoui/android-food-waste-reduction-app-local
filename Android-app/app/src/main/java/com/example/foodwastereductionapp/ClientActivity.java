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
import com.example.foodwastereductionapp.model.Panier;
import com.example.foodwastereductionapp.model.Reservation;
import com.example.foodwastereductionapp.service.ClientManager;

import java.util.List;

public class ClientActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private ClientManager clientManager;
    private TextView tvCommerces;
    private TextView tvPaniersDisponibles;
    private TextView tvHistorique;
    private EditText etCommerceId;
    private EditText etPanierId;
    private EditText etReservationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "food-waste-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        sessionManager = new SessionManager(this);
        clientManager = new ClientManager(db);

        bindViews();
        setupActions();
        refreshScreen();
    }

    private void bindViews() {
        tvCommerces = findViewById(R.id.tvCommerces);
        tvPaniersDisponibles = findViewById(R.id.tvPaniersDisponibles);
        tvHistorique = findViewById(R.id.tvHistorique);
        etCommerceId = findViewById(R.id.etCommerceId);
        etPanierId = findViewById(R.id.etPanierId);
        etReservationId = findViewById(R.id.etReservationId);
    }

    private void setupActions() {
        Button btnBackAuth = findViewById(R.id.btnBackAuth);
        Button btnLoadPaniers = findViewById(R.id.btnLoadPaniers);
        Button btnReserver = findViewById(R.id.btnReserver);
        Button btnAnnuler = findViewById(R.id.btnAnnuler);

        btnBackAuth.setOnClickListener(v -> backToAuth());
        btnLoadPaniers.setOnClickListener(v -> loadPaniersByCommerce());
        btnReserver.setOnClickListener(v -> reserverPanier());
        btnAnnuler.setOnClickListener(v -> annulerReservation());
    }

    private void refreshScreen() {
        tvCommerces.setText(buildCommercesText());
        tvHistorique.setText(buildHistoriqueText());
    }

    private String buildCommercesText() {
        List<Commerce> commerces = clientManager.voirCommerces();
        if (commerces.isEmpty()) {
            return "Aucun commerce";
        }

        StringBuilder sb = new StringBuilder();
        for (Commerce c : commerces) {
            sb.append("ID ").append(c.id)
                    .append(" | ").append(c.name)
                    .append(" | ").append(c.address)
                    .append("\n");
        }
        return sb.toString().trim();
    }

    private void loadPaniersByCommerce() {
        String commerceIdText = etCommerceId.getText().toString().trim();
        if (commerceIdText.isEmpty()) {
            showToast("Saisis ID commerce.");
            return;
        }

        try {
            int commerceId = Integer.parseInt(commerceIdText);
            List<Panier> paniers = clientManager.voirPaniersDisponibles(commerceId);
            if (paniers.isEmpty()) {
                tvPaniersDisponibles.setText("Aucun panier disponible.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (Panier p : paniers) {
                sb.append("ID ").append(p.id)
                        .append(" | ").append(p.title)
                        .append(" | ").append(p.price)
                        .append(" DH | qte: ").append(p.quantity)
                        .append("\n");
            }
            tvPaniersDisponibles.setText(sb.toString().trim());
        } catch (NumberFormatException e) {
            showToast("ID commerce invalide.");
        }
    }

    private void reserverPanier() {
        String panierIdText = etPanierId.getText().toString().trim();
        if (panierIdText.isEmpty()) {
            showToast("Saisis ID panier.");
            return;
        }

        try {
            int panierId = Integer.parseInt(panierIdText);
            int userId = sessionManager.getUserId();
            boolean ok = clientManager.reserverPanier(userId, panierId);
            showToast(ok ? "Reservation effectuee." : "Reservation impossible.");
            refreshScreen();
        } catch (NumberFormatException e) {
            showToast("ID panier invalide.");
        }
    }

    private String buildHistoriqueText() {
        int userId = sessionManager.getUserId();
        List<Reservation> reservations = clientManager.voirHistoriqueReservations(userId);
        if (reservations.isEmpty()) {
            return "Aucune reservation";
        }

        StringBuilder sb = new StringBuilder();
        for (Reservation r : reservations) {
            sb.append("Res ID ").append(r.id)
                    .append(" | Panier ").append(r.panierId)
                    .append(" | Date ").append(r.date)
                    .append("\n");
        }
        return sb.toString().trim();
    }

    private void annulerReservation() {
        String reservationIdText = etReservationId.getText().toString().trim();
        if (reservationIdText.isEmpty()) {
            showToast("Saisis ID reservation.");
            return;
        }

        try {
            int reservationId = Integer.parseInt(reservationIdText);
            int userId = sessionManager.getUserId();
            boolean ok = clientManager.annulerReservation(userId, reservationId);
            showToast(ok ? "Reservation annulee." : "Annulation impossible.");
            refreshScreen();
        } catch (NumberFormatException e) {
            showToast("ID reservation invalide.");
        }
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
