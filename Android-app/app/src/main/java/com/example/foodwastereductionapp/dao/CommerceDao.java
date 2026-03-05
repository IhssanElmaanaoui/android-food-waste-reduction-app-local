package com.example.foodwastereductionapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.foodwastereductionapp.model.Commerce;

import java.util.List;

@Dao
public interface CommerceDao {

    @Insert
    long insert(Commerce commerce);

    @Delete
    void delete(Commerce commerce);

    @Query("SELECT * FROM Commerce")
    List<Commerce> getAllCommerce();

    @Query("SELECT * FROM Commerce WHERE ownerId = :ownerId LIMIT 1")
    Commerce getByOwnerId(int ownerId);

    @Query("SELECT * FROM Commerce WHERE name = :name LIMIT 1")
    Commerce getByName(String name);
}
