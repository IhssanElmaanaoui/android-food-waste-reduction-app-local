package com.example.foodwastereductionapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Panier.class,
                        parentColumns = "id",
                        childColumns = "panierId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index("userId"), @Index("panierId")}
)
public class Reservation {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public int panierId;
    public String date;
}
