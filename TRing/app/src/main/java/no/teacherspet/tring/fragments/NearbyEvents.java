package no.teacherspet.tring.fragments;

import android.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import connection.Event;
import connection.ICallbackAdapter;
import connection.NetworkManager;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.OEventViewModel;
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
 * {@link NearbyEvents.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NearbyEvents#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyEvents extends Fragment implements RoomInteract {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private boolean reverseAlpha;
    private boolean reversePop;
    private boolean reverseScore;
    private boolean reverseTime;

    private ListOfSavedEvents parent;
    private Event selectedEvent;
    private ArrayList<Event> listItems;
    private BroadcastReceiver mReciever;

    private OnFragmentInteractionListener mListener;
    private RoomSaveAndLoad roomSaveAndLoad;
    private EventAdapter eventAdapter;


    public NearbyEvents() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NearbyEvents.
     */
    public static NearbyEvents newInstance() {
        NearbyEvents fragment = new NearbyEvents();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        roomSaveAndLoad = new RoomSaveAndLoad(getContext(), this);
        reverseAlpha = false;
        reversePop = false;
        reverseScore = false;
        reverseTime = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearby_events, container, false);
    }


    /**
     * gets run when the view is created. Sets up the lists to receive data. Also defines the BroadCastReceiver to act when its activity broadcasts messages. If possible gets updates from server to update local database
     * @param view
     * @param savedInstanceState
     */
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ListView mListView = (ListView) getView().findViewById(R.id.nearby_events_list);
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
        filter.addAction(ListOfSavedEvents.ACTION_LIST_LOADED);
        filter.addAction(ListOfSavedEvents.ACTION_SORT_ALPHA);
        filter.addAction(ListOfSavedEvents.ACTION_SORT_POPULARITY);
        filter.addAction(ListOfSavedEvents.ACTION_SORT_DIST);
        filter.addAction(ListOfSavedEvents.ACTION_SORT_TIME);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(mReciever, filter);


        listItems = new ArrayList<>();
        eventAdapter = new EventAdapter(this.getContext(), listItems);
        mListView.setAdapter(eventAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocalDatabase ld = LocalDatabase.getInstance(getContext());
                OEventViewModel vm = new OEventViewModel(ld.oEventDAO());
                vm.getActiveEvent().subscribe(roomOEvents -> {
                    selectedEvent = listItems.get(position);
                    if (roomOEvents.isEmpty()) {
                        roomSaveAndLoad.saveRoomEvent(selectedEvent);
                    } else {
                        openOverrideDialog();
                    }
                });
            }
        });
    }

    /**
     * Opens a dialog to notify user that there exist an active event
     */
    private void openOverrideDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle(getString(R.string.event_in_progress));
        builder.setMessage(getString(R.string.cancel_last_event));
        CharSequence[] elements = {getString(R.string.cancel), getString(R.string.proceed)};
        builder.setPositiveButton(getString(R.string.proceed), (dialog, which) -> {
            roomSaveAndLoad.saveRoomEvent(selectedEvent);
            dialog.dismiss();
        });
        builder.setNegativeButton(getString(R.string.abort), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Sorts the list based on properties and whether the order is to be reversed
     * @param property to sort by
     * @param reversed whether it is to be reversed
     */
    private void sortList(String property, boolean reversed) {
        Collections.sort(listItems, new EventComparator(property, reversed));
        eventAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

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

    /**
     * sets the list items to be the events gotten from the parent activity if they exist
     */
    private void initList() {
        HashMap<Integer, Event> theEventReceived = parent.getEvents();
        if (theEventReceived != null) {
            for (Event ev : theEventReceived.values()) {
                listItems.add(ev);
            }
        }
            eventAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    /*
     * Stops the fragment from listening to updates when view gets destroyed.
     */
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(mReciever);
    }

    @Override
    public void whenRoomFinished(Object object) {
        NetworkManager.getInstance().subscribeToEvent(selectedEvent.getId(), new ICallbackAdapter<List<Event>>() {
            @Override
            public void onResponse(List<Event> object) {
                if(object != null){
                    Log.d("Subscribe", String.format("List<Event> has events: %d", object.size()));
                }
                else {
                    Log.d("Subscribe", "List<Event> is null");
                }
                startEvent();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Subscribe", t.getMessage());
                startEvent();
            }
        });
    }

    /**
     * Starts the event saved as selectedEvent and sends the event to the PerformOEvent class
     */
    private void startEvent(){
        Log.d("Room", String.format("Event %d saved", selectedEvent.getId()));
        Intent detailIntent = new Intent(this.getContext(), PerformOEvent.class);
        detailIntent.putExtra("MyEvent", selectedEvent);
        startActivity(detailIntent);
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
        void onFragmentInteraction(Uri uri);
    }
}
