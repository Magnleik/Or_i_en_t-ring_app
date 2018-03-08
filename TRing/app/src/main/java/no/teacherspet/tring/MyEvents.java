package no.teacherspet.tring;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connection.Event;
import connection.Point;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 * Use the {@link MyEvents#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyEvents extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String title = "MyEvents";
    private Event selectedEvent;
    ///////

    List<Map<String, String>> myEventList = new ArrayList<>();
    ArrayList<Event> listeFraServ = new ArrayList<>();
    HashMap<String, String> event = new HashMap<String, String>();
    ArrayList<LatLng> latLngList = new ArrayList<>();
    ///////

    private OnFragmentInteractionListener mListener;

    public MyEvents() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyEvents.
     */
    // TODO: Rename and change types and number of parameters
    public static MyEvents newInstance(String param1, String param2) {
        MyEvents fragment = new MyEvents();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        return view;

    }





    //////////////TEST
    private HashMap<String, String> addEventToList(String key, String name) {
        HashMap<String, String> event = new HashMap<String, String>();
        event.put(key, name);

        return event;
    }






    public void initList() {
        Point testPoint1 =  new Point(10.324, 20.420, "This is a test point");
        Point testPoint2 = new Point(123.321, 12.123, "Test point #2");
        Point testPoint3 = new Point(0.0, 0.0, "This is a starting point");
        ArrayList<Point> points = new ArrayList<Point>();
        points.add(testPoint1); points.add(testPoint2);
        Event testEvent = new Event();
        testEvent.addPosts(points);
        testEvent.setStartPoint(testPoint3);
        testEvent.addProperty("event_title", "Olmesterskapet");

        Event testEvent1 = new Event();
        testEvent1.addPosts(points);
        testEvent1.setStartPoint(testPoint3);
        testEvent1.addProperty("event_title","Hardangervidda rundt");

        Event testEvent2 = new Event();
        testEvent2.addPosts(points);
        testEvent2.setStartPoint(testPoint3);
        testEvent2.addProperty("event_title", "Tiiiiiidenes rebus");

        this.listeFraServ.add(testEvent) ; this.listeFraServ.add(testEvent1) ; this.listeFraServ.add(testEvent2);

        LatLng punkt = new LatLng(testPoint1.getLatitude(), testPoint1.getLongitude());
        this.latLngList.add(punkt);











        myEventList.add(addEventToList("event_title", listeFraServ.get(0).getProperty("event_title") + ""));
        myEventList.add(addEventToList("event_title", listeFraServ.get(1).getProperty("event_title") + ""));
        myEventList.add(addEventToList("event_title", listeFraServ.get(2).getProperty("event_title") + ""));



    }


    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        ListView lv = (ListView) getView().findViewById(R.id.my_events_list);




        initList();

        ListAdapter simpleAdt = new SimpleAdapter(getActivity(), myEventList, android.R.layout.simple_list_item_1, new String[] {"event_title"}, new int[] {android.R.id.text1});

        lv.setAdapter(simpleAdt);

        final Context context = this.getContext();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 1
                String selectedEventTitle = myEventList.get(position).get("event_title");

                for (Event e : listeFraServ) {
                    if (e.getProperty("event_title").equals(selectedEventTitle)) {
                        selectedEvent = e;
                    }
                }

                // 2
                Intent detailIntent = new Intent(context, PerformOEvent.class);

                // 3
                detailIntent.putParcelableArrayListExtra(selectedEvent.getProperty("event_title")+ "", latLngList  );

                // 4
                startActivity(detailIntent);
            }

        });

    }
/////////////

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
