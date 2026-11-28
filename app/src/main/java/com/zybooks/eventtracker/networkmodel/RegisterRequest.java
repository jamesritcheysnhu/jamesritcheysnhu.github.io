package com.zybooks.eventtracker.networkmodel;

// RegisterRequest: stores a readonly PLAINTEXT username and password to send to the server API
public class RegisterRequest {
    private String username;
    public String password;

    public RegisterRequest (String username, String password) {
        this.username = username;
        this.password = password;
    }
}
