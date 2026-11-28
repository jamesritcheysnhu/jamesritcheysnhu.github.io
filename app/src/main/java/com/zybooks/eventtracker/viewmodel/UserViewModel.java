package com.zybooks.eventtracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.zybooks.eventtracker.apiservice.RemoteRepository;
//import com.zybooks.eventtracker.dbmodel.User; // database layer
import com.zybooks.eventtracker.apiservice.ResponseCallback;
import com.zybooks.eventtracker.backgroundservice.MessageMap;
import com.zybooks.eventtracker.clientmodel.User; // domain layer

import java.util.Map;
import java.util.regex.Pattern;

// TODO: repo will be temporarily unused for an online-only experience.
//       future iterations will use repo as local offline mode storage.

public class UserViewModel extends AndroidViewModel {
    private static ResponseCallback sResponseCallback;

    // mRemoteRepo: the remote API access point
    private static RemoteRepository mRemoteRepo;

    // sMsgs: possible messages for the view
    private static Messages sMsgs;

    //mMsgData: error/status message sent to the view
    private static MutableLiveData<String> sMsgData;

    // mUserData: contains the user domain LiveData object
    private static MutableLiveData<User> sUserData;

    public UserViewModel (@NonNull Application app, @NonNull RemoteRepository remoteRepo) {
        super(app);

        mRemoteRepo = remoteRepo;

        if (sMsgs == null) {
            sMsgs = new Messages();
        }

        if (sUserData == null) {
            sUserData = new MutableLiveData<>();
        }

        if (sMsgData == null) {
            sMsgData = new MutableLiveData<>();
        }

        if (sResponseCallback == null) {
            sResponseCallback = new UserResponseCallback(sUserData, sMsgData);
        }
    }

    public MutableLiveData<User> register(@NonNull User user) {
        // local input validation - if fails, output error and return empty user
        if (!isUsername(user.username) || !isPassword(user.password)) {
            sUserData.setValue(User.createDefault());
            sMsgData.setValue(sMsgs.get("toast_invalid_auth"));

            return sUserData;
        }

         // attempt API call:
         // - if API call succeeds, performs callback that updates LiveData output
         // - if fails, set general error and set empty User
        try {
            mRemoteRepo.register(user, sResponseCallback);
        }
        catch (Exception ex) {
            if (!sUserData.isInitialized()) {
                sUserData.setValue(User.createDefault());
            }

            if (!sMsgData.isInitialized()) {
                sMsgData.setValue(sMsgs.get("toast_unknown_error"));
            }
        }

        return sUserData;
    }

    public MutableLiveData<User> login(@NonNull User user) {
        // local input validation - if fails, output error and return empty user
        if (!isUsername(user.username) || !isPassword(user.password)) {
            sUserData.setValue(User.createDefault());
            sMsgData.setValue(sMsgs.get("toast_invalid_auth"));

            return sUserData;
        }

        // attempt API call:
        // - if API call succeeds, performs callback that updates LiveData output
        // - if fails, set general error and set empty User
        try {
            // API repo register call creates a user if possible and returns
            mRemoteRepo.login(user, sResponseCallback);
        }
        catch (Exception ex) {
            if (!sUserData.isInitialized()) {
                sUserData.setValue(User.createDefault());
            }

            if (!sMsgData.isInitialized()) {
                sMsgData.setValue(sMsgs.get("toast_unknown_error"));
            }
        }

        return sUserData;
    }

    public MutableLiveData<String> message () {
        return sMsgData;
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
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=()-])(?=\\S+$).{8,20}";

        return Pattern.matches(pattern, password);
    }

    // UserResponseCallback: updates LiveData (preferably upon API response)
    private static class UserResponseCallback implements ResponseCallback {
        private static MutableLiveData<User> sUserData;
        private static MutableLiveData<String> sMsgData;

        public UserResponseCallback (
                @NonNull MutableLiveData<User> userData, @NonNull MutableLiveData<String> msgData)
        {
            if (sUserData == null) {
                sUserData = userData;
            }

            if (sMsgData == null) {
                sMsgData = msgData;
            }
        }

        public void onSuccess(@NonNull User user, @NonNull String message) {
            sUserData.setValue(user);
            sMsgData.setValue(message);
        }

        public void onError(@NonNull User user, @NonNull String message) {
            sUserData.setValue(user);
            sMsgData.setValue(message);
        }
    }

    // Messages: stores a map of name keys and non-UI string text values
    private static class Messages implements MessageMap {
        private final Map<String, String> mMessages;

        public Messages() {
            this.mMessages = Map.of(
                    "toast_invalid_auth", "Invalid username or password",
                    "toast_unknown_error", "Unknown error"
            );
        }

        public String get(String key) {
            return mMessages.get(key);
        }
    }
}
