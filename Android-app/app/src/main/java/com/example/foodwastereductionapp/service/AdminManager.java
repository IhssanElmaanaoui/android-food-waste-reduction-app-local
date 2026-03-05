package com.example.foodwastereductionapp.service;

import com.example.foodwastereductionapp.database.AppDatabase;
import com.example.foodwastereductionapp.model.Commerce;
import com.example.foodwastereductionapp.model.Reservation;
import com.example.foodwastereductionapp.model.User;

import java.util.List;

public class AdminManager {

    private final AppDatabase db;

    public AdminManager(AppDatabase db) {
        this.db = db;
    }

    public List<User> voirTousLesUtilisateurs() {
        return db.userDao().getAllUsers();
    }

    public boolean changerRoleUtilisateur(String email, String nouveauRole) {
        User user = db.userDao().findByEmail(email);
        if (user == null) {
            return false;
        }
        user.role = nouveauRole;
        db.userDao().update(user);
        return true;
    }

    public List<Commerce> voirTousLesCommerces() {
        return db.commerceDao().getAllCommerce();
    }

    public boolean supprimerCommerceParNom(String commerceName) {
        Commerce commerce = db.commerceDao().getByName(commerceName);
        if (commerce == null) {
            return false;
        }
        db.commerceDao().delete(commerce);
        return true;
    }

    public List<Reservation> voirToutesLesReservations() {
        return db.reservationDao().getAllReservations();
    }
}
