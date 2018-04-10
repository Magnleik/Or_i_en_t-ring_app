package no.teacherspet.tring;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import connection.Event;
import no.teacherspet.tring.Database.Entities.RoomOEvent;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.OEventViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 */
public class MyEvents extends Fragment {

    private Event selectedEvent;
    private RecyclerView eventsList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<RoomOEvent> oEvents;
    private HashMap<Integer,Event> theEventReceived;

    private OnFragmentInteractionListener mListener;

    public MyEvents() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HashMap<Integer,Event> theEventReceived = new HashMap<>();
        getEvents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);
        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        eventsList = (RecyclerView) getView().findViewById(R.id.my_events_list);
        eventsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        eventsList.setLayoutManager(layoutManager);


    }
    private void getEvents(){
        LocalDatabase database = LocalDatabase.getInstance(getContext());
        OEventViewModel oEventViewModel = new OEventViewModel(database.oEventDAO());
        oEventViewModel.getAllOEvents().subscribe(roomOEvents -> oEvents = roomOEvents);
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
