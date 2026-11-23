package com.zybooks.eventtracker.networkmodel;

// UserResponse: the server's API response token as a readonly string and associated user data
public class UserResponse {
    private final String mToken;  // JSON Web Token
    private final User mUser;  // the remotely stored user data

    public UserResponse (String token, User user) {
        mToken = token;
        mUser = user;
    }

    public String token () {
        return mToken;
    }

    public User user () {
        return mUser;
    }
}
