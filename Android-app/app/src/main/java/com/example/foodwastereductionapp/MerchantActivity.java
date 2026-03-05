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
import com.example.foodwastereductionapp.model.User;
import com.example.foodwastereductionapp.service.MerchantManager;

import java.util.List;

public class MerchantActivity extends AppCompatActivity {

    private AppDatabase db;
    private SessionManager sessionManager;
    private MerchantManager merchantManager;
    private int commerceId;
    private String commerceName;

    private TextView tvCommerceInfo;
    private TextView tvPaniers;
    private TextView tvReservations;
    private EditText etTitle;
    private EditText etPrice;
    private EditText etQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "food-waste-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        sessionManager = new SessionManager(this);
        merchantManager = new MerchantManager(db);

        bindViews();
        setupMerchantContext();
        setupActions();
        refreshScreen();
    }

    private void bindViews() {
        tvCommerceInfo = findViewById(R.id.tvCommerceInfo);
        tvPaniers = findViewById(R.id.tvPaniers);
        tvReservations = findViewById(R.id.tvReservations);
        etTitle = findViewById(R.id.etTitle);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
    }

    private void setupMerchantContext() {
        int userId = sessionManager.getUserId();
        Commerce commerce = db.commerceDao().getByOwnerId(userId);
        if (commerce != null) {
            commerceId = commerce.id;
            commerceName = commerce.name;
            return;
        }

        User user = db.userDao().getById(userId);
        Commerce newCommerce = new Commerce();
        newCommerce.name = user != null ? "Commerce de " + user.name : "Mon commerce";
        newCommerce.address = "Adresse a definir";
        newCommerce.ownerId = userId;
        commerceId = (int) db.commerceDao().insert(newCommerce);
        commerceName = newCommerce.name;
    }

    private void setupActions() {
        Button btnAddPanier = findViewById(R.id.btnAddPanier);
        Button btnUpdatePanier = findViewById(R.id.btnUpdatePanier);
        Button btnDeletePanier = findViewById(R.id.btnDeletePanier);
        Button btnBackAuth = findViewById(R.id.btnBackAuth);

        btnAddPanier.setOnClickListener(v -> addPanier());
        btnUpdatePanier.setOnClickListener(v -> updatePanier());
        btnDeletePanier.setOnClickListener(v -> deletePanier());
        btnBackAuth.setOnClickListener(v -> backToAuth());
    }

    private void addPanier() {
        String title = etTitle.getText().toString().trim();
        String priceText = etPrice.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();

        if (title.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
            showToast("Remplis titre, prix et quantite.");
            return;
        }
        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);
            merchantManager.ajouterPanier(commerceId, title, price, quantity);
            clearInput();
            refreshScreen();
            showToast("Panier ajoute.");
        } catch (NumberFormatException e) {
            showToast("Prix ou quantite invalide.");
        }
    }

    private void updatePanier() {
        String title = etTitle.getText().toString().trim();
        String priceText = etPrice.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();

        if (title.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
            showToast("Remplis titre, prix et quantite.");
            return;
        }
        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);
            boolean ok = merchantManager.modifierPanier(commerceId, title, price, quantity);
            refreshScreen();
            showToast(ok ? "Panier modifie." : "Panier introuvable.");
        } catch (NumberFormatException e) {
            showToast("Prix ou quantite invalide.");
        }
    }

    private void deletePanier() {
        String title = etTitle.getText().toString().trim();
        if (title.isEmpty()) {
            showToast("Saisis le titre du panier.");
            return;
        }
        boolean ok = merchantManager.supprimerPanier(commerceId, title);
        clearInput();
        refreshScreen();
        showToast(ok ? "Panier supprime." : "Panier introuvable.");
    }

    private void refreshScreen() {
        tvCommerceInfo.setText("Commerce: " + commerceName);
        tvPaniers.setText(buildPaniersText());
        tvReservations.setText(buildReservationsText());
    }

    private String buildPaniersText() {
        List<Panier> paniers = db.panierDao().getPaniersByCommerceId(commerceId);
        if (paniers.isEmpty()) return "Aucun panier";
        StringBuilder sb = new StringBuilder();
        for (Panier p : paniers) {
            sb.append(p.title).append(" | ").append(p.price).append(" DH | qte: ").append(p.quantity).append("\n");
        }
        return sb.toString().trim();
    }

    private String buildReservationsText() {
        List<Reservation> reservations = merchantManager.voirReservationsRecues(commerceId);
        if (reservations.isEmpty()) return "Aucune reservation recue";
        StringBuilder sb = new StringBuilder();
        for (Reservation r : reservations) {
            sb.append("Res ").append(r.id).append(" | User ").append(r.userId)
                    .append(" | Panier ").append(r.panierId).append(" | ").append(r.date).append("\n");
        }
        return sb.toString().trim();
    }

    private void clearInput() {
        etTitle.setText("");
        etPrice.setText("");
        etQuantity.setText("");
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
