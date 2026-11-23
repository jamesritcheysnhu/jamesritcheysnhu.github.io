package com.zybooks.eventtracker.networkmodel;

// User: the data object attached to the JSON response representing the remotely stored user
public class User {
    private final String mRemoteId;  // the virtual id of the remotely stored data subject
    private final String mUsername;  // the string identifier created by the user for their account

    public User(String remoteId, String username) {
        mRemoteId = remoteId;
        mUsername = username;
    }

    public String remoteId () {
        return mRemoteId;
    }

    public String username () {
        return mUsername;
    }
}
