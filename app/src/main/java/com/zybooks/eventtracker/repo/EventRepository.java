package com.zybooks.eventtracker.repo;

import com.zybooks.eventtracker.model.Event;
import com.zybooks.eventtracker.model.User;
import android.content.Context;
import androidx.room.Room;
import java.util.List;

public class EventRepository {
    private static EventRepository mEventRepo;
    private final EventDao mEventDao;
    private final UserDao mUserDao;

    public static EventRepository getInstance(Context context) {
        if (mEventRepo == null) {
            mEventRepo = new EventRepository(context);
        }

        return mEventRepo;
    }

    private EventRepository(Context context) {
        EventDatabase database =
            Room.databaseBuilder(context, EventDatabase.class, "event.db")
                .allowMainThreadQueries()
                .build();

        mEventDao = database.eventDao();
        mUserDao = database.userDao();
    }

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
}