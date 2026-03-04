package com.example.foodwastereductionapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.foodwastereductionapp.model.Commerce;

import java.util.List;

@Dao
public interface CommerceDao {

    @Insert
    void insert(Commerce commerce);

    @Query("SELECT * FROM Commerce")
    List<Commerce> getAllCommerce();
}