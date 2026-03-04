package com.example.foodwastereductionapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.foodwastereductionapp.model.Reservation;

import java.util.List;

@Dao
public interface ReservationDao {

    @Insert
    long insert(Reservation reservation);

    @Query("SELECT * FROM Reservation")
    List<Reservation> getAllReservations();

    @Query("SELECT r.* FROM Reservation r INNER JOIN Panier p ON r.panierId = p.id WHERE p.commerceId = :commerceId")
    List<Reservation> getReservationsByCommerceId(int commerceId);
}
