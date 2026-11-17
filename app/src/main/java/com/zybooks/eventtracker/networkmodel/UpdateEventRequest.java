package com.zybooks.eventtracker.networkmodel;

public class UpdateEventRequest {
    public String id;
    public String title;
    public String location;
    public long datetime;  // Unix timestamp
    public String description;
    //public String ownerId;  // JWT user id (online)
    //  NOTE: ownerId is perpetual per current design and doesn't need to be updated
}
