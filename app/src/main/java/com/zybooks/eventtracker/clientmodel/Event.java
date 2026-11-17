package com.zybooks.eventtracker.clientmodel;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// TODO: Events should be owned by a particular user
//       the remote user id is derived from the JWT and stored once as any id would be,
//       as are all other remote ids
//       remote ids are null unless a connection can be made and syncs

@Entity
public class Event {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;
    //public int remoteId;  // master id for sync; store once just like id
    @NonNull
    public String title;

    public String location;
    public long datetime;  // Unix timestamp
    public String description;
    //public String ownerId;  // JWT user id (online)
    //public int localOwnerId;  // local db user id (offline)
}