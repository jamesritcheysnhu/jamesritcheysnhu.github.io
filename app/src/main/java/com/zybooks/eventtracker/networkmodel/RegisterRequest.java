package com.zybooks.eventtracker.networkmodel;

// RegisterRequest: stores a readonly PLAINTEXT username and password to send to the server API
public class RegisterRequest {
    private final String mUsername;
    private final String mPassword;

    public RegisterRequest (String username, String password) {
        mUsername = username;
        mPassword = password;
    }

    public String username () {
        return mUsername;
    }

    public String password () {
        return mPassword;
    }
}
