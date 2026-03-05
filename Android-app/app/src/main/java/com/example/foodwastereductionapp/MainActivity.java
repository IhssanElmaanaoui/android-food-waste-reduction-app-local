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
import com.example.foodwastereductionapp.service.AdminManager;
import com.example.foodwastereductionapp.service.MerchantManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private MerchantManager merchantManager;
    private AdminManager adminManager;
    private int commerceId;
    private String commerceName;

    private TextView tvCommerceInfo;
    private TextView tvPaniers;
    private TextView tvReservations;
    private TextView tvAdminUsers;
    private TextView tvAdminCommerces;
    private TextView tvAdminReservations;
    private EditText etTitle;
    private EditText etPrice;
    private EditText etQuantity;
    private EditText etAdminUserEmail;
    private EditText etAdminCommerceName;

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
        adminManager = new AdminManager(db);
        bindViews();
        setupMerchantContext();
        setupAdminContext();
        setupActions();
        refreshScreen();
    }

    private void bindViews() {
        tvCommerceInfo = findViewById(R.id.tvCommerceInfo);
        tvPaniers = findViewById(R.id.tvPaniers);
        tvReservations = findViewById(R.id.tvReservations);
        tvAdminUsers = findViewById(R.id.tvAdminUsers);
        tvAdminCommerces = findViewById(R.id.tvAdminCommerces);
        tvAdminReservations = findViewById(R.id.tvAdminReservations);
        etTitle = findViewById(R.id.etTitle);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etAdminUserEmail = findViewById(R.id.etAdminUserEmail);
        etAdminCommerceName = findViewById(R.id.etAdminCommerceName);
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

    private void setupAdminContext() {
        User admin = db.userDao().getByEmail("admin@demo.com");
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

    private void setupActions() {
        Button btnAddPanier = findViewById(R.id.btnAddPanier);
        Button btnUpdatePanier = findViewById(R.id.btnUpdatePanier);
        Button btnDeletePanier = findViewById(R.id.btnDeletePanier);
        Button btnAdminSetClient = findViewById(R.id.btnAdminSetClient);
        Button btnAdminSetMerchant = findViewById(R.id.btnAdminSetMerchant);
        Button btnAdminDeleteCommerce = findViewById(R.id.btnAdminDeleteCommerce);

        btnAddPanier.setOnClickListener(v -> addPanier());
        btnUpdatePanier.setOnClickListener(v -> updatePanier());
        btnDeletePanier.setOnClickListener(v -> deletePanier());
        btnAdminSetClient.setOnClickListener(v -> changeUserRole("client"));
        btnAdminSetMerchant.setOnClickListener(v -> changeUserRole("merchant"));
        btnAdminDeleteCommerce.setOnClickListener(v -> deleteCommerce());
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
        tvAdminUsers.setText(buildUsersText());
        tvAdminCommerces.setText(buildCommercesText());
        tvAdminReservations.setText(buildAdminReservationsText());
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
        String commerceToDelete = etAdminCommerceName.getText().toString().trim();
        if (commerceToDelete.isEmpty()) {
            showToast("Saisis nom commerce.");
            return;
        }

        if (commerceToDelete.equalsIgnoreCase(commerceName)) {
            showToast("Suppression commerce actif non autorisee.");
            return;
        }

        boolean ok = adminManager.supprimerCommerceParNom(commerceToDelete);
        showToast(ok ? "Commerce supprime." : "Commerce introuvable.");
        refreshScreen();
    }

    private String buildUsersText() {
        List<User> users = adminManager.voirTousLesUtilisateurs();
        if (users.isEmpty()) {
            return "Aucun utilisateur";
        }

        StringBuilder sb = new StringBuilder();
        for (User user : users) {
            sb.append(user.name)
                    .append(" | ")
                    .append(user.email)
                    .append(" | role: ")
                    .append(user.role)
                    .append("\n");
        }
        return sb.toString().trim();
    }

    private String buildCommercesText() {
        List<Commerce> commerces = adminManager.voirTousLesCommerces();
        if (commerces.isEmpty()) {
            return "Aucun commerce";
        }

        StringBuilder sb = new StringBuilder();
        for (Commerce commerce : commerces) {
            sb.append(commerce.name)
                    .append(" | ")
                    .append(commerce.address)
                    .append("\n");
        }
        return sb.toString().trim();
    }

    private String buildAdminReservationsText() {
        List<Reservation> reservations = adminManager.voirToutesLesReservations();
        if (reservations.isEmpty()) {
            return "Aucune reservation";
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
