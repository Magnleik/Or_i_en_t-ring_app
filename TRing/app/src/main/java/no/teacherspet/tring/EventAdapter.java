package no.teacherspet.tring;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import connection.Event;

/**
 * Created by petterbjorkaas on 08/03/2018.
 */

public class EventAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Event> mDataSource;

    public EventAdapter(Context context, ArrayList<Event> events) {
        mContext = context;
        mDataSource = events;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //1
    @Override
    public int getCount() {
        return mDataSource.size();
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



        Event event = getItem(position);

        titleTextView.setText(event.getProperty("event_name")+"");


        return rowView;
    }



}
