package com.example.foodwastereductionapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.foodwastereductionapp.database.AppDatabase;
import com.example.foodwastereductionapp.model.Commerce;
import com.example.foodwastereductionapp.model.Panier;
import com.example.foodwastereductionapp.model.Reservation;
import com.example.foodwastereductionapp.model.User;
import com.example.foodwastereductionapp.service.MerchantManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
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
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "food-waste-db"
        ).allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

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
        List<Commerce> commerces = db.commerceDao().getAllCommerce();
        if (!commerces.isEmpty()) {
            commerceId = commerces.get(0).id;
            commerceName = commerces.get(0).name;
            return;
        }

        User merchant = new User();
        merchant.name = "Marchand 1";
        merchant.email = "marchand@demo.com";
        merchant.password = "1234";
        merchant.role = "merchant";
        int merchantId = (int) db.userDao().insert(merchant);

        Commerce commerce = new Commerce();
        commerce.name = "Boulangerie Demo";
        commerce.address = "Rue 1";
        commerce.ownerId = merchantId;
        commerceId = (int) db.commerceDao().insert(commerce);
        commerceName = commerce.name;
    }

    private void setupActions() {
        Button btnAddPanier = findViewById(R.id.btnAddPanier);
        Button btnUpdatePanier = findViewById(R.id.btnUpdatePanier);
        Button btnDeletePanier = findViewById(R.id.btnDeletePanier);

        btnAddPanier.setOnClickListener(v -> addPanier());
        btnUpdatePanier.setOnClickListener(v -> updatePanier());
        btnDeletePanier.setOnClickListener(v -> deletePanier());
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
            int panierId = merchantManager.ajouterPanier(commerceId, title, price, quantity);
            showToast("Panier ajoute (ID: " + panierId + ")");
            clearInput();
            refreshScreen();
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
            showToast(ok ? "Panier modifie." : "Panier introuvable.");
            refreshScreen();
        } catch (NumberFormatException e) {
            showToast("Prix ou quantite invalide.");
        }
    }

    private void deletePanier() {
        String title = etTitle.getText().toString().trim();
        if (title.isEmpty()) {
            showToast("Saisis le titre du panier a supprimer.");
            return;
        }

        boolean ok = merchantManager.supprimerPanier(commerceId, title);
        showToast(ok ? "Panier supprime." : "Panier introuvable.");
        clearInput();
        refreshScreen();
    }

    private void refreshScreen() {
        tvCommerceInfo.setText("Commerce: " + commerceName);
        tvPaniers.setText(buildPaniersText());
        tvReservations.setText(buildReservationsText());
    }

    private String buildPaniersText() {
        List<Panier> paniers = db.panierDao().getPaniersByCommerceId(commerceId);
        if (paniers.isEmpty()) {
            return "Aucun panier";
        }

        StringBuilder sb = new StringBuilder();
        for (Panier panier : paniers) {
            sb.append(panier.title)
                    .append(" | ")
                    .append(panier.price)
                    .append(" DH | qte: ")
                    .append(panier.quantity)
                    .append("\n");
        }
        return sb.toString().trim();
    }

    private String buildReservationsText() {
        List<Reservation> reservations = merchantManager.voirReservationsRecues(commerceId);
        if (reservations.isEmpty()) {
            return "Aucune reservation recue";
        }

        StringBuilder sb = new StringBuilder();
        for (Reservation reservation : reservations) {
            sb.append("Res ID ")
                    .append(reservation.id)
                    .append(" | User ")
                    .append(reservation.userId)
                    .append(" | Panier ")
                    .append(reservation.panierId)
                    .append(" | Date ")
                    .append(reservation.date)
                    .append("\n");
        }
        return sb.toString().trim();
    }

    private void clearInput() {
        etTitle.setText("");
        etPrice.setText("");
        etQuantity.setText("");
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
