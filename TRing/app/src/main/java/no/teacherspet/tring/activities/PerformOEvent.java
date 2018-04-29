package no.teacherspet.tring.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import connection.Event;
import connection.ICallbackAdapter;
import connection.NetworkManager;
import connection.Point;
import no.teacherspet.tring.Database.Entities.EventResult;
import no.teacherspet.tring.Database.Entities.PointOEventJoin;
import no.teacherspet.tring.Database.Entities.RoomOEvent;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.OEventViewModel;
import no.teacherspet.tring.Database.ViewModels.PointOEventJoinViewModel;
import no.teacherspet.tring.Database.ViewModels.ResultViewModel;
import no.teacherspet.tring.R;

public class PerformOEvent extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private Location currentLocation;
    private int positionViewed = 0;
    private HashMap<Point, Marker> markers;
    private ArrayList<Point> points;
    private ArrayList<Point> visitedPoints;
    private Event startedEvent;
    long startTime;
    long eventTime;
    boolean savedResults = false;
    private int eventDifficultyValue;
    private double seconds;
    private double minutes;
    private double hours;
    private String timeTextToServer;
    private Marker posisjonsmarkor;
    private Location lastKnown;


    private OEventViewModel oEventViewModel;
    private PointOEventJoinViewModel joinViewModel;
    private ResultViewModel resultViewModel;

    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;
    private TextView difficultyTextViewValue;
    private TextView eventTitle;
    private TextView difficultyTextViewText;
    private TextView explanationTextView;
    private Button startButton;
    private Button showMyPosition;
    private Button arrivedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markers = new HashMap<>();
        setContentView(R.layout.activity_perform_oevent);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_used_in_event);
        mapFragment.getMapAsync(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        createLocationRequest();


        startButton = (Button) findViewById(R.id.start_event_btn);
        showMyPosition = (Button) findViewById(R.id.show_position_btn);
        arrivedButton = (Button) findViewById(R.id.test_position_btn);
        lastKnown=null;


        visitedPoints = new ArrayList<>();

        LocalDatabase localDatabase = LocalDatabase.getInstance(this);
        oEventViewModel = new OEventViewModel(localDatabase.oEventDAO());
        joinViewModel = new PointOEventJoinViewModel(localDatabase.pointOEventJoinDAO());
        resultViewModel = new ResultViewModel(localDatabase.resultDAO());

        // 1
        //TODO: Fix saving of points when phone is flipped

//        Toast.makeText(getApplicationContext(),Integer.toString(startedEvent.getId()),Toast.LENGTH_LONG).show();

        this.startedEvent = (Event) getIntent().getSerializableExtra("MyEvent");
        startTime = getIntent().getLongExtra("StartTime", -1);
        startButton.setVisibility(View.INVISIBLE);
        eventDifficultyValue = getIntent().getIntExtra("Difficulty", -1);


        if (startTime == -1) {
            showMyPosition.setVisibility(View.INVISIBLE);
            arrivedButton.setVisibility(View.INVISIBLE);
            startButton.setVisibility(View.VISIBLE);
            openStartDialog();
            showLocationUntilEventIsStarted();
        }
//        Toast.makeText(getApplicationContext(),Integer.toString(startedEvent.getId()),Toast.LENGTH_SHORT).show();

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
                    if (locationResult.getLastLocation().getAccuracy() <= 700 || currentLocation == null) {
                        currentLocation = locationResult.getLastLocation();
                        if (startTime == -1) {
                            showLocationUntilEventIsStarted();
                        }
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
            Toast.makeText(getApplicationContext(), "The event does not have any points!", Toast.LENGTH_SHORT).show();
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
        if(mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(),R.raw.mapstyle))){
            System.out.println("parsing successful");
        }
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        LatLng avgPosition = getAvgLatLng();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (points != null) {
            for (Point point : points) {
                if (point != null) {
                    if (point.isVisited()) {
                        visitedPoints.add(point);
                        markers.put(point,mMap.addMarker(new MarkerOptions().title(point.getTitle()).snippet(point.getSnippet()).position(new LatLng(point.getLatitude(), point.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
                    } else {
                        if (point.equals(startedEvent.getStartPoint())) {
                            markers.put(point,mMap.addMarker(new MarkerOptions().title(point.getDescription()).position(new LatLng(point.getLatitude(), point.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.startpoint_flag_five))));
                        } else {
                            markers.put(point,mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude())).title((point.getDescription()))));                   
                        }
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

    //Dialog opens when event starts

    public void openStartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View inflator = inflater.inflate(R.layout.event_started_dialog, null);
        builder.setView(inflator);

        easyButton = (Button) inflator.findViewById(R.id.easy_btn);
        mediumButton = (Button) inflator.findViewById(R.id.medium_btn);
        hardButton = (Button) inflator.findViewById(R.id.hard_btn);
        difficultyTextViewValue = (TextView) inflator.findViewById(R.id.difficulty_textview);
        eventTitle = (TextView) inflator.findViewById(R.id.eventStartedTitle);
        difficultyTextViewText = inflator.findViewById(R.id.difficulty_textView_Text);
        explanationTextView = inflator.findViewById(R.id.explanation_textview);
        eventTitle.setText(startedEvent.getProperty("event_name"));
        explanationTextView.setVisibility(View.INVISIBLE);

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


        builder.setPositiveButton(R.string.start_dialog_positive_button_next, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        if (easyButton.getVisibility() == View.VISIBLE) {
                            easyButton.setVisibility(View.INVISIBLE);
                            mediumButton.setVisibility(View.INVISIBLE);
                            hardButton.setVisibility(View.INVISIBLE);
                            difficultyTextViewText.setVisibility(View.INVISIBLE);
                            difficultyTextViewValue.setVisibility(View.INVISIBLE);
                            explanationTextView.setVisibility(View.VISIBLE);
                            button.setText("Got it!");
                        } else {
                            //Dismiss once everything is OK.
                            //Lagre event (Tid, score og avstand?)

                            alertDialog.dismiss();


                        }
                    }
                });
            }
        });

        alertDialog.show();

    }

    public void setEventDifficulty(String difficulty) {
        switch (difficulty) {
            case "easy":
                this.eventDifficultyValue = 8;
                this.difficultyTextViewValue.setText(R.string.easy_button_text);
                this.difficultyTextViewValue.setBackgroundColor(Color.parseColor("#80228B22"));
                break;

            case "medium":
                this.eventDifficultyValue = 12;
                this.difficultyTextViewValue.setText(R.string.medium_button_text);
                this.difficultyTextViewValue.setBackgroundColor(Color.parseColor("#99e5e500"));
                break;

            case "hard":
                this.eventDifficultyValue = 16;
                this.difficultyTextViewValue.setText(R.string.hard_button_text);
                this.difficultyTextViewValue.setBackgroundColor(Color.parseColor("#B3b20000"));
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
        seconds = 0;
        seconds = getEventTime();
        int secondsInt = (int) seconds;
        int minutesInt = (int) minutes;
        int hoursInt = (int) hours;

        if (seconds > 60) {
            //Regn om til minutter
            minutes = calculate(eventTime);
            seconds = calculateRest(eventTime);

        }

        if (minutes > 60) {
            //Regn om til timer

            hours = calculate(minutes);
            minutes = calculateRest(minutes);
            seconds = calculateRest(minutes);
        }

        timeTextToServer = "";

        TextView timeTextView = (TextView) inflator.findViewById(R.id.timeTextView);
        if ((seconds) != 0) {
            timeTextView.setText("Tid: " + (int) seconds + " sec");
        }

        if ((minutes) != 0) {
            timeTextView.setText("Tid: " + (int) minutes + "min " + (int) seconds + " sec");
        }


        if ((hours) != 0) {
            timeTextView.setText("Tid: " + (int) hours + "hour " + (int) minutes + "min " + (int) seconds + " sec");
            //Fiks for timer
        }

        //Tid som skal sendes til server
        timeTextToServer = String.format("%02d:%02d:%02d", hoursInt, minutesInt, secondsInt);


        //Viser score oppnådd under eventet
        double eventScore = Math.round(getEventScore());
        String eventScoreString = Integer.toString((int) eventScore);
        eventScoreString += "/100";
        TextView scoreTextView = (TextView) inflator.findViewById(R.id.scoreTextView);
        scoreTextView.setText(String.format(getString(R.string.total_score_formatted), eventScoreString));


        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NetworkManager networkManager = NetworkManager.getInstance();
                networkManager.postResults(startedEvent.getId(), timeTextToServer, (int) eventScore, new ICallbackAdapter<Event>() {
                    @Override
                    public void onResponse(Event object) {
                        Toast.makeText(getApplicationContext(), "Save complete", Toast.LENGTH_SHORT).show();
                        openScoreDialog();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(getApplicationContext(), "Save failed", Toast.LENGTH_SHORT).show();

                    }
                });
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

    //Shows the users score, compared to the avrage time
    public void openScoreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View inflator = inflater.inflate(R.layout.event_score_dialog, null);
        builder.setView(inflator);

        builder.setPositiveButton(R.string.return_to_start_menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(R.string.return_to_event, new DialogInterface.OnClickListener() {
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
        Marker positionMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon)).title(getString(R.string.my_position)));
        //Marker removed after 5 seconds
        CountDownTimer visible = new CountDownTimer(5000, 990) {
            int sek = 5;

            Toast myToast = Toast.makeText(getApplicationContext(), String.format(getString(R.string.position_count_marker_warning_formatted), positionViewed, sek), Toast.LENGTH_SHORT);

            @Override
            public void onTick(long l) {
                myToast.setText(String.format(getString(R.string.position_count_marker_warning_formatted), positionViewed, sek));
                myToast.show();
                sek--;
            }

            @Override
            public void onFinish() {
                positionMarker.remove();
                myToast.cancel();
            }

        }.start();
    }






    public void showLocationUntilEventIsStarted() {
        if (currentLocation != null) {
            if (lastKnown == null) {
                posisjonsmarkor = mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon)).title("Min posisjon"));
            }
            //Markor fjernes etter 5 sekund

            CountDownTimer synlig = new CountDownTimer(5000, 1000) {
                int sek = 5;

                @Override
                public void onTick(long l) {
                    sek--;
                }

                @Override
                public void onFinish() {
                    if (lastKnown != currentLocation) {
                        posisjonsmarkor.remove();
                        posisjonsmarkor= mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon)).title("Min posisjon"));
                        lastKnown = currentLocation;
                    }
                }
            }.start();


        }

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
        if (currentLocation != null) {
            LatLng position = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            int prevsize = visitedPoints.size();
            for (Point point : points) {
                float distance = point.getDistanceFromPoint(position);
                if ((distance < 20) && !visitedPoints.contains(point)) {
                    visitedPoints.add(point);
                    point.setVisited(true);
                    updatePoint(point);

                    if (!point.equals(startedEvent.getStartPoint())) {
                        markers.get(point).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        Toast.makeText(getApplicationContext(), R.string.arrived_at_unvisited_point, Toast.LENGTH_SHORT).show();
                    }

                    break;
                }
            }

            if (prevsize == visitedPoints.size()) {
                Toast.makeText(getApplicationContext(), R.string.no_new_point_here, Toast.LENGTH_SHORT).show();
            }
            if (points.size() == visitedPoints.size()) {
                updateEvent(false);
                //Event finnished!
                resultViewModel.getResult(startedEvent.getId()).subscribe(results -> saveEventResult(getEventTime(), results));
                endEvent();
            }
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
                Log.d("Room", String.format("Event %d updated, starting: %b", event.getId(), starting));
                updatePoints(startedEvent, starting);
            }
        });
    }

    /**
     * Method for saving which points have been visited to room
     *
     * @param event    Event which we are updating
     * @param starting True: Event should be set to active. False: Event should be set to inactive
     */
    private void updatePoints(Event event, boolean starting) {
        PointOEventJoin[] joins = new PointOEventJoin[points.size()];
        Point startPoint = event.getStartPoint();
        for (int i = 0; i < points.size(); i++) {
            boolean start = startPoint.equals(points.get(i));
            joins[i] = new PointOEventJoin(points.get(i).getId(), event.getId(), start, points.get(i).isVisited() && starting);
        }
        Log.d("Room",String.format("Started updating %d points for event %d", joins.length, event.getId()));
        joinViewModel.addJoins(joins).subscribe(longs -> {
            if (longs[0] != -1) {
                Log.d("Room", String.format("Points for event %d updated, code: %d", event.getId(), longs[0]));
            }
        });
    }
    /**
     * Updates a single point to "visited" in the local database
     *
     * @param point Point to be updated
     */
    private void updatePoint(Point point) {
        boolean start = startedEvent.getStartPoint().equals(point);
        PointOEventJoin join = new PointOEventJoin(point.getId(), startedEvent.getId(), start, true);
        Log.d("Room",String.format("Setting point %d to visited", point.getId()));
        joinViewModel.addJoins(join).subscribe(longs -> {
            if(longs[0]>0){
                Log.d("Room",String.format("Visit point %d updated, code: %d", point.getId(), longs[0]));
            }
            else{
                Log.d("Room",String.format("Visit point %d not updated, code: %d", point.getId(), longs[0]));
            }
        });
    }

    /**
     * Saves the start time of an event to Room
     * @param startTime start time in seconds
     */
    private void saveEventStartTime(long startTime, List<EventResult> results){
        EventResult result = new EventResult(startedEvent.getId());
        result.setStartTime(startTime);
        if(results.size() > 0){
            result.setEventTime(results.get(0).getEventTime());
        }
        Log.d("Room", String.format("StartTime set to %d", startTime));
        resultViewModel.addResults(result).subscribe(longs -> Log.d("Room", String.format("StartTime saved for event %d", result.getId())));
    }

    /**
     * Saves the result to room, if the new result is better than the previous result
     */
    private void saveEventResult(long eventTime, List<EventResult> results){
        EventResult result = new EventResult(startedEvent.getId());
        if(results.size() > 0){
            if(results.get(0).getEventTime() == -1){
                result.setEventTime(eventTime);
            }
            else if(eventTime < results.get(0).getEventTime()){
                result.setEventTime(eventTime);
            }
            else{
                result.setEventTime(results.get(0).getEventTime());
            }
        }
        else{
            result.setEventTime(eventTime);
        }
        Log.d("Room", String.format("StartTime : %d, ResultTime: %d", result.getStartTime(), result.getEventTime()));
        resultViewModel.addResults(result).subscribe(longs -> Log.d("Room", String.format("ResultTime saved for event %d", result.getId())));
    }

    /**
     * Set all events in room to not active, set all points to not visited
     *
     * @param activeEvent Event which should not be reset
     */
    private void resetActiveEvents(Event activeEvent) {
        Log.d("Room", "Started resetting active events");
        oEventViewModel.getActiveEvent().subscribe(roomOEvents -> {
            Log.d("Room",String.format("Found %d active events", roomOEvents.size()));
            for (RoomOEvent event : roomOEvents){
                if(event.getId() != activeEvent.getId()){
                    Log.d("Room",String.format("Setting event %d to not active", event.getId()));
                    joinViewModel.getJoinsForOEvent(event.getId()).subscribe(joins ->
                            resetEvent(new RoomOEvent(event.getId(), event.getProperties()), joins));
                }
            }
        });
    }

    /**
     * Method for setting to inactive, and points to not visited
     *
     * @param event Event we are resetting
     * @param joins All connections between event and its points
     */
    private void resetEvent(RoomOEvent event, List<PointOEventJoin> joins) {
        Log.d("Room", String.format("Event %d has %d points", event.getId(), joins.size()));
        event.setActive(false);
        Log.d("Room", String.format("Event: eID: %d, active: %b", event.getId(), event.isActive()));
        oEventViewModel.addOEvents(event).subscribe(longs -> {
            PointOEventJoin[] joinArray = new PointOEventJoin[joins.size()];
            for (int i = 0; i < joins.size(); i++) {
                joinArray[i] = new PointOEventJoin(joins.get(i).pointID, joins.get(i).oEventID, joins.get(i).isStart(), false);
                Log.d("Room", String.format("Join: pID: %d, eID %d, Start %b, Visited %b",
                        joinArray[i].getPointID(), joinArray[i].getoEventID(), joinArray[i].isStart(), joinArray[i].isVisited()));
            }
            joinViewModel.addJoins(joinArray).subscribe(longs1 -> Log.d("Room", String.format("%d points updated", longs1.length)));
            Log.d("Room",String.format("Event %d set to not active", event.getId()));
        });
    }

    /**
     * @return The points of the event to be performed
     */
    public ArrayList<Point> getPoints() {
        return points;
    }

    public long getEventTime() {
        if (this.startTime != -1) {
            long difference = System.currentTimeMillis() - this.startTime;
            this.eventTime = (difference / 1000) ; //seconds
        }

        return eventTime;
    }

    public double calculate(double eventTime) {
        eventTime = Math.round(eventTime/60);
        return eventTime;
    }

    public double calculateRest(double eventTime) {
        double eventTimeRest = Math.round(eventTime%60);
        return eventTimeRest;
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

        if(eventScore<1) {
            eventScore =1;
        }

        return eventScore;
    }

    public void startEventBtnPressed(View v) {
        // Check if user is on startpoint



        if (currentLocation == null) {
            Toast.makeText(getApplicationContext(), R.string.try_again_in_5_sec, Toast.LENGTH_SHORT).show();
            return;
        }

        LatLng userLocationLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        if (startedEvent.getStartPoint() != null) {
            float distance = startedEvent.getStartPoint().getDistanceFromPoint(userLocationLatLng);
            if (distance < 20) {
                //addEventButton.setVisibility(View.GONE);
                //TODO Ikke sett hvis startTime allerede er satt
                if(startTime == -1){
                    startTime = System.currentTimeMillis();
                }
                resultViewModel.getResult(startedEvent.getId()).subscribe(results -> {
                    Log.d("Room",String.format("Found %d results for event %d", results.size(), startedEvent.getId()));
                    saveEventStartTime(startTime, results);
                });
                this.eventTime = -1;
                posisjonsmarkor.remove();
                arrivedButton.setVisibility(View.VISIBLE);
                showMyPosition.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.INVISIBLE);
                onArrivedBtnPressed(new View(getApplicationContext()));
            } else {
                Toast.makeText(getApplicationContext(), R.string.move_to_start, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void endEvent() {
        openFinishDialog();
    }


}

