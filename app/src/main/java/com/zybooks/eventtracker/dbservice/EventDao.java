package com.zybooks.eventtracker.dbservice;
import com.zybooks.eventtracker.clientmodel.Event;
import androidx.room.*;
import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM Event ORDER BY title ASC")
    List<Event> getEvents();

    @Query("SELECT * FROM Event WHERE id = :eventId")
    Event getEvent(int eventId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addEvent(Event event);

    @Update
    void updateEvent(Event event);

    @Delete
    void deleteEvent(Event event);
}