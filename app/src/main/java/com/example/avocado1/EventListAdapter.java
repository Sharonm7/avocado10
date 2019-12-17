package com.example.avocado1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.avocado1.R;
import com.example.avocado1.EventListAdapter;
import com.example.avocado1.ScheduledEvents;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


import java.util.List;


public class EventListAdapter extends BaseAdapter {
    private Context context;
    private List<ScheduledEvents> scheduledEvents;
    private LayoutInflater inflater;
    private String userEmail;
    private FirebaseAuth mAuth;
    private User user;
    private DatabaseReference myRef;

    public EventListAdapter(Context context, List<ScheduledEvents> scheduledEvents){
        this.context = context;
        this.scheduledEvents = scheduledEvents;
        inflater = LayoutInflater.from(this.context);
    }



    @Override
    public int getCount() {
        return scheduledEvents.size();
    }

    @Override
    public Object getItem(int i) {

        return scheduledEvents.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventHolder eventHolder;
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser fbUser = mAuth.getCurrentUser();




        if (convertView == null) {
            convertView = inflater.inflate(R.layout.event_view_layout, parent, false);
            eventHolder = new EventHolder(convertView);
            convertView.setTag(eventHolder);
        } else {
            eventHolder = (EventHolder) convertView.getTag();
        }
        ScheduledEvents scheduledEvents = (ScheduledEvents) getItem(position);

        eventHolder.eventAttendee.setText(scheduledEvents.getAttendees());
        eventHolder.eventTitle.setText(scheduledEvents.getEventSummery());
        eventHolder.eventDes.setText(scheduledEvents.getDescription());
        eventHolder.eventStart.setText(scheduledEvents.getStartDate());
        eventHolder.eventEnd.setText(scheduledEvents.getEndDate());

        System.out.println("1"+fbUser.getEmail());
        System.out.println("2"+eventHolder.eventAttendee.getText().toString());


//        if((eventHolder.eventAttendee.getText().toString())== fbUser.getEmail().toString()) {
//
//            setText(scheduledEvents,eventHolder);
//
//        }

            return convertView;
        }
//
//     public void setText(ScheduledEvents scheduledEvents, EventHolder eventHolder){
//
//         eventHolder.eventTitle.setText(scheduledEvents.getEventSummery());
//         eventHolder.eventDes.setText(scheduledEvents.getDescription());
//         //  eventHolder.eventAttendee.setText(scheduledEvents.getAttendees());
//         eventHolder.eventStart.setText(scheduledEvents.getStartDate());
//         eventHolder.eventEnd.setText(scheduledEvents.getEndDate());
//
//     }

    private class EventHolder {
        TextView eventTitle, eventDes, eventAttendee, eventStart, eventEnd, eventLocation;

        public EventHolder(View item) {
            eventTitle = (TextView) item.findViewById(R.id.eventTitle);
            eventDes = (TextView) item.findViewById(R.id.eventDes);
            eventAttendee = (TextView) item.findViewById(R.id.eventAttendee);
            eventStart = (TextView) item.findViewById(R.id.eventStart);
            eventEnd = (TextView) item.findViewById(R.id.eventEnd);
        }
    }
}
