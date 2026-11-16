package com.zybooks.eventtracker.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// TODO: Events should be owned by a particular user

@Entity
public class Event {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @NonNull
    public String title;

    public String location;
    public long datetime; // Unix timestamp
    public String description;
}