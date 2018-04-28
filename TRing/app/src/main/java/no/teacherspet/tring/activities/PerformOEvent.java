package no.teacherspet.tring.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import connection.Event;
import connection.Point;
import no.teacherspet.tring.Database.Entities.PointOEventJoin;
import no.teacherspet.tring.Database.Entities.RoomOEvent;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.OEventViewModel;
import no.teacherspet.tring.Database.ViewModels.PointOEventJoinViewModel;
import no.teacherspet.tring.R;

public class PerformOEvent extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private Location currentLocation;
    private int positionViewed = 0;
    private ArrayList<Point> points;
    private ArrayList<Point> visitedPoints;
    private Event startedEvent;
    long startTime;
    long eventTime;

    private LocalDatabase localDatabase;
    private OEventViewModel oEventViewModel;
    private PointOEventJoinViewModel joinViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform_oevent);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_used_in_event);
        mapFragment.getMapAsync(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        createLocationRequest();

        visitedPoints = new ArrayList<>();

        localDatabase = LocalDatabase.getInstance(this);
        oEventViewModel = new OEventViewModel(localDatabase.oEventDAO());
        joinViewModel = new PointOEventJoinViewModel(localDatabase.pointOEventJoinDAO());

        // 1
        //TODO: Fix saving of points when phone is flipped
        this.startedEvent = (Event) getIntent().getSerializableExtra("MyEvent");
//        Toast.makeText(getApplicationContext(),Integer.toString(startedEvent.getId()),Toast.LENGTH_LONG).show();
        if (startedEvent != null) {
            points = readPoints();
            if (points == null) {
                finish();
            }
        }

    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000).setFastestInterval(500).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    System.out.println(locationResult.getLastLocation().getAccuracy());
                    if (locationResult.getLastLocation().getAccuracy() <= 700 || currentLocation == null) {
                        currentLocation = locationResult.getLastLocation();
                    }
                }
            };
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    /**
     * Reads the points from the event to be performed
     *
     * @return The list of points to be included
     */
    public ArrayList<Point> readPoints() {
        if (!startedEvent.getPoints().isEmpty()) {
            return startedEvent.getPoints();
        } else {
            Toast.makeText(getApplicationContext(), "The event does not have any points!", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        LatLng avgPosition = getAvgLatLng();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (points != null) {
            for (Point point : points) {
                if (point != null) {
                    if (point.isVisited()) {
                        visitedPoints.add(point);
                        mMap.addMarker(new MarkerOptions().title(point.getDescription()).position(new LatLng(point.getLatitude(), point.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    } else {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude())).title((point.getDescription())));
                    }
                    builder.include(new LatLng(point.getLatitude(), point.getLongitude()));
                }
            }
            resetActiveEvents(startedEvent);
            updateEvent(true);
            LatLngBounds bounds = builder.build();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(avgPosition));
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //TODO: display information when marker is clicked
                    return false;
                }
            });
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                    mMap.setLatLngBoundsForCameraTarget(bounds);
                    mMap.moveCamera(CameraUpdateFactory.zoomOut());
                    mMap.setMinZoomPreference(12.0f);
                    mMap.setMaxZoomPreference(20.0f);
                }
            });
        }
    }

    //Dialog opens when event is finished
    public void openFinishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View inflator = inflater.inflate(R.layout.event_finished_dialog, null);
        builder.setView(inflator);

        //Viser tiden brukt under eventet
        double eventTime = getEventTime();
        String eventTimeString = Double.toString(eventTime);
        TextView timeTextView = (TextView) inflator.findViewById(R.id.timeTextView);
        timeTextView.setText(String.format(getString(R.string.time_minutes_formatted), eventTimeString));

        //Viser score oppnådd under eventet
        double eventScore = getEventScore();
        String eventScoreString = Double.toString(eventScore);
        TextView scoreTextView = (TextView) inflator.findViewById(R.id.scoreTextView);
        scoreTextView.setText(String.format(getString(R.string.total_score_formatted), eventScoreString));


        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Lagre event (Tid, score og avstand?)

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    /**
     * Shows the users location for 5 seconds when the user presses the button for requesting to do so.
     *
     * @param v
     */
    public void showLocationButtonPressed(View v) {
        Marker posisjonsmarkor = mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon)).title("Min posisjon"));
        //Markor fjernes etter 5 sekund
        CountDownTimer synlig = new CountDownTimer(5000, 1000) {
            int sek = 5;

            @Override
            public void onTick(long l) {
                sek--;
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.position_count_marker_warning_formatted), positionViewed, sek), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFinish() {
                posisjonsmarkor.remove();

            }
        }.start();
    }

    /**
     * gives the average LatLng for the points in the event. Used for centering the camera correctly for the user.
     *
     * @return Average LatLng for the set of points.
     */
    private LatLng getAvgLatLng() {
        double lat = 0;
        double lng = 0;
        int numPoints = 0;
        if (points != null) {
            for (Point point : points) {
                if (point != null) {
                    lat += point.getLatitude();
                    lng += point.getLongitude();
                    numPoints++;
                }
            }
        }
        lat = lat / numPoints;
        lng = lng / numPoints;
        return new LatLng(lat, lng);
    }

    /**
     * executes if the uses presses button for being at a point. Checks Whether the user is close enough to an unchecked point to qualifying reaching it, and gives feedback based on this
     *
     * @param v
     */
    public void onArrivedBtnPressed(View v) {
        LatLng position = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        int prevsize = visitedPoints.size();
        for (Point point : points) {
            float distance = point.getDistanceFromPoint(position);
            if ((distance < 20) && !visitedPoints.contains(point)) {
                visitedPoints.add(point);
                point.setVisited(true);
                updatePoint(point);
                Toast.makeText(getApplicationContext(), R.string.arrived_at_unvisited_point, Toast.LENGTH_LONG).show();
                mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude()))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                break;
            }
        }

        if (prevsize == visitedPoints.size()) {
            Toast.makeText(getApplicationContext(), R.string.no_new_point_here, Toast.LENGTH_LONG).show();
        }
        if (points.size() == visitedPoints.size()) {
            updateEvent(false);
            //Event finnished!
            endEvent();
        }
    }

    /**
     * Method for updating the event to Room. To be called when starting and when finishing
     * @param starting Whether the event should be saved as starting(TRUE), or finishing(FALSE)
     */
    private void updateEvent(boolean starting) {
        RoomOEvent event = new RoomOEvent(startedEvent.getId(), startedEvent._getAllProperties());
        event.setActive(starting);
        oEventViewModel.addOEvents(event).subscribe(longs -> {
            if (longs[0] != -1) {
                Log.d("Room",String.format("Event %d updated, starting: %b", event.getId(), starting));
                updatePoints(startedEvent, starting);
            }
        });
    }
    /**
     * Method for saving which points have been visited to room
     * @param event Event which we are updating
     * @param starting True: Event should be set to active. False: Event should be set to inactive
     */
    private void updatePoints(Event event, boolean starting) {
        PointOEventJoin[] joins = new PointOEventJoin[points.size()];
        Point startPoint = event.getStartPoint();
        for (int i = 0; i < points.size(); i++) {
            boolean start = startPoint.equals(points.get(i));
            joins[i] = new PointOEventJoin(points.get(i).getId(), event.getId(), start, points.get(i).isVisited() && starting);
        }
        joinViewModel.addJoins(joins).subscribe(longs -> {
            if(longs[0] != -1){
                Log.d("Room",String.format("Points for event %d updated, code: %d", event.getId(), longs[0]));
            }
        });
    }

    /**
     * Updates a single point to "visited" in the local database
     * @param point Point to be updated
     */
    private void updatePoint(Point point) {
        boolean start = startedEvent.getStartPoint().equals(point);
        PointOEventJoin join = new PointOEventJoin(point.getId(), startedEvent.getId(), start, true);
        joinViewModel.addJoins(join).subscribe(longs -> {
            if(longs[0]>0){
                Log.d("Room",String.format("Point %d saved, code: %d", point.getId(), longs[0]));
            }
            else{
                Log.d("Room",String.format("Point %d not saved, code: %d", point.getId(), longs[0]));
            }
        });
    }

    /**
     * Set all events in room to not active, set all points to not visited
     * @param activeEvent Event which should not be reset
     */
    private void resetActiveEvents(Event activeEvent){
        Log.d("Room","Started resetting active events");
        oEventViewModel.getActiveEvent().subscribe(roomOEvents -> {
            Log.d("Room",String.format("Found %d active events", roomOEvents.size()));
            for (RoomOEvent event : roomOEvents){
                if(event.getId() != activeEvent.getId()){
                    Log.d("Room",String.format("Resetting event %d", event.getId()));
                    joinViewModel.getJoinsForOEvent(event.getId()).subscribe(joins ->
                            resetEvent(new RoomOEvent(event.getId(), event.getProperties()), joins));
                }
            }
        });
    }
    /**
     * Method for setting to inactive, and points to not visited
     * @param event Event we are resetting
     * @param joins All connections between event and its points
     */
    private void resetEvent(RoomOEvent event, List<PointOEventJoin> joins){
        Log.d("Room",String.format("Event %d has %d points", event.getId(), joins.size()));
        event.setActive(false);
        oEventViewModel.addOEvents(event).subscribe(longs -> {
            for (PointOEventJoin join : joins){
                PointOEventJoin newJoin = new PointOEventJoin(join.pointID, join.oEventID, join.isStart(), false);
                joinViewModel.addJoins(newJoin);
            }
            Log.d("Room",String.format("Event %d set to not active", event.getId()));
        });
    }

    /**
     * @return The points of the event to be performed
     */
    public ArrayList<Point> getPoints() {
        return points;
    }

    public double getEventTime() {
        if (this.eventTime == -1) {
            long difference = System.currentTimeMillis() - this.startTime;
            this.eventTime = (difference / 1000) / 60; //minutes
        }
        return eventTime;
    }

    public double getEventScore() {
        //TODO må lage ordentlig funksjon

        double distance = 0;
        // Kalkuler distanse
        for (Point point : points) {
            int index = points.indexOf(point);
            if (index == points.size() - 1) {
                break;
            }
            distance += point.getDistanceFromPoint(new LatLng(points.get(index + 1).getLatitude(), points.get(index + 1).getLongitude()));
        }

        //This must be changed based on the users level
        //Now using avrg jogging spead (Mid-levelsish?) 8kmph

        double avrageTimeBasedOnDistance = distance / 8000; //minutes


        double eventTime = getEventTime();
        double eventScore = avrageTimeBasedOnDistance / eventTime;


        return eventScore;
    }

    public void startEventBtnPressed(View v) {
        // Check if user is on startpoint

        View addEventButton = findViewById(R.id.start_event_btn);
        if (currentLocation == null) {
            Toast.makeText(getApplicationContext(), R.string.try_again_in_5_sec, Toast.LENGTH_SHORT).show();
            return;
        }
        LatLng userLocationLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        float distance = startedEvent.getStartPoint().getDistanceFromPoint(userLocationLatLng);
        if (distance < 20) {
            addEventButton.setVisibility(View.GONE);
            this.startTime = System.currentTimeMillis();
            this.eventTime = -1;
        } else {
            Toast.makeText(getApplicationContext(), R.string.move_to_start, Toast.LENGTH_LONG).show();
        }
    }

    public void endEvent() {
        openFinishDialog();
    }


}

