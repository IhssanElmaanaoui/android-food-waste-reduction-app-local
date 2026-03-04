package com.example.foodwastereductionapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.foodwastereductionapp.dao.CommerceDao;
import com.example.foodwastereductionapp.dao.PanierDao;
import com.example.foodwastereductionapp.dao.ReservationDao;
import com.example.foodwastereductionapp.dao.UserDao;
import com.example.foodwastereductionapp.model.Commerce;
import com.example.foodwastereductionapp.model.Panier;
import com.example.foodwastereductionapp.model.Reservation;
import com.example.foodwastereductionapp.model.User;

@Database(
        entities = {User.class, Commerce.class, Panier.class, Reservation.class},
        version = 2
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract CommerceDao commerceDao();

    public abstract PanierDao panierDao();

    public abstract ReservationDao reservationDao();
}
