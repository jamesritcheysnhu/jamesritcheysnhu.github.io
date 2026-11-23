package com.zybooks.eventtracker.dbservice;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.zybooks.eventtracker.dbmodel.SyncQueueItem;
import com.zybooks.eventtracker.dbmodel.Event;
import com.zybooks.eventtracker.dbmodel.User;

@Database(entities = {Event.class, User.class, SyncQueueItem.class}, version = 2)
public abstract class ClientDatabase extends RoomDatabase {
    public abstract EventDao eventDao();
    public abstract UserDao userDao();
    public abstract SyncQueueItemDao syncQueueItemDao();
}