package no.teacherspet.tring.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.List;

import connection.Event;
import connection.ICallbackAdapter;
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
import no.teacherspet.tring.util.RoomSaveAndLoad;
import no.teacherspet.tring.util.RoomInteract;
import no.teacherspet.tring.util.EventComparator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 * Use the {@link MyEvents#newInstance} factory method to
 */
public class MyEvents extends Fragment implements RoomInteract {

    private ListView mListView;
    private Event selectedEvent;
    private boolean changeEvent;

    private boolean reverseAlpha;
    private boolean reversePop;
    private boolean reverseScore;
    private boolean reverseTime;

    private BroadcastReceiver mReciever;
    private EventAdapter eventAdapter;

    private OEventViewModel oEventViewModel;
    private ArrayList<Event> listItems;
    private RoomSaveAndLoad roomSaveAndLoad;

    private OnFragmentInteractionListener mListener;

    public MyEvents() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyEvents.
     */
    public static MyEvents newInstance() {
        MyEvents fragment = new MyEvents();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reverseAlpha = false;
        reversePop = false;
        reverseScore = false;
        reverseTime = false;

        listItems = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(), listItems);
        roomSaveAndLoad = new RoomSaveAndLoad(getContext(), this);
        changeEvent = true;
        HashMap<Integer, Event> theEventReceived = new HashMap<>();
        LocalDatabase database = LocalDatabase.getInstance(this.getContext());
        oEventViewModel = new OEventViewModel(database.oEventDAO());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);
        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mListView = (ListView) getView().findViewById(R.id.my_events_list);

        eventAdapter = new EventAdapter(this.getContext(), listItems);
        mListView.setAdapter(eventAdapter);

        mReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ListOfSavedEvents.ACTION_SORT_ALPHA.equals(intent.getAction())) {
                    sortList("event_name", reverseAlpha);
                    reverseAlpha = !reverseAlpha;
                } else if (ListOfSavedEvents.ACTION_SORT_POPULARITY.equals(intent.getAction())) {
                    sortList("popularity", reversePop);
                    reversePop = !reversePop;
                } else if (ListOfSavedEvents.ACTION_SORT_DIST.equals(intent.getAction())) {
                    sortList("dist", reverseScore);
                    reverseScore = !reverseScore;
                } else if (ListOfSavedEvents.ACTION_SORT_TIME.equals(intent.getAction())) {
                    sortList("avg_time", reverseTime);
                    reverseTime = !reverseTime;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ListOfSavedEvents.ACTION_SORT_ALPHA);
        filter.addAction(ListOfSavedEvents.ACTION_SORT_POPULARITY);
        filter.addAction(ListOfSavedEvents.ACTION_SORT_DIST);
        filter.addAction(ListOfSavedEvents.ACTION_SORT_TIME);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(mReciever, filter);
        ((ListOfSavedEvents) getActivity()).setActionBarTitle(getString(R.string.my_events));

        //Get subscribed events from server
        NetworkManager.getInstance().getSubscribedEvents(new ICallbackAdapter<List<Event>>() {
            @Override
            public void onResponse(List<Event> object) {
                if (object != null) {
                    if (object.size() > 0) {
                        listItems.clear();
                        listItems.addAll(object);
                        eventAdapter.notifyDataSetChanged();

                        oEventViewModel.getActiveEvent().subscribe(activeEvents -> {
                            oEventViewModel.getAllOEvents().subscribe(roomOEvents -> {
                                ArrayList<Integer> ids = new ArrayList<>();
                                for(RoomOEvent roomOEvent : roomOEvents){
                                    ids.add(roomOEvent.getId());
                                }
                                int activeId;
                                if(!activeEvents.isEmpty()){
                                    activeId = activeEvents.get(0).getId();
                                }
                                else{
                                    activeId = -1;
                                }
                                for (Event event : object) {
                                    if(ids.contains(event.getId())){
                                        ids.remove(ids.indexOf(event.getId()));
                                    }
                                    if(event.getId() != activeId){
                                        roomSaveAndLoad.saveRoomEvent(event);
                                    }
                                }
                                for(Integer id : ids){
                                    if(id != activeId){
                                        oEventViewModel.deleteOEvent(id);
                                    }
                                }

                            });
                        });

                    } else {
                        loadData();
                    }
                } else {
                    loadData();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                loadData();
            }
        });

        final Context context = this.getContext();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (changeEvent) {
                    oEventViewModel.getActiveEvent().subscribe(roomOEvents -> {
                        selectedEvent = listItems.get(position);
                        if (roomOEvents.isEmpty()) {
                            startEvent();
                        }
                        else {
                            openOverrideDialog();
                        }
                    });
                }
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                changeEvent = false;
                selectedEvent = listItems.get(position);
                openSettingsDialog();
                return false;
            }
        });
    }

    private void startEvent() {
        Intent detailIntent = new Intent(getContext(), PerformOEvent.class);
        detailIntent.putExtra("MyEvent", selectedEvent);
        startActivity(detailIntent);
    }

    private void openOverrideDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle(getString(R.string.event_in_progress));
        builder.setMessage(getString(R.string.cancel_last_event));
        CharSequence[] elements = {getString(R.string.cancel), getString(R.string.proceed)};
        builder.setPositiveButton(getString(R.string.proceed), (dialog, which) -> {
            startEvent();
            dialog.dismiss();
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sortList(String property, boolean reversed) {
        Collections.sort(listItems, new EventComparator(property, reversed));
        eventAdapter.notifyDataSetChanged();
    }

    private void openSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle(selectedEvent.getProperty("Title"));
        CharSequence[] elements = {getString(R.string.delete), getString(R.string.cancel)};
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
        Log.d("Room", "Started loading events");
        oEventViewModel.getAllOEvents().subscribe(oEvents -> createEvents(oEvents));
    }

    private void createEvents(List<RoomOEvent> oEvents) {
        Log.d("Room", String.format("%d events found", oEvents.size()));
        if (oEvents.size() > 0) {
            for (RoomOEvent event : oEvents) {
                roomSaveAndLoad.reconstructEvent(event);
            }
        } else {
            listItems = null;
            Toast.makeText(this.getContext(), R.string.found_no_locally_saved_events, Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void deleteEvent(Event event) {
        oEventViewModel.deleteOEvent(event.getId()).subscribe(integer -> {
            if (integer != -1) {
                Log.d("Room", String.format("Event %d deleted", event.getId()));
                Toast.makeText(this.getContext(), R.string.event_deleted, Toast.LENGTH_SHORT).show();
                listItems.remove(event);
                eventAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this.getContext(), R.string.something_wrong_toast, Toast.LENGTH_SHORT).show();
            }
        });
        NetworkManager.getInstance().unsubscribeFromEvent(event.getId(), new ICallbackAdapter<List<Event>>() {
            @Override
            public void onResponse(List<Event> object) {
                if (object != null) {
                    Log.d("Subscribe", String.format("Unsubscribed from event %d", event.getId()));
                } else {
                    Log.d("Subscribe", String.format("Couldn't unsubscribe from event %d", event.getId()));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Subscribe", String.format("Couldn't unsubscribe from event %d", event.getId()));
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

    @Override
    public void whenRoomFinished(Object object) {
        if (object instanceof Event) {
            listItems.add((Event) object);
            eventAdapter.notifyDataSetChanged();
        }
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

