package com.zybooks.eventtracker;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zybooks.eventtracker.model.Event;
import com.zybooks.eventtracker.viewmodel.EventViewModel;
import java.util.concurrent.TimeUnit;

// TODO: database operations such as adding new events should run in a background thread
public class EventEditFragment extends Fragment {
    public static final String ARG_EVENT_ID = "event_id";
    private EventViewModel mEventViewModel;
    private Event mEvent;

    public EventEditFragment() {
        // Required empty public constructor
    }

    public static EventEditFragment newInstance(Integer idParam) {
        EventEditFragment fragment = new EventEditFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_ID, idParam);
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_event_edit, container, false);

        // get the selected event
        int eventId = -1;

        // get the event ID from the fragment arguments
        Bundle args = getArguments();

        if (args != null) {
            eventId = args.getInt(ARG_EVENT_ID);
        }

        mEventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        mEvent = mEventViewModel.getEvent(eventId);

        // populate the views if there is an event to be edited
        if (mEvent != null) {
            // set title
            EditText titleEditText = rootView.findViewById(R.id.input_event_title);
            titleEditText.setText(mEvent.title);

            // set location
            EditText locationEditText = rootView.findViewById(R.id.input_event_location);
            locationEditText.setText(mEvent.location);

            // datetime must be converted from Unix time to DatePicker and TimePicker values
            DatePicker date = rootView.findViewById(R.id.input_event_date);
            TimePicker time = rootView.findViewById(R.id.input_event_time);
            EventViewModel.setPickersFromTimestamp(mEvent.datetime, date, time);

            // set description
            EditText descriptionEditText = rootView.findViewById(R.id.input_event_description);
            descriptionEditText.setText(mEvent.description);
        }

        // TODO: data binding would be a cleaner alternative to the following
        // add click callback to 'save event' button
        FloatingActionButton saveEventButton =
                (FloatingActionButton)rootView.findViewById(R.id.save_event_button);

        saveEventButton.setOnClickListener(buttonView -> {
            // set title
            EditText titleEditText = rootView.findViewById(R.id.input_event_title);
            //titleEditText.setText(mEvent.getValue().title);
            String title = titleEditText.getText().toString();

            if (title.isEmpty()) {
                Toast emptyTitleToast = Toast.makeText(
                    rootView.getContext(),
                    getString(R.string.toast_empty_title),
                    Toast.LENGTH_SHORT);
                emptyTitleToast.show();
                return;
            }

            // set location
            EditText locationEditText = rootView.findViewById(R.id.input_event_location);
            String location = locationEditText.getText().toString();

            // datetime must be converted from Unix time to DatePicker and TimePicker values
            DatePicker date = rootView.findViewById(R.id.input_event_date);
            TimePicker time = rootView.findViewById(R.id.input_event_time);
            long datetime = EventViewModel.convertPickersToTimestamp(date, time);

            // set description
            EditText descriptionEditText = rootView.findViewById(R.id.input_event_description);
            String description = descriptionEditText.getText().toString();
            // if event does not exist, create one
            if (mEvent == null) {
                mEvent = mEventViewModel.addEvent(title, location, datetime, description);
            }
            else {
                mEvent.title = title;
                mEvent.location = location;
                mEvent.datetime = datetime;
                mEvent.description = description;
                mEventViewModel.updateEvent(mEvent);
                removeEventNotification(mEvent.id);
            }

            createEventNotification(mEvent);
            Navigation.findNavController(buttonView).navigate(R.id.navigation_event_list);
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getParentFragmentManager()
            .beginTransaction().remove((Fragment)this).commitAllowingStateLoss();
    }

    private void createEventNotification(Event event) {
        long delay = event.datetime - System.currentTimeMillis();

        // if event has past, do nothing
        if (delay <= 0) {
            return;
        }

        Data data = new Data.Builder()
            .putString(EventNotificationWorker.KEY_TITLE, event.title)
            .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(EventNotificationWorker.class)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("event_" + event.id)
            .build();

        WorkManager.getInstance(requireContext()).enqueue(request);
    }

    private void removeEventNotification(long id) {
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag("event_" + id);
    }
}