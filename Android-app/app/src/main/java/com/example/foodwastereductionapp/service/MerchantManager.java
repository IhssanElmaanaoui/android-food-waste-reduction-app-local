package com.example.foodwastereductionapp.service;

import com.example.foodwastereductionapp.database.AppDatabase;
import com.example.foodwastereductionapp.model.Panier;
import com.example.foodwastereductionapp.model.Reservation;

import java.util.List;

public class MerchantManager {

    private final AppDatabase db;

    public MerchantManager(AppDatabase db) {
        this.db = db;
    }

    public int ajouterPanier(int commerceId, String titre, double prix, int quantite) {
        Panier panier = new Panier();
        panier.commerceId = commerceId;
        panier.title = titre;
        panier.price = prix;
        panier.quantity = quantite;
        return (int) db.panierDao().insert(panier);
    }

    public boolean modifierPanier(int commerceId, String titre, double prix, int quantite) {
        Panier panier = db.panierDao().getByCommerceIdAndTitle(commerceId, titre);
        if (panier == null) {
            return false;
        }

        panier.price = prix;
        panier.quantity = quantite;
        db.panierDao().update(panier);
        return true;
    }

    public boolean supprimerPanier(int commerceId, String titre) {
        Panier panier = db.panierDao().getByCommerceIdAndTitle(commerceId, titre);
        if (panier == null) {
            return false;
        }
        db.panierDao().delete(panier);
        return true;
    }

    public List<Reservation> voirReservationsRecues(int commerceId) {
        return db.reservationDao().getReservationsByCommerceId(commerceId);
    }
}
