package com.zybooks.eventtracker.clientmodel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SyncQueueItem {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;
    public String endpoint;
    public String httpMethod;
    public String jsonPayload;
    public long createdAt;
    public int retryCount;

    public SyncQueueItem(String endpoint, String httpMethod, String jsonPayload) {
        this.endpoint = endpoint;
        this.httpMethod = httpMethod;
        this.jsonPayload = jsonPayload;
        this.createdAt = System.currentTimeMillis();
        this.retryCount = 0;
    }
}
