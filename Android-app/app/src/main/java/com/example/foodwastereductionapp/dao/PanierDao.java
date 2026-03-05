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
    long insert(Panier panier);

    @Update
    void update(Panier panier);

    @Delete
    void delete(Panier panier);

    @Query("SELECT * FROM Panier")
    List<Panier> getAllPaniers();

    @Query("SELECT * FROM Panier WHERE commerceId = :commerceId")
    List<Panier> getPaniersByCommerceId(int commerceId);

    @Query("SELECT * FROM Panier WHERE id = :id LIMIT 1")
    Panier getById(int id);

    @Query("SELECT * FROM Panier WHERE commerceId = :commerceId AND title = :title LIMIT 1")
    Panier getByCommerceIdAndTitle(int commerceId, String title);
}
