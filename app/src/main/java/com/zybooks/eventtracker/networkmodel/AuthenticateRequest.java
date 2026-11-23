package com.zybooks.eventtracker.networkmodel;

import com.squareup.moshi.Json;

// AuthenticateRequest: stores a readonly PLAINTEXT username and password to send to the server API
public class AuthenticateRequest {
    private final String mUsername;
    private final String mPassword;

    public AuthenticateRequest (String username, String password) {
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
