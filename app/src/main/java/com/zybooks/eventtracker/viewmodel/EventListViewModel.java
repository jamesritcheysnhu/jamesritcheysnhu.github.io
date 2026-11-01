package com.zybooks.eventtracker.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.zybooks.eventtracker.model.Event;
import com.zybooks.eventtracker.repo.EventRepository;
import java.util.List;

public class EventListViewModel extends AndroidViewModel {
    private final EventRepository mEventRepo;
    public List<Event> mEventListLiveData;

    public EventListViewModel(@NonNull Application application) {
        super(application);
        mEventRepo = EventRepository.getInstance(application.getApplicationContext());
        mEventListLiveData = mEventRepo.getEvents();
    }

    public List<Event> getEvents() {
        return mEventRepo.getEvents();
    }

    public void deleteEvent(Event event) {
        mEventRepo.deleteEvent(event);
    }
}