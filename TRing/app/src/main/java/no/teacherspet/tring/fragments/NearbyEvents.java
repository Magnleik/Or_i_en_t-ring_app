package no.teacherspet.tring.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import java.util.Collections;
import java.util.HashMap;

import connection.Event;
import connection.NetworkManager;
import connection.Point;
import no.teacherspet.tring.Database.Entities.PointOEventJoin;
import no.teacherspet.tring.Database.Entities.RoomOEvent;
import no.teacherspet.tring.Database.Entities.RoomPoint;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.OEventViewModel;
import no.teacherspet.tring.Database.ViewModels.PointOEventJoinViewModel;
import no.teacherspet.tring.Database.ViewModels.PointViewModel;
import no.teacherspet.tring.R;
import no.teacherspet.tring.activities.ListOfSavedEvents;
import no.teacherspet.tring.activities.PerformOEvent;
import no.teacherspet.tring.util.EventAdapter;
import no.teacherspet.tring.util.EventComparator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NearbyEvents.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NearbyEvents#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyEvents extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private boolean reverseAlpha;
    private boolean reversePop;
    private boolean reverseScore;
    private boolean reverseTime;

    private ListOfSavedEvents parent;
    private Event selectedEvent;
    private ListView mListView;
    private HashMap<Integer, Event> theEventReceived;
    private NetworkManager networkManager;
    private FusedLocationProviderClient lm;
    private LatLng position;
    private ArrayList<Event> listItems;
    BroadcastReceiver mReciever;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private LocalDatabase localDatabase;
    private PointViewModel pointViewModel;
    private OEventViewModel oEventViewModel;
    private PointOEventJoinViewModel pointOEventJoinViewModel;
    private EventAdapter eventAdapter;

    public NearbyEvents() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NearbyEvents.
     */
    // TODO: Rename and change types and number of parameters
    public static NearbyEvents newInstance(String param1, String param2) {
        NearbyEvents fragment = new NearbyEvents();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkManager = NetworkManager.getInstance();
        reverseAlpha = false;
        reversePop = false;
        reverseScore = false;
        reverseTime = false;
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearby_events, container, false);
    }


    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mListView = (ListView) getView().findViewById(R.id.nearby_events_list);
        ((ListOfSavedEvents) getActivity()).setActionBarTitle("Løp i nærheten");
        mReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ListOfSavedEvents.ACTION_LIST_LOADED.equals(intent.getAction())) {
                    initList();
                } else if (ListOfSavedEvents.ACTION_SORT_ALPHA.equals(intent.getAction())) {
                    sortList("event_name", reverseAlpha);
                    reverseAlpha = !reverseAlpha;
                } else if (ListOfSavedEvents.ACTION_SORT_POPULARITY.equals(intent.getAction())) {
                    sortList("popularity", reversePop);
                    reversePop = !reversePop;
                } else if (ListOfSavedEvents.ACTION_SORT_SCORE.equals(intent.getAction())) {
                    sortList("avg_score", reverseScore);
                    reverseScore = !reverseScore;
                } else if (ListOfSavedEvents.ACTION_SORT_TIME.equals(intent.getAction())) {
                    sortList("avg_time", reverseTime);
                    reverseTime = !reverseTime;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ListOfSavedEvents.ACTION_LIST_LOADED);
        filter.addAction(ListOfSavedEvents.ACTION_SORT_ALPHA);
        filter.addAction(ListOfSavedEvents.ACTION_SORT_POPULARITY);
        filter.addAction(ListOfSavedEvents.ACTION_SORT_SCORE);
        filter.addAction(ListOfSavedEvents.ACTION_SORT_TIME);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(mReciever, filter);
        ((ListOfSavedEvents) getActivity()).setActionBarTitle(getString(R.string.my_events));


        EventAdapter eventAdapter = new EventAdapter(this.getContext(), listItems);
        mListView.setAdapter(eventAdapter);

        final Context context = this.getContext();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {

                    selectedEvent = listItems.get(position);
                    saveEventToRoom(selectedEvent);
                    /*
                    Intent detailIntent = new Intent(context, PerformOEvent.class);
                    detailIntent.putExtra("MyEvent", selectedEvent);
                    startActivity(detailIntent);
                    */
                }
            }

        });


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        parent = (ListOfSavedEvents) getActivity();
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

    public void initList() {
        ArrayList<Event> listItems = new ArrayList<>();
        theEventReceived = parent.getEvents();
        if (theEventReceived != null) {
            for (Event ev : theEventReceived.values()) {
                listItems.add(ev);
            }
        }
        if (!listItems.equals(this.listItems)) {
            this.listItems = listItems;
            updateList();
        }
        //theEventReceived = new StartupMenu().getTestEvents();

    }

    private void updateList() {
        eventAdapter = new EventAdapter(this.getContext(), listItems);
        mListView.setAdapter(eventAdapter);
    }

    private void sortList(String property, boolean reversed) {
        Collections.sort(listItems, new EventComparator(property, reversed));
        eventAdapter.notifyDataSetChanged();

    }

    /**
     * Saves the Event and corresponding Points, and adds connections between them in the Room database
     * Makes sure that events and points are saved before the connections are saved. The next step
     * is only called after the previous is finished.
     */
    //TODO Call when user wants to save event from server to Room
    private void saveEventToRoom(Event event) {
        localDatabase = LocalDatabase.getInstance(this.getContext());
        pointViewModel = new PointViewModel(localDatabase.pointDAO());
        oEventViewModel = new OEventViewModel(localDatabase.oEventDAO());

        Log.d("Room", "Started saving event");
        RoomOEvent newevent = new RoomOEvent(event.getId(), event._getAllProperties());
        oEventViewModel.addOEvents(newevent).subscribe(longs -> {
            Log.d("Room", String.format("Event %d saved", event.getId()));
            savePoints(event);
        });
    }

    private void savePoints(Event event) {
        RoomPoint[] roomPoints = new RoomPoint[event.getPoints().size()];
        for (int i = 0; i < event.getPoints().size(); i++) {
            Point point = event.getPoints().get(i);
            RoomPoint roomPoint = new RoomPoint(point.getId(), point._getAllProperties(), new LatLng(point.getLatitude(), point.getLongitude()));
            roomPoints[i] = roomPoint;
        }
        pointViewModel.addPoints(roomPoints).subscribe(longs -> {
            Log.d("Room", String.format("%d points saved", longs.length));
            joinPointsToEvent(event);
        });
    }

    private void joinPointsToEvent(Event event) {
        pointOEventJoinViewModel = new PointOEventJoinViewModel(localDatabase.pointOEventJoinDAO());
        PointOEventJoin[] joins = new PointOEventJoin[event.getPoints().size()];
        for (int i = 0; i < event.getPoints().size(); i++) {
            Point point = event.getPoints().get(i);
            boolean start = i == 0;
            joins[i] = new PointOEventJoin(point.getId(), event.getId(), start, false);
        }
        pointOEventJoinViewModel.addJoins(joins).subscribe(longs -> checkSave(longs));
    }

    private void checkSave(long[] longs) {
        boolean savedAll = true;
        for (long aLong : longs) {
            if (aLong < 0) {
                savedAll = false;
            }
        }
        if (savedAll) {
            Toast.makeText(this.getContext(), "Save to phone successfull", Toast.LENGTH_SHORT).show();
            Log.d("Room", String.format("%d joins saved", longs.length));
        } else {
            Toast.makeText(this.getContext(), "Save to phone unsuccessfull", Toast.LENGTH_SHORT).show();
        }
        Intent detailIntent = new Intent(this.getContext(), PerformOEvent.class);
        detailIntent.putExtra("MyEvent", selectedEvent);
        startActivity(detailIntent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(mReciever);
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
