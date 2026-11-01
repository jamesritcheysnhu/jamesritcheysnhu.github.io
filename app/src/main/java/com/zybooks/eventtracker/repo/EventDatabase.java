package com.zybooks.eventtracker.repo;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.zybooks.eventtracker.model.Event;
import com.zybooks.eventtracker.model.User;

@Database(entities = {Event.class, User.class}, version = 1)
public abstract class EventDatabase extends RoomDatabase {
    public abstract EventDao eventDao();
    public abstract UserDao userDao();
}