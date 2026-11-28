package com.zybooks.eventtracker.clientmodel;

import androidx.annotation.NonNull;

// TODO: this model is to be used in place of dbmodel.Event temporarily
// TODO: the local password field will be deprecated in favor of remote password.
//       in the interim, the local client model representing the local database will be unused.
//       temporarily, these classes will be simple pojos with no references to the Room database
//       in the future, the local storage will act as offline mode storage, and
//       the local model will primarily service the view/viewmodel

// TODO: network check should be done on all create, update, read, delete
//       if a write is being performed,
//          if network is unavailable,
//              insert JSON request into sync queue (local db table)
//          else
//              perform JSON sending action

// TODO: local id should not yet exist and so will always be null until there is a local-only db

// TODO: password must only ever be set via Viewmodel string input. Once used, it must be null.
//       the viewmodel validates the string BEFORE creating the user.
//       if the repo sends a User, upon read, it should clear the password automatically.
// User: the domain model user accessed by the rest of the application
public class User {
    public int id;  // local id
    public String remoteId;  // remote (primary) id

    @NonNull
    public String username;  // the user's username

    @NonNull
    public String password;  // the user's password

    private User (int id, String remoteId, @NonNull String username) {
        this.id = id;
        this.remoteId = remoteId;
        this.username = username;
        this.password = "";
    }

    private User () {
        this(0, "", "");
    }

    // create: returns a (mostly) complete user - note that the password is empty
    public static User create (int id, String remoteId, String username) {
        return new User(id, remoteId, username);
    }

    // createPartialUser: returns a partial user sourced locally and lacking in sensitive info
    public static User createPartial (@NonNull String username) {
        User newUser =  createDefault();

        newUser.username = username;

        return newUser;
    }

    // createDefault: returns an empty user
    public static User createDefault () {
        return new User();
    }
}