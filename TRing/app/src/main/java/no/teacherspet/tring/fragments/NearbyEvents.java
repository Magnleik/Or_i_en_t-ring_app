package no.teacherspet.tring.fragments;

import android.content.Context;
<<<<<<< HEAD
import android.content.Intent;
=======
>>>>>>> origin/magnus/maps_layout
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;

import connection.Event;
import connection.ICallbackAdapter;
import connection.NetworkManager;
=======
import android.widget.ListView;
import android.widget.Toast;
>>>>>>> origin/magnus/maps_layout

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;

import connection.Event;
import connection.ICallbackAdapter;
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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private NetworkManager networkManager;
    private FusedLocationProviderClient lm;
    private LatLng position;
    private HashMap<Integer, Event> theEventReceived;
    private ListView mListView;

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
        HashMap<Integer, Event> theEventReceived = new HashMap<>();
        networkManager=NetworkManager.getInstance();
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

    public ArrayList<Event> initList() {
        networkManager = NetworkManager.getInstance();
        if (ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lm = LocationServices.getFusedLocationProviderClient(this.getActivity());
            lm.getLastLocation().addOnSuccessListener(this.getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    position = new LatLng(location.getLatitude(), location.getLongitude());
                    if (position.longitude==0.0 || position.latitude==0.0) {
                        ICallbackAdapter<ArrayList<Event>> adapter = new ICallbackAdapter<ArrayList<Event>>() {
                            @Override
                            public void onResponse(ArrayList<Event> object) {
                                if (object == null) {
                                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (int i = 0; i < object.size(); i++) {
                                        theEventReceived.put(object.get(i).getId(), object.get(i));
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Toast.makeText(getContext(), "Could not connect to server.", Toast.LENGTH_SHORT).show();
                            }
                        };
                        networkManager.getNearbyEvents(position.latitude, position.longitude, 3, adapter);
                    }
                }
            });
        }
        //theEventReceived = new StartupMenu().getTestEvents();
        ArrayList<Event> listItems = new ArrayList<>();
        if (theEventReceived != null) {
            for (Event ev : theEventReceived.values()) {
                listItems.add(ev);
            }
            return listItems;
        }
        return null;
    }


    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mListView = (ListView) getView().findViewById(R.id.nearby_events_list);
        ((ListOfSavedEvents) getActivity()).setActionBarTitle("Mine løp");
        final ArrayList<Event> listItems = initList();


        EventAdapter eventAdapter = new EventAdapter(this.getContext(), listItems);
        mListView.setAdapter(eventAdapter);

        final Context context = this.getContext();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

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

    public ArrayList<Event> initList() {
        networkManager = NetworkManager.getInstance();
        if (ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lm = LocationServices.getFusedLocationProviderClient(this.getActivity());
            lm.getLastLocation().addOnSuccessListener(this.getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    position = new LatLng(location.getLatitude(), location.getLongitude());
                    if (position.longitude==0.0 || position.latitude==0.0) {
                        ICallbackAdapter<ArrayList<Event>> adapter = new ICallbackAdapter<ArrayList<Event>>() {
                            @Override
                            public void onResponse(ArrayList<Event> object) {
                                if (object == null) {
                                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (int i = 0; i < object.size(); i++) {
                                        theEventReceived.put(object.get(i).getId(), object.get(i));
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Toast.makeText(getContext(), "Could not connect to server.", Toast.LENGTH_SHORT).show();
                            }
                        };
                        networkManager.getNearbyEvents(position.latitude, position.longitude, 3, adapter);
                    }
                }
            });
        }
        //theEventReceived = new StartupMenu().getTestEvents();
        ArrayList<Event> listItems = new ArrayList<>();
        if (theEventReceived != null) {
            for (Event ev : theEventReceived.values()) {
                listItems.add(ev);
            }
            return listItems;
        }
        return null;
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
