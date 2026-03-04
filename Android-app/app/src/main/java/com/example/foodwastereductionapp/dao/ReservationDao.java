package com.example.foodwastereductionapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.foodwastereductionapp.model.Reservation;

import java.util.List;

@Dao
public interface ReservationDao {

    @Insert
    void insert(Reservation reservation);

    @Query("SELECT * FROM Reservation")
    List<Reservation> getAllReservations();
}