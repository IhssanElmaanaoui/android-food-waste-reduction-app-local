package com.example.foodwastereductionapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.foodwastereductionapp.database.AppDatabase;
import com.example.foodwastereductionapp.model.Commerce;
import com.example.foodwastereductionapp.model.Reservation;
import com.example.foodwastereductionapp.model.User;
import com.example.foodwastereductionapp.service.MerchantManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    AppDatabase db;

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

        TextView tvResult = findViewById(R.id.tvResult);
        tvResult.setText(runMerchantDemo());
    }

    private String runMerchantDemo() {
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
        int commerceId = (int) db.commerceDao().insert(commerce);

        User client = new User();
        client.name = "Client 1";
        client.email = "client@demo.com";
        client.password = "1234";
        client.role = "client";
        int clientId = (int) db.userDao().insert(client);

        MerchantManager merchantManager = new MerchantManager(db);

        int panierId = merchantManager.ajouterPanier(commerceId, "Panier Invendu", 25.0, 5);
        merchantManager.modifierPanier(panierId, "Panier Invendu (Maj)", 22.5, 4);

        Reservation reservation = new Reservation();
        reservation.userId = clientId;
        reservation.panierId = panierId;
        reservation.date = "2026-03-04";
        db.reservationDao().insert(reservation);

        List<Reservation> reservationsRecues = merchantManager.voirReservationsRecues(commerceId);
        boolean panierSupprime = merchantManager.supprimerPanier(panierId);

        return "Panier ajoute puis modifie. Reservations recues: "
                + reservationsRecues.size()
                + " | Panier supprime: "
                + panierSupprime;
    }
}
