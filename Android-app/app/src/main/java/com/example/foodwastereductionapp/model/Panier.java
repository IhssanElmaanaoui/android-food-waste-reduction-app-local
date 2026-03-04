package com.example.foodwastereductionapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Panier {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public double price;
    public int quantity;
    public int commerceId;
}