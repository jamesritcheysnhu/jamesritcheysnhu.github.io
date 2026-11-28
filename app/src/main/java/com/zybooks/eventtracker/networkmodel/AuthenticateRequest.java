package com.zybooks.eventtracker.networkmodel;

import com.squareup.moshi.Json;

// AuthenticateRequest: stores a readonly PLAINTEXT username and password to send to the server API
public class AuthenticateRequest {
    private String username;
    public String password;

    public AuthenticateRequest (String username, String password) {
        this.username = username;
        this.password = password;
    }
}
