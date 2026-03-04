package com.example.foodwastereductionapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Commerce {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String address;
    public int ownerId; // utilisateur commerçant
}