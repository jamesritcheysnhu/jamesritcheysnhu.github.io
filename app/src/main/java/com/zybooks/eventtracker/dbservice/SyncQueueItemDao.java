package com.zybooks.eventtracker.dbservice;

import androidx.room.*;
import java.util.List;
import com.zybooks.eventtracker.dbmodel.SyncQueueItem;

// SyncQueueItemDao: interface for interacting with offline request sync queue
@Dao
public interface SyncQueueItemDao {
    // addSyncQueueItem(): add a pending request to the sync queue
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addSyncQueueItem(SyncQueueItem item);

    // getSyncQueueItems(): return the pending request list
    @Query("SELECT * FROM SyncQueueItem ORDER BY createdAt ASC")
    List<SyncQueueItem> getSyncQueueItems();

    // incrementRetryCount(): set the number of times a user connection attempt is made
    @Query("UPDATE SyncQueueItem SET retryCount = retryCount + 1 WHERE id = :id")
    void incrementRetryCount(long id);

    // deleteSyncQueueItem(): remove the finished request from the sync queue
    @Delete
    void deleteSyncQueueItem(SyncQueueItem item);
}
