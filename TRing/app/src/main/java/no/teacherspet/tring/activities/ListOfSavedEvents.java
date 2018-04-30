package no.teacherspet.tring.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import connection.Event;
import connection.ICallbackAdapter;
import connection.NetworkManager;
import no.teacherspet.tring.R;
import no.teacherspet.tring.fragments.MostPopularEvents;
import no.teacherspet.tring.fragments.MyEvents;
import no.teacherspet.tring.fragments.NearbyEvents;
import no.teacherspet.tring.util.EventFragmentPagerAdapter;
import no.teacherspet.tring.util.GeneralProgressDialog;
import no.teacherspet.tring.util.PagerAdapter;

public class ListOfSavedEvents extends AppCompatActivity implements MyEvents.OnFragmentInteractionListener, NearbyEvents.OnFragmentInteractionListener, MostPopularEvents.OnFragmentInteractionListener {

    public static final String ACTION_LIST_LOADED = "action_list_loaded";
    public static final String ACTION_SORT_ALPHA = "action_sort_alpha";
    public static final String ACTION_SORT_POPULARITY = "action_sort_popularity";
    public static final String ACTION_SORT_DIST = "action_sort_dist";
    public static final String ACTION_SORT_TIME = "action_sort_time";
    private static int distance = 1000;

    private boolean reverseAlpha;
    private boolean reversePop;
    private boolean reverseScore;
    private boolean reverseTime;

    private HashMap<Integer, Event> theEventReceived;
    private NetworkManager networkManager;
    private FusedLocationProviderClient lm;
    private LatLng position;
    private LocationCallback mLocationCallback;
    private Location currentLocation;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private PagerAdapter mPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reverseAlpha = false;
        reversePop = false;
        reverseScore = false;
        reverseTime = false;
        setContentView(R.layout.activity_list_of_saved_events);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        initList();
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), 2);

        setSupportActionBar((Toolbar) findViewById(R.id.saved_events_toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(new EventFragmentPagerAdapter(getSupportFragmentManager(),
                ListOfSavedEvents.this));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.bringToFront();
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    protected void onResume() {
        supportInvalidateOptionsMenu();
        super.onResume();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.general_menu, menu);
        getMenuInflater().inflate(R.menu.sorting_menu, menu);

        MenuItem logInMenu = menu.findItem(R.id.log_in_menu);
        logInMenu.setIntent(new Intent(this, LogInActivity.class));
        if (NetworkManager.getInstance().isAuthenticated()) {
            logInMenu.setVisible(false);
        }

        MenuItem logOutMenu = menu.findItem(R.id.log_out_menu);
        if (!NetworkManager.getInstance().isAuthenticated()) {
            logOutMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case (android.R.id.home):
                finish();
                break;
            case (R.id.log_out_menu):
                Intent intent = new Intent(ListOfSavedEvents.this, OrientationSelector.class);
                intent.putExtra("Logout", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case (R.id.sort_alpha):
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(ACTION_SORT_ALPHA));
                break;
            case (R.id.sort_popularity):
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(ACTION_SORT_POPULARITY));
                break;
            case (R.id.sort_dist):
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(ACTION_SORT_DIST));
                break;
            case (R.id.sort_time):
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(ACTION_SORT_TIME));
                break;
        }
        supportInvalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    public void initList() {
        theEventReceived = new HashMap<>();
        GeneralProgressDialog progressDialog = new GeneralProgressDialog(getApplicationContext(), this, true);
        networkManager = NetworkManager.getInstance();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lm = LocationServices.getFusedLocationProviderClient(this);
            progressDialog.show();
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(1000).setFastestInterval(500).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    stopLocationRequest();
                    System.out.println(locationResult.getLastLocation().getAccuracy());
                    if (locationResult.getLastLocation().getAccuracy() <= 500 || currentLocation == null) {
                        currentLocation = locationResult.getLastLocation();
                    }
                    ICallbackAdapter<ArrayList<Event>> adapter = new ICallbackAdapter<ArrayList<Event>>() {
                        @Override
                        public void onResponse(ArrayList<Event> object) {
                            if (object == null) {
                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            } else {
                                for (int i = 0; i < object.size(); i++) {
                                    theEventReceived.put(object.get(i).getId(), object.get(i));
                                }
                            }
                            progressDialog.hide();
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(ACTION_LIST_LOADED));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Toast.makeText(getApplicationContext(), "Could not connect to server.", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                        }
                    };
                    networkManager.getNearbyEvents(currentLocation.getLatitude(), currentLocation.getLongitude(), distance, adapter);
                }
            };
            lm.requestLocationUpdates(locationRequest, mLocationCallback, null);
        }
        //theEventReceived = new StartupMenu().getTestEvents();

    }

    public void stopLocationRequest() {
        if (mLocationCallback != null) {
            lm.removeLocationUpdates(mLocationCallback);
        }
    }

    public HashMap<Integer, Event> getEvents() {
        return theEventReceived;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}