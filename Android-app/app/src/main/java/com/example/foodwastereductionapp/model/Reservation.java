package com.example.foodwastereductionapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Reservation {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public int panierId;
    public String date;
}