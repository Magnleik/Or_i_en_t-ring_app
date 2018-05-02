package no.teacherspet.tring.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import connection.Event;
import no.teacherspet.tring.R;

/**
 * Created by petterbjorkaas on 08/03/2018.
 */

public class EventAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Event> mDataSource;

    public EventAdapter(Context context, ArrayList<Event> events) {
        mDataSource = events;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //1
    @Override
    public int getCount() {
        if(mDataSource!=null) {
            return mDataSource.size();
        }
        return 0;
    }

    //2
    @Override
    public Event getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.list_item_event, parent, false);

        TextView titleTextView = (TextView) rowView.findViewById(R.id.list_item_event_title);
        TextView averageDistanceValue = (TextView) rowView.findViewById(R.id.avg_dist);
        TextView averageTimeTextView = (TextView) rowView.findViewById(R.id.avg_time);



        Event event = getItem(position);

        if ((event.getProperty("dist") != null)) {
            int distance = (int) Math.round(event.getMinDistance());

            int m = 0;
            double km = 0;

            //Kalkuler km osv
            if (distance > 1000) {
                km = (double) distance /1000;
                m = (distance%1000)/1000;

                String numberAsString = String.format("%.1f", km);
                averageDistanceValue.setText(numberAsString + "km");

            } else {
                m=distance;
                averageDistanceValue.setText((m + "m"));
            }

        };

        if ((event.getProperty("avg_time") != null)) {

            averageTimeTextView.setText(event.getProperty("avg_time"));
        }

        titleTextView.setText(event.getProperty("event_name"));


        return rowView;
    }



}
