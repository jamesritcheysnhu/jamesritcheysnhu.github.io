package com.zybooks.eventtracker.dbservice;

import com.zybooks.eventtracker.clientmodel.Event;
import com.zybooks.eventtracker.clientmodel.User;
import com.zybooks.eventtracker.clientmodel.SyncQueueItem;
import android.content.Context;
import androidx.room.Room;
import java.util.List;

public class ClientRepository {
    private static ClientRepository mClientRepo;
    private final EventDao mEventDao;
    private final UserDao mUserDao;
    private final SyncQueueItemDao mSyncQueueItemDao;

    public static ClientRepository getInstance(Context context) {
        if (mClientRepo == null) {
            mClientRepo = new ClientRepository(context);
        }

        return mClientRepo;
    }

    private ClientRepository(Context context) {
        ClientDatabase database =
            Room.databaseBuilder(context, ClientDatabase.class, "event.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration(true)
                .build();

        mEventDao = database.eventDao();
        mUserDao = database.userDao();
        mSyncQueueItemDao = database.syncQueueItemDao();
    }

    //// Events ////
    public void addEvent(Event event) {
        mEventDao.addEvent(event);
    }

    public void updateEvent(Event event) {
        mEventDao.updateEvent(event);
    }

    public Event getEvent(int id) {
        return mEventDao.getEvent(id);
    }

    public List<Event> getEvents() {
        return mEventDao.getEvents();
    }

    public void deleteEvent(Event event) {
        mEventDao.deleteEvent(event);
    }

    //// Users ////
    public void addUser(User user) {
        mUserDao.addUser(user);
    }

    public void updateUser(User user) {
        mUserDao.updateUser(user);
    }

    public User getUser(int id) {
        return mUserDao.getUser(id);
    }

    public List<User> getUsers() {
        return mUserDao.getUsers();
    }

    public void deleteUser(User user) {
        mUserDao.deleteUser(user);
    }

    //// SyncQueueItems ////
    public void addSyncQueueItem(SyncQueueItem syncQueueItem) {
        mSyncQueueItemDao.addSyncQueueItem(syncQueueItem);
    }

    public List<SyncQueueItem> getSyncQueueItems () {
        return mSyncQueueItemDao.getSyncQueueItems();
    }

    public void incrementRetryCount (long id) {
        mSyncQueueItemDao.incrementRetryCount(id);
    }

    public void deleteSyncQueueItem (SyncQueueItem syncQueueItem) {
        mSyncQueueItemDao.deleteSyncQueueItem(syncQueueItem);
    }
}