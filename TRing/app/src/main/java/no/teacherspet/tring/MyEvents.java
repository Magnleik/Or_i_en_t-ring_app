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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
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
    private Event selectedEvent;
    private ListView mListView;
    ///////


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


    public ArrayList<Event> initList1() {
        final HashMap<Integer,Event> theEventReceived = new StartupMenu().getTestEvents();
        int i=0;
        ArrayList<Event> listItems = new ArrayList<>();

        for (Event ev : theEventReceived.values()) {
            listItems.add(ev);
            i++;
        }
        return listItems;
    }


    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        mListView = (ListView) getView().findViewById(R.id.my_events_list);

        final ArrayList<Event> listItems = initList1();

        EventAdapter eventAdapter = new EventAdapter(this.getContext(), listItems);
        mListView.setAdapter(eventAdapter);

        final Context context = this.getContext();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 1
                Event selectedEvent = listItems.get(position);

                // 2
                Intent detailIntent = new Intent(context, PerformOEvent.class);

                // 3
                detailIntent.putExtra("MyEvent", selectedEvent);

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
