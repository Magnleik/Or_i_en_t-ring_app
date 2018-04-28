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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import connection.Event;
import connection.NetworkManager;
import no.teacherspet.tring.R;
import no.teacherspet.tring.activities.ListOfSavedEvents;
import no.teacherspet.tring.activities.PerformOEvent;
import no.teacherspet.tring.util.EventAdapter;


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

    private Event selectedEvent;
    private ListView mListView;
    private HashMap<Integer, Event> theEventReceived;
    private NetworkManager networkManager;
    private FusedLocationProviderClient lm;
    private LatLng position;
    private ArrayList<Event> listItems;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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
        BroadcastReceiver mReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(ListOfSavedEvents.ACTION_LIST_LOADED.equals(intent.getAction())){
                    initList();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ListOfSavedEvents.ACTION_LIST_LOADED);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(mReciever,filter);


        EventAdapter eventAdapter = new EventAdapter(this.getContext(), listItems);
        mListView.setAdapter(eventAdapter);

        final Context context = this.getContext();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {

                    Event selectedEvent = listItems.get(position);

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
        ListOfSavedEvents parent = (ListOfSavedEvents) getActivity();
        listItems = new ArrayList<>();
        theEventReceived = parent.getEvents();
        if (theEventReceived != null) {
            for (Event ev : theEventReceived.values()) {
                listItems.add(ev);
            }
        }
        parent.stopLocationRequest();
        updateList();
        //theEventReceived = new StartupMenu().getTestEvents();

    }

    private void updateList() {
        EventAdapter eventAdapter = new EventAdapter(this.getContext(), listItems);
        mListView.setAdapter(eventAdapter);
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
