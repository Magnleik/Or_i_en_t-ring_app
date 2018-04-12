package no.teacherspet.tring.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import connection.Event;
import connection.NetworkManager;
import connection.Point;
import no.teacherspet.tring.Database.Entities.RoomOEvent;
import no.teacherspet.tring.Database.Entities.RoomPoint;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.OEventViewModel;
import no.teacherspet.tring.Database.ViewModels.PointOEventJoinViewModel;
import no.teacherspet.tring.Database.ViewModels.PointViewModel;
import no.teacherspet.tring.activities.ListOfSavedEvents;
import no.teacherspet.tring.activities.PerformOEvent;
import no.teacherspet.tring.R;
import no.teacherspet.tring.util.EventAdapter;


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
    private Event selectedEvent;
    private ListView mListView;
    private HashMap<Integer, Event> theEventReceived;
    private NetworkManager networkManager;
    private FusedLocationProviderClient lm;
    private LatLng position;
    private LocalDatabase database;
    private OEventViewModel oEventViewModel;
    private PointOEventJoinViewModel joinViewModel;
    private ArrayList<Event> listItems;

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
        HashMap<Integer, Event> theEventReceived = new HashMap<>();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);
        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mListView = (ListView) getView().findViewById(R.id.my_events_list);
        ((ListOfSavedEvents) getActivity()).setActionBarTitle("Mine løp");
        loadData();

        EventAdapter eventAdapter = new EventAdapter(this.getContext(), listItems);
        mListView.setAdapter(eventAdapter);

        final Context context = this.getContext();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    // 1 Header takes one position --> Make sure not to start event when header is clicked and no events are available
                    Event selectedEvent = listItems.get(position - 1);
                    // 2
                    Intent detailIntent = new Intent(context, PerformOEvent.class);
                    // 3
                    detailIntent.putExtra("MyEvent", selectedEvent);
                    // 4
                    startActivity(detailIntent);
                }
            }
        });
    }
    /**
     * loadData, loadPoints and createEvent work together get all relevant data out of the
     * room database and create a new event from it
     */
    public void loadData() {
        listItems = new ArrayList<>();
        database = LocalDatabase.getInstance(this.getContext());
        oEventViewModel = new OEventViewModel(database.oEventDAO());
        joinViewModel = new PointOEventJoinViewModel(database.pointOEventJoinDAO());

        oEventViewModel.getAllOEvents().subscribe(oEvents -> loadPoints(oEvents));
    }
    private void loadPoints(List<RoomOEvent> oEvents){
        if(oEvents.size() > 0){
            for(RoomOEvent oEvent:oEvents){
                joinViewModel.getPointsForOEvent(oEvent.getId()).subscribe(roomPoints -> createEvent(oEvent, roomPoints));
            }
        }
        else{
            listItems = null;
        }
    }
    private void createEvent(RoomOEvent oEvent, List<RoomPoint> roomPoints){
        if(roomPoints.size() > 0){
            ArrayList<Point> points = new ArrayList<>();
            for(RoomPoint roomPoint : roomPoints){
                Point point = new Point(roomPoint.getLatLng().latitude, roomPoint.getLatLng().longitude, "placeholder");
                point._setId(roomPoint.getId());
                for(String key : roomPoint.getProperties().keySet()){
                    point.addProperty(key, roomPoint.getProperties().get(key));
                }
                points.add(point);
            }
            Event event = new Event();
            event._setId(oEvent.getId());
            event.setStartPoint(points.get(0));
            event.addPosts(points);
            for(String key : oEvent.getProperties().keySet()){
                event.addProperty(key, oEvent.getProperties().get(key));
            }
            listItems.add(event);
        }
    }

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

