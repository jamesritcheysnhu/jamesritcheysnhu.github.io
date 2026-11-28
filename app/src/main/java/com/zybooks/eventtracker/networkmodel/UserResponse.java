package com.zybooks.eventtracker.networkmodel;

// UserResponse: the server's API response token as a readonly string and associated user data

/* JSON output:
"success": boolean,
    "msg": string,
    "token": string,
    "user": {
        "id": string,
        "username": string
    }
 */
public class UserResponse {
    public boolean success;  // responded successfully
    public String msg;  // contains notices and errors
    public String token;  // JSON Web Token
    public User user;  // the remotely stored user data
}
