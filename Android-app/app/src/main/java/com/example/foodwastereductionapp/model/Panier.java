package com.example.foodwastereductionapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = @ForeignKey(
                entity = Commerce.class,
                parentColumns = "id",
                childColumns = "commerceId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("commerceId")}
)
public class Panier {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public double price;
    public int quantity;
    public int commerceId;
}
