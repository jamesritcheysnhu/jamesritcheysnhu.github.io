package com.zybooks.eventtracker.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.zybooks.eventtracker.clientmodel.Event;
import com.zybooks.eventtracker.dbservice.ClientRepository;
import java.util.List;

public class EventListViewModel extends AndroidViewModel {
    private final ClientRepository mEventRepo;
    public List<Event> mEventListLiveData;

    public EventListViewModel(@NonNull Application application) {
        super(application);
        mEventRepo = ClientRepository.getInstance(application.getApplicationContext());
        mEventListLiveData = mEventRepo.getEvents();
    }

    public List<Event> getEvents() {
        return mEventRepo.getEvents();
    }

    public void deleteEvent(Event event) {
        mEventRepo.deleteEvent(event);
    }
}