package com.example.foodwastereductionapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.foodwastereductionapp.model.Panier;

import java.util.List;

@Dao
public interface PanierDao {

    @Insert
    void insert(Panier panier);

    @Update
    void update(Panier panier);

    @Delete
    void delete(Panier panier);

    @Query("SELECT * FROM Panier")
    List<Panier> getAllPaniers();
}