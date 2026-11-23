package com.zybooks.eventtracker.dbmodel;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// TODO: remote ids store the (consistent) server db ids.
//       if network is available, store it. Otherwise it remains null.

// TODO: for offline use, if a user has never authenticated with the server, there should be a
//       local-only 'guest' account

// TODO: network check should be done on all create, update, read, delete
//       if a write is being performed,
//          if network is unavailable,
//              insert JSON request into sync queue (local db table)
//          else
//              perform JSON sending action

// TODO: create sync queue in local db that stores JSON payloads which carry the http requests
//       it can perform upon login or using a background service that checks network connection

// TODO: syncStatus informs the client whether to retrieve the remote db or no.
//       the client viewmodel reads the dbmodel (db model + keystore)
//       the networkmodel writes to repo via dbmodel
//       read:
//       if logged in:
//          perform sync queue changes
//          set sync_status to true
//       read local for view

// TODO: the client requires a deleted_at timestamp or boolean that informs the view that:
//       - it should no longer be visible to the view and irrelevant to other client logic
//       - the sync queue request should trigger a callback that deletes the item from the local db


@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;
    //public int remoteId;  // master id for sync; store once just like id

    @NonNull
    public String username;
    // TODO: the local password field will be deprecated in favor of remote password.
    //       in the interim, the local client model representing the local database will be unused.
    //       temporarily, these classes will be simple pojos with no references to the Room database
    //       in the future, the local storage will act as offline mode storage, and
    //       the local model will primarily service the view/viewmodel
    @NonNull
    public String password;
    // TODO: remove loggedIn variable - this value is not useful if determining auth status with JWT
    public boolean loggedIn;
    // TODO: JSON requests to determine specific action;
    //       syncStatus boolean check if the sync queue should be run
    //public boolean syncStatus;
}