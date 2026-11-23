package com.zybooks.eventtracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zybooks.eventtracker.R;
import com.zybooks.eventtracker.apiservice.RemoteRepository;
//import com.zybooks.eventtracker.dbmodel.User; // database layer
import com.zybooks.eventtracker.clientmodel.User; // domain layer

import java.util.regex.Pattern;

// TODO: viewmodel scope creep: the findUser, findUserByUsername, and other controller-like
//       methods should be refactored or removed - the functions on the viewmodel should adhere more
//       to the view's functions and data, e.g. username field, pw field, submit button.
//       Viewmodels should represent UI elements, data required by view, and client-side validation.

// TODO: repo will be temporarily unused for an online-only experience.
//       future iterations will use repo as local offline mode storage.

// TODO: a viewmodel's data includes those errors which may be outputted to the toast.
//       set the viewmodel's error output attribute
public class UserViewModel extends AndroidViewModel {
    private final RemoteRepository mRemoteRepo;

    private String mUsername;  // user's string identifier for visualization, 2nd to remoteId
    private String mPassword;  // plaintext password to be transmitted (hopefully via HTTPS POST)

    private String mNotification;  // TODO: user data status

    public UserViewModel (@NonNull Application application, RemoteRepository remoteRepo) {
        super(application);
        mRemoteRepo = remoteRepo;
        mUsername = "";
        mPassword = "";
        mNotification = "";
    }

    /**
     * https://www.geeksforgeeks.org/javascript/username-validation-in-js-regex/
     * isUsername:
     * - must be between 8 and 20 chars long
     * - must begin with a letter
     */
    private boolean isUsername (String username) {
        String pattern = "[a-zA-Z][a-zA-Z0-9_]{7,19}";

        return Pattern.matches(pattern, username);
    }

    /**
     * https://www.geeksforgeeks.org/java/how-to-validate-a-password-using-regular-expressions-in-java/
     * isPassword:
     * - contains at least 8 characters and at most 20 characters
     * - contains at least one digit
     * - contains at least one upper case alphabet
     * - contains at least one lower case alphabet
     * - contains at least one special character which includes !@#$%&*()-+=^
     * - doesn't contain any white space
     */
    private boolean isPassword (String password) {
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=()-])(?=\\S+$).{8,20}";;

        return Pattern.matches(pattern, password);
    }

    public User register(@NonNull User newUser) {
        if (!isUsername(newUser.username) || !isPassword(newUser.password)) {
            return null;
        }

        User user = null;

        try {
            // API repo register call creates a user if possible and returns or else returns null
            user = mRemoteRepo.register(newUser);
        }
        catch (Exception ex) {
            return null;
        }

        if (user != null) {
            mUsername = user.username;
        }

        return user;
    }

    public User login (User authenticatingUser) {
        if (!isUsername(authenticatingUser.username) || !isPassword(authenticatingUser.password)) {
            return null;
        }

        User user = null;

        try {
            // API repo register call creates a user if possible and returns or else returns null
            user = mRemoteRepo.login(authenticatingUser);
        }
        catch (Exception ex) {
            return null;
        }

        if (user != null) {
            mUsername = user.username;
        }
        return user;
    }
}
