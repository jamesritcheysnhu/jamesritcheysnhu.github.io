package com.zybooks.eventtracker.clientmodel;

import androidx.annotation.NonNull;

// TODO: this model is to be used in place of dbmodel.Event temporarily

// TODO: Events should be owned by a particular user
//       the remote user id is derived from the JWT and stored once as any id would be,
//       as are all other remote ids
//       remote ids are null unless a connection can be made and syncs

public class Event {
    public int id;

    @NonNull
    public String title;

    public String location;

    public long datetime;  // Unix timestamp

    public String description;
}