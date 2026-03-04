package com.example.foodwastereductionapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "ownerId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("ownerId")}
)
public class Commerce {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String address;
    public int ownerId; // utilisateur commerçant
}
