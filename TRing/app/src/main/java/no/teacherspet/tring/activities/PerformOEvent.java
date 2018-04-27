package no.teacherspet.tring.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    boolean savedResults = false;
    private int eventDifficultyValue;

    private LocalDatabase localDatabase;
    private OEventViewModel oEventViewModel;
    private PointOEventJoinViewModel joinViewModel;

    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;
    private TextView difficultyTextView;

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
        openStartDialog();



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

            resetActiveEvents();

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

    //Dialog opens when event starts

    public void openStartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View inflator = inflater.inflate(R.layout.event_started_dialog, null);
        builder.setView(inflator);

        easyButton = (Button) inflator.findViewById(R.id.easy_btn);
        mediumButton = (Button) inflator.findViewById(R.id.medium_btn);
        hardButton = (Button) inflator.findViewById(R.id.hard_btn);
        difficultyTextView = (TextView) inflator.findViewById(R.id.difficulty_textview);

        setEventDifficulty("easy");

        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEventDifficulty("easy");
            }
        });

        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEventDifficulty("medium");
            }
        });

        hardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEventDifficulty("hard");
            }
        });


        builder.setPositiveButton("Start løp", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Lagre event (Tid, score og avstand?)
                startEventBtnPressed();
                if (eventTime == -1) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();




    }

    public void setEventDifficulty(String difficulty) {
        switch (difficulty) {
            case "easy":
                this.eventDifficultyValue = 8;
                this.difficultyTextView.setText("Lett");
                this.difficultyTextView.setBackgroundColor(Color.GREEN);
                break;

            case "medium":
                this.eventDifficultyValue = 12;
                this.difficultyTextView.setText("Medium");
                this.difficultyTextView.setBackgroundColor(Color.YELLOW);
                break;

            case "hard":
                this.eventDifficultyValue = 16;
                this.difficultyTextView.setText("Vanskelig");
                this.difficultyTextView.setBackgroundColor(Color.RED);
                break;

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
        timeTextView.setText("Tid: " + eventTimeString + " sekunder");

        //Viser score oppnådd under eventet
        double eventScore = getEventScore();
        String eventScoreString = Double.toString(eventScore);
        TextView scoreTextView = (TextView) inflator.findViewById(R.id.scoreTextView);
        scoreTextView.setText("Total score: " + eventScoreString);


        builder.setPositiveButton("Lagre", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Lagre event (Tid, score og avstand?)

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
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
        positionViewed++;
        Marker posisjonsmarkor = mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon)).title("Min posisjon"));
        //Markor fjernes etter 5 sekund
        CountDownTimer synlig = new CountDownTimer(5000, 1000) {
            int sek = 5;

            @Override
            public void onTick(long l) {
                sek--;
                Toast.makeText(getApplicationContext(), "Dette er " + positionViewed + ".gang posisjonen vises, markor fjernes om " + sek + " sekunder", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(getApplicationContext(), "You arrived at a previously unvisited point!", Toast.LENGTH_LONG).show();
                mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude()))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                break;
            }
        }

        if (prevsize == visitedPoints.size()) {
            Toast.makeText(getApplicationContext(), "There is no new point here to be visited", Toast.LENGTH_LONG).show();
        }
        if (points.size() == visitedPoints.size()) {
            updateEvent(false);
            //Event finnished!
            endEvent();
        }
    }

    /**
     * Method for updating the event to Room. To be called when starting and when finishing
     *
     * @param starting Whether the event should be saved as starting(TRUE), or finishing(FALSE)
     */
    private void updateEvent(boolean starting) {
        RoomOEvent event = new RoomOEvent(startedEvent.getId(), startedEvent._getAllProperties());
        event.setActive(starting);
        oEventViewModel.addOEvents(event).subscribe(longs -> {
            if (longs[0] != -1) {
                updatePoints(startedEvent, starting);
            }
        });
    }

    /**
     * Method for saving which points have been visited to room
     *
     * @param event
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
                //TODO For testing purposes
                Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates a single point to "visited" in the local database
     *
     * @param point
     */
    private void updatePoint(Point point) {
        boolean start = startedEvent.getStartPoint().equals(point);
        PointOEventJoin join = new PointOEventJoin(point.getId(), startedEvent.getId(), start, true);
        joinViewModel.addJoins(join);
    }

    /**
     * Set all events in room to not active, set all points to not visited
     */
    private void resetActiveEvents(){
        oEventViewModel.getActiveEvent().subscribe(roomOEvents -> {
            for (RoomOEvent event : roomOEvents){
                if(event.getId() != startedEvent.getId()){
                    joinViewModel.getJoinsForOEvent(event.getId()).subscribe(joins -> resetEvent(event, joins));
                }
            }
        });
    }
    private void resetEvent(RoomOEvent event, List<PointOEventJoin> joins){
        event.setActive(false);
        oEventViewModel.addOEvents(event);
        for (PointOEventJoin join : joins){
            PointOEventJoin newJoin = new PointOEventJoin(join.pointID, join.oEventID, join.isStart(), false);
            joinViewModel.addJoins(newJoin);
        }
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
            this.eventTime = (difference / 1000) ; //seconds
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
        distance = distance*1.2; // Multiplying because of the terrain (The route will most likely be longer than the air distance
        //This must be changed based on the users level
        //Now using avrg jogging spead (Mid-levelsish?) 8kmph

        double avrageTimeBasedOnDistance = (distance) / ((this.eventDifficultyValue * 1000)/3600); //

        double eventTime = getEventTime();

        double eventScore = avrageTimeBasedOnDistance *100 / eventTime;
        if ( eventScore > 100) {
            eventScore = 100;
        }
        eventScore -= positionViewed*5; // Fjerner poeng for å ha sjekket posisjonen.

        return eventScore;
    }

    public void startEventBtnPressed() {
        // Check if user is on startpoint

        if (currentLocation == null) {
            Toast.makeText(getApplicationContext(), "Prøv igjen om 5 sekunder", Toast.LENGTH_SHORT).show();
            return;
        }

        LatLng userLocationLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        float distance = startedEvent.getStartPoint().getDistanceFromPoint(userLocationLatLng);
        if (distance < 20) {
            this.startTime = System.currentTimeMillis();
            this.eventTime = -1;
        } else {
            Toast.makeText(getApplicationContext(), "Beveg deg til startpunktet for å starte løpet", Toast.LENGTH_LONG).show();
        }
    }

    public void endEvent() {
        openFinishDialog();
    }


}

