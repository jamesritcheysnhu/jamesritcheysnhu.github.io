package com.zybooks.eventtracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.zybooks.eventtracker.model.User;
import com.zybooks.eventtracker.repo.EventRepository;
import java.util.List;

public class UserViewModel  extends AndroidViewModel {
    private final EventRepository mEventRepo;
    private List<User> mUsers;

    public UserViewModel(@NonNull Application application) {
        super(application);
        mEventRepo = EventRepository.getInstance(application.getApplicationContext());
        mUsers = mEventRepo.getUsers();
    }

    public User findUser(String username, String password) {
        for (User user : mUsers) {
            if (user.username.equals(username) && user.password.equals(password)) {
                return user;
            }
        }

        return null;
    }

    public User findUserByUsername(String username) {
        for (User user : mUsers) {
            if (user.username.equals(username)) {
                return user;
            }
        }

        return null;
    }

    public User updateUser(User user) {
        if (user.username.isEmpty() || user.password.isEmpty()) {
            return null;
        }

        mEventRepo.updateUser(user);
        mUsers = mEventRepo.getUsers();

        return user;
    }

    public User addUser(String username, String password) {
        // if user exists, do nothing
        if (findUser(username, password) != null) {
            return null;
        }

        // if either username or password are empty, do nothing
        if (username.isEmpty() || password.isEmpty()) {
            return null;
        }

        // create a new user and add it to the repo
        User user = new User();
        user.username = username;
        user.password = password;
        user.loggedIn = true;
        mEventRepo.addUser(user);
        mUsers = mEventRepo.getUsers();

        return user;
    }
}
