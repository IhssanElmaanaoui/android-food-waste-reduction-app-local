package com.example.foodwastereductionapp.service;

import com.example.foodwastereductionapp.database.AppDatabase;
import com.example.foodwastereductionapp.model.Commerce;
import com.example.foodwastereductionapp.model.Panier;
import com.example.foodwastereductionapp.model.Reservation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClientManager {

    private final AppDatabase db;

    public ClientManager(AppDatabase db) {
        this.db = db;
    }

    public List<Commerce> voirCommerces() {
        return db.commerceDao().getAllCommerce();
    }

    public List<Panier> voirPaniersDisponibles(int commerceId) {
        return db.panierDao().getAvailablePaniersByCommerceId(commerceId);
    }

    public boolean reserverPanier(int userId, int panierId) {
        Panier panier = db.panierDao().getById(panierId);
        if (panier == null || panier.quantity <= 0) {
            return false;
        }

        Reservation reservation = new Reservation();
        reservation.userId = userId;
        reservation.panierId = panierId;
        reservation.date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        db.reservationDao().insert(reservation);

        panier.quantity = panier.quantity - 1;
        db.panierDao().update(panier);
        return true;
    }

    public List<Reservation> voirHistoriqueReservations(int userId) {
        return db.reservationDao().getReservationsByUserId(userId);
    }

    public boolean annulerReservation(int userId, int reservationId) {
        Reservation reservation = db.reservationDao().getById(reservationId);
        if (reservation == null || reservation.userId != userId) {
            return false;
        }

        Panier panier = db.panierDao().getById(reservation.panierId);
        if (panier != null) {
            panier.quantity = panier.quantity + 1;
            db.panierDao().update(panier);
        }

        db.reservationDao().delete(reservation);
        return true;
    }
}
