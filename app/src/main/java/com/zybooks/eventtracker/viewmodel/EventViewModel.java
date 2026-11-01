package com.zybooks.eventtracker.viewmodel;

import android.app.Application;
import android.widget.DatePicker;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.zybooks.eventtracker.model.Event;
import com.zybooks.eventtracker.repo.EventRepository;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// TODO: datetime helper functions should be factored out into their own class
public class EventViewModel extends AndroidViewModel {
    private final EventRepository mEventRepo;

    public EventViewModel(@NonNull Application application) {
        super(application);
        mEventRepo = EventRepository.getInstance(application.getApplicationContext());
    }

    public Event getEvent(int id) {
        return mEventRepo.getEvent(id);
    }

    public void updateEvent(Event event) {
        mEventRepo.updateEvent(event);
    }

    public Event addEvent(
            String title, String location, long timestamp, String description)
    {
        if (title.isEmpty()) {
            return null;
        }

        Event event = new Event();
        event.title = title;
        event.location = location;
        event.datetime = timestamp;
        event.description = description;
        mEventRepo.addEvent(event);

        return event;
    }

    public static long convertPickersToTimestamp(DatePicker date, TimePicker time) {
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDayOfMonth();
        int hour = time.getHour();
        int minute = time.getMinute();

        // set the calendar
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // return the time in milliseconds since the Unix epoch
        return calendar.getTimeInMillis();
    }

    public static void setPickersFromTimestamp(long timestamp, DatePicker date, TimePicker time) {
        // set the calendar with the timestamp
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        // get the constituent calendar values
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // set the Android controls using the constituent values
        date.updateDate(year, month, day);
        time.setHour(hour);
        time.setMinute(minute);
    }

    public static String convertTimestampToString(long timestamp) {
        Date date = new Date(timestamp);

        // format date
        SimpleDateFormat formatter = new SimpleDateFormat(
            "MM/dd/yy hh:mm a", Locale.getDefault());

        return formatter.format(date);
    }
}
