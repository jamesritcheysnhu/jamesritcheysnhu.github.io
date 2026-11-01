package com.zybooks.eventtracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
//import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zybooks.eventtracker.model.Event;
import com.zybooks.eventtracker.viewmodel.EventListViewModel;
import com.zybooks.eventtracker.viewmodel.EventViewModel;
import java.util.List;

// TODO: notification is not destroyed upon event's removal
public class EventListFragment extends Fragment {
    EventListViewModel mEventListViewModel;
    private List<Event> mEventList;

    public EventListFragment() {
        // Required empty public constructor
    }

    public static EventListFragment newInstance() {
        return new EventListFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_event_list, container, false);

        // get all events
        mEventListViewModel = new ViewModelProvider(this).get(EventListViewModel.class);
        mEventList = mEventListViewModel.getEvents();

        if (mEventList == null) {
            return rootView;
        }

        // click listener for the RecyclerView
        View.OnClickListener onClickListener = itemView -> {

            // create fragment arguments containing the selected event ID
            int selectedEventId = (int)itemView.getTag();
            Bundle args = new Bundle();
            args.putInt(EventEditFragment.ARG_EVENT_ID, selectedEventId);

            // replace list with details
            Navigation.findNavController(itemView).navigate(R.id.show_item_detail, args);
        };

        // send events to RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.event_list);

        // add a divider decoration
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(),
            DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        // add delete event handler to each item
        EventAdapter adapter = new EventAdapter(mEventList, onClickListener, event -> {
            mEventListViewModel.deleteEvent(event);
        });
        recyclerView.setAdapter(adapter);

        // add click callback to 'add new event' button
        FloatingActionButton createEventButton =
            (FloatingActionButton)rootView.findViewById(R.id.create_event_button);

        createEventButton.setOnClickListener(buttonView -> {
            //addEvent();
            // TODO: this 'clearing' of the bundle using -1 for id should be handled in a better way
            Bundle args = new Bundle();
            args.putInt(EventEditFragment.ARG_EVENT_ID, -1);
            Navigation.findNavController(buttonView).navigate(R.id.navigation_event_edit, args);
        });

        return rootView;
    }

    // interface for attaching delete listeners to each item
    public interface OnItemDeleteClickListener {
        void onDeleteClick(Event event);
    }

    private class EventAdapter extends RecyclerView.Adapter<EventHolder> {
        private List<Event> mEvents;
        private final View.OnClickListener mOnClickListener;
        private final OnItemDeleteClickListener mOnItemDeleteClickListener;

        public EventAdapter(
                List<Event> events,
                View.OnClickListener onClickListener,
                OnItemDeleteClickListener onItemDeleteClickListener)
        {
            mEvents = events;
            mOnClickListener = onClickListener;
            mOnItemDeleteClickListener = onItemDeleteClickListener;
        }

        @NonNull
        @Override
        public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new EventHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(EventHolder holder, int position) {
            Event event = mEvents.get(position);
            holder.bind(event);

            // set the Event's id for editing
            holder.itemView.setTag(event.id);

            // set RecyclerView item's event handler
            holder.itemView.setOnClickListener(mOnClickListener);

            // set delete button's event handler
            ImageView deleteButton = holder.itemView.findViewById(R.id.delete_event_button);

            deleteButton.setOnClickListener(
                button -> {
                    mOnItemDeleteClickListener.onDeleteClick(event);
                    mEventList.remove(event);
                    //notifyItemRemoved(event.id);
                    mEvents = mEventList;
                    notifyDataSetChanged();
                });
        }

        @Override
        public int getItemCount() {
            return mEvents.size();
        }
    }

    private static class EventHolder extends RecyclerView.ViewHolder {
        private final TextView mTitleTextView;
        private final TextView mDatetimeTextView;

        public EventHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_event, parent, false));

            mTitleTextView = itemView.findViewById(R.id.event_title);
            mDatetimeTextView = itemView.findViewById(R.id.event_date);
        }

        public void bind(Event event) {
            // set title
            mTitleTextView.setText(event.title);

            // set datetime
            mDatetimeTextView.setText(
                EventViewModel.convertTimestampToString(event.datetime));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getParentFragmentManager()
            .beginTransaction().remove((Fragment)this).commitAllowingStateLoss();
    }
}