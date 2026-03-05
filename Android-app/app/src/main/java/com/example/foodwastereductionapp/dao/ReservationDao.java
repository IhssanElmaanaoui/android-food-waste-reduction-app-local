package com.example.foodwastereductionapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.foodwastereductionapp.model.Reservation;

import java.util.List;

@Dao
public interface ReservationDao {

    @Insert
    long insert(Reservation reservation);

    @Delete
    void delete(Reservation reservation);

    @Query("SELECT * FROM Reservation")
    List<Reservation> getAllReservations();

    @Query("SELECT r.* FROM Reservation r INNER JOIN Panier p ON r.panierId = p.id WHERE p.commerceId = :commerceId")
    List<Reservation> getReservationsByCommerceId(int commerceId);

    @Query("SELECT * FROM Reservation WHERE userId = :userId")
    List<Reservation> getReservationsByUserId(int userId);

    @Query("SELECT * FROM Reservation WHERE id = :id LIMIT 1")
    Reservation getById(int id);
}
