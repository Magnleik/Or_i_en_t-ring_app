package no.teacherspet.tring.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import connection.Event;
import connection.NetworkManager;
import connection.Point;
import no.teacherspet.tring.Database.Entities.PointOEventJoin;
import no.teacherspet.tring.Database.Entities.RoomOEvent;
import no.teacherspet.tring.Database.Entities.RoomPoint;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.OEventViewModel;
import no.teacherspet.tring.Database.ViewModels.PointOEventJoinViewModel;
import no.teacherspet.tring.R;
import no.teacherspet.tring.activities.ListOfSavedEvents;
import no.teacherspet.tring.activities.PerformOEvent;
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
    private ListView mListView;
    private boolean changeEvent;
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
        changeEvent = true;
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
                if(changeEvent) {
                    Event selectedEvent = listItems.get(position);
                    Intent detailIntent = new Intent(context, PerformOEvent.class);
                    detailIntent.putExtra("MyEvent", selectedEvent);
                    startActivity(detailIntent);
                }
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                changeEvent = false;
                Event selectedEvent = listItems.get(position);
                openSettingsDialog(selectedEvent);
                return false;
            }
        });
    }

    private void openSettingsDialog(Event selectedEvent){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle(selectedEvent.getProperty("Title"));
        CharSequence[] elements = {"Slett", "Avbryt"};
        builder.setItems(elements, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        deleteEvent(selectedEvent);
                        changeEvent = true;
                        dialog.dismiss();
                        break;
                    case 1:
                        changeEvent = true;
                        dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * loadData, loadPoints and createEvent work together get all relevant data out of the
     * room database and create a new event from it
     */
    private void loadData() {
        listItems = new ArrayList<>();
        database = LocalDatabase.getInstance(this.getContext());
        oEventViewModel = new OEventViewModel(database.oEventDAO());
        joinViewModel = new PointOEventJoinViewModel(database.pointOEventJoinDAO());
        Log.d("Room","Started loading events");
        oEventViewModel.getAllOEvents().subscribe(oEvents -> loadPoints(oEvents));
    }

    private void loadPoints(List<RoomOEvent> oEvents){
        Log.d("Room",String.format("%d events found", oEvents.size()));
        if(oEvents.size()>0) {
            for (RoomOEvent event : oEvents) {
                joinViewModel.getPointsForOEvent(event.getId()).subscribe(roomPoints -> {
                    Log.d("Room",String.format("%d points found for event %d", roomPoints.size(), event.getId()));
                    if(roomPoints.size() > 0){
                        joinViewModel.getJoinsForOEvent(event.getId()).subscribe(joins -> createEvent(event, roomPoints, joins));
                    }
                });
            }
        }
        else{
            listItems = null;
            Toast.makeText(this.getContext(), "Found no locally saved events", Toast.LENGTH_SHORT).show();
        }
    }
    private void createEvent(RoomOEvent oEvent, List<RoomPoint> roomPoints, List<PointOEventJoin> joins){
        Event event = new Event();
        event._setId(oEvent.getId());
        for(String key : oEvent.getProperties().keySet()){
            event.addProperty(key, oEvent.getProperties().get(key));
        }
        for (RoomPoint roomPoint : roomPoints){
            for (PointOEventJoin join : joins){
                if(roomPoint.getId() == join.getPointID()){
                    //Point point = setupPoint(roomPoint, join.isVisited());
                    Point point = setupPoint(roomPoint, false);
                    if(join.isStart()){
                        event.setStartPoint(point);
                    }
                    else{
                        event.addPost(point);
                    }
                }
            }
        }
        Log.d("Room",String.format("Event %d created",event.getId()));
        listItems.add(event);
        updateList();
    }
    private Point setupPoint(RoomPoint roomPoint, boolean visited){
        Point point = new Point(roomPoint.getLatLng().latitude, roomPoint.getLatLng().longitude, "placeholder");
        point._setId(roomPoint.getId());
        point.setVisited(visited);
        for(String key : roomPoint.getProperties().keySet()){
            point.addProperty(key, roomPoint.getProperties().get(key));
        }
        return point;
    }

    private void updateList() {
        EventAdapter eventAdapter = new EventAdapter(this.getContext(), listItems);
        mListView.setAdapter(eventAdapter);
    }

        // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void deleteEvent(Event event){
        oEventViewModel.deleteOEvent(event.getId()).subscribe(integer -> {
            if(integer != -1){
                Log.d("Room",String.format("Event %d deleted", event.getId()));
                Toast.makeText(this.getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                listItems.remove(event);
                updateList();
            }
            else{
                Toast.makeText(this.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
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

