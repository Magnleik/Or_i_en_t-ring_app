package no.teacherspet.tring.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;

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
import no.teacherspet.tring.Database.ViewModels.PointViewModel;
import no.teacherspet.tring.R;

public class CreateOEvent extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Marker> arrayListWithCoords = new ArrayList<>();
    private ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    private NetworkManager networkManager;
    private LocationRequest locationRequest;
    private LatLng clickedPosition;
    private Marker startPoint;
    private Location currentLocation;
    private LocalDatabase localDatabase;
    private PointViewModel pointViewModel;
    private OEventViewModel oEventViewModel;
    private PointOEventJoinViewModel pointOEventJoinViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLocationRequest();
        setContentView(R.layout.activity_create_oevent);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_under_creation);
        mapFragment.getMapAsync(this);

        if (savedInstanceState != null) {
            latLngArrayList = savedInstanceState.getParcelableArrayList("points");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
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

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Boolean hasPosition = false;
                while (!hasPosition) {
                    if (currentLocation != null) {
                        hasPosition = true;
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
                    }
                }
            }
        });

        if ((latLngArrayList.size() > 0) && (arrayListWithCoords.size() == 0)) {
            for (LatLng latlng : latLngArrayList) {
                Marker Point = mMap.addMarker(new MarkerOptions().position(latlng).title("Punkt " + (arrayListWithCoords.size() + 1)));
                arrayListWithCoords.add(Point);
            }
        }
        Toast.makeText(getApplicationContext(), "Klikk på kartet for å legge til punkter.", Toast.LENGTH_SHORT).show();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            public void onMapClick(LatLng latLng) {
                clickedPosition = latLng;
                openAddDialog(null, latLng);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                openEditDialog(marker);
                return false;
            }
        });
    }

    public void openAddDialog(Marker marker, LatLng position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Legg til nytt punkt");
        EditText input = new EditText(this);
        input.setHint("Navn");
        if (marker != null) {
            builder.setTitle("Endre navn");
            input.setText(marker.getTitle());
        }
        builder.setView(input);
        builder.setPositiveButton("Legg til", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (marker != null) {
                    marker.setTitle(input.getText().toString());
                } else {
                    if (!input.getText().toString().isEmpty()) {
                        addNewMarker(input.getText().toString(), position);
                    } else {
                        addNewMarker(null, position);
                    }
                }
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

    public void openEditDialog(Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(marker.getTitle());
        CharSequence[] elements = {"Sett som startpunkt", "Rediger", "Slett"};
        builder.setItems(elements, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (startPoint != null) {
                            startPoint.setIcon(BitmapDescriptorFactory.defaultMarker());
                        }
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        startPoint = marker;
                        dialog.dismiss();
                        break;
                    case 1:
                        openAddDialog(marker, marker.getPosition());
                        dialog.dismiss();
                        break;
                    case 2:
                        deletePoint(marker);
                        dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Enables the user to add markers to the map for creating a new event. The markers later get converted to Point objects. Method gets called when the "Legg til punkter" button is pressed
     *
     * @param v
     */
    public void existingPointClicked(View v) {
        Intent intent = new Intent(CreateOEvent.this, AddExistingPoint.class);
        startActivityForResult(intent, 2);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (data != null) {
                    if (resultCode == RESULT_OK) {
                        addExistingMarkers(data);
                    }
                }
        }
    }

    private void addNewMarker(String name, LatLng location) {
        Marker point;
        if (name != null) {
            point = mMap.addMarker(new MarkerOptions().position(location).title(name).draggable(true));
            arrayListWithCoords.add(point);
        } else {
            point = mMap.addMarker(new MarkerOptions().position(location).title("Punkt " + (arrayListWithCoords.size() + 1)).draggable(true));
            arrayListWithCoords.add(point);
        }
    }


    /**
     * Adds a list of existing points to the current points in the event
     *
     * @param data
     */
    private void addExistingMarkers(Intent data) {
        Serializable items = data.getSerializableExtra("selectedPositions");
        ArrayList<LatLng> positions = (ArrayList<LatLng>) items;
        for (LatLng position : positions) {
            //TODO hent ut navn til ulike markers
            addNewMarker(null, position);
        }
        Toast.makeText(getApplicationContext(), "Added " + positions.size() + " points.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Removes the latest element in the marker array. Method gets called when the "Slett forrige" button is pressed
     *
     * @param marker to be removed
     */
    public void deletePoint(Marker marker) {
        if (marker.equals(startPoint)) {
            startPoint = null;
        }
        if (arrayListWithCoords.size() > 0) {
            marker.remove();
            arrayListWithCoords.remove(marker);
        }
    }

    //Adds marker on the users location

    /**
     * creates a locationRequest to update the CurrentLocation variable as often as possible. If a reading is to inaccurate, it will discard it
     */
    private void createLocationRequest() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient lm = LocationServices.getFusedLocationProviderClient(this);
            locationRequest = new LocationRequest();
            locationRequest.setInterval(1000).setFastestInterval(500).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    System.out.println(locationResult.getLastLocation().getAccuracy());
                    if (locationResult.getLastLocation().getAccuracy() <= 500 || currentLocation == null) {
                        currentLocation = locationResult.getLastLocation();
                    }
                }
            };
            lm.requestLocationUpdates(locationRequest, mLocationCallback, null);
        } else {
            Toast.makeText(getApplicationContext(), "You need to give the app location permission.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Adds a marker to your position if it is available
     *
     * @param v Button that fires the method
     */
    public void addMarkerMyPosition(View v) {
        boolean shouldAdd = true;
        Location markerLocation;
        for (Marker marker : arrayListWithCoords) {
            markerLocation = new Location("");
            markerLocation.setLatitude(marker.getPosition().latitude);
            markerLocation.setLongitude(marker.getPosition().longitude);
            if (currentLocation.distanceTo(markerLocation) <= 5) {
                shouldAdd = false;
            }
        }
        if (shouldAdd) {
            arrayListWithCoords.add(mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Punkt " + arrayListWithCoords.size() + 1)));
        }
    }

    /**
     * Creates a new event with the given markers as posts. Gives the event the name found in the EditText on top of the screen. Method gets called when The "Ferdig" button is pressed
     *
     * @param v
     */
    public void saveEvent(View v) {
        EditText eventTitleField = (EditText) findViewById(R.id.create_event_name);
        if (eventTitleField.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Du må gi løpet et navn!", Toast.LENGTH_SHORT).show();
        } else if (startPoint == null) {
            Toast.makeText(getApplicationContext(), "Du må velge et startpunkt!", Toast.LENGTH_SHORT).show();
        } else {
            Event event = new Event();
            String eventTitle = eventTitleField.getText().toString();
            event.addProperty("event_name", eventTitle);
            Point sp = new Point(startPoint.getPosition().latitude,startPoint.getPosition().longitude,startPoint.getTitle());
            event.setStartPoint(sp);
            for (Marker marker : arrayListWithCoords) {
                if(marker!=startPoint) {
                    if (event.getPoints() == null) {
                        event.setStartPoint(new Point(marker.getPosition().latitude, marker.getPosition().longitude, marker.getTitle()));
                    } else {
                        event.addPost(new Point(marker.getPosition().latitude, marker.getPosition().longitude, marker.getTitle()));
                    }
                }
            }
            networkManager = NetworkManager.getInstance();
            networkManager.addEvent(event, new ICallbackAdapter<Event>() {
                @Override
                public void onResponse(Event object) {
                    if (object == null) {
                        Toast.makeText(getApplicationContext(), "Failed to create event.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Event: " + event.getProperty("event_name") + " added.", Toast.LENGTH_SHORT).show();
                        saveEventToRoom(object);
                        finish();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(getApplicationContext(), "Couldn't connect to internet", Toast.LENGTH_SHORT).show();
                }
            });
            //StartupMenu.addEvent(event);
            //Toast.makeText(getApplicationContext(), "Lagret ruten '" + eventTitle + "', " + arrayListWithCoords.size() + " punkt registrert", Toast.LENGTH_LONG).show();
            //LAGRE
            //Reset
        }
    }

    /**
     * Saves the Event and corresponding Points, and adds connections between them in the Room database
     * Makes sure that events and points are saved before the connections are saved. The next step
     * is only called after the previous is finished.
     */
    private void saveEventToRoom(Event event) {
        localDatabase = LocalDatabase.getInstance(this);
        pointViewModel = new PointViewModel(localDatabase.pointDAO());
        oEventViewModel = new OEventViewModel(localDatabase.oEventDAO());

        RoomOEvent newevent = new RoomOEvent(event.getId(), event._getAllProperties());
        oEventViewModel.addOEvents(newevent).subscribe(longs -> savePoints(event));
    }

    private void savePoints(Event event) {
        RoomPoint[] roomPoints = new RoomPoint[event.getPoints().size()];
        for (int i = 0; i < event.getPoints().size(); i++) {
            Point point = event.getPoints().get(i);
            RoomPoint roomPoint = new RoomPoint(point.getId(), point._getAllProperties(), new LatLng(point.getLatitude(), point.getLongitude()));
            roomPoints[i] = roomPoint;
        }
        pointViewModel.addPoints(roomPoints).subscribe(longs -> joinPointsToEvent(event));
    }

    private void joinPointsToEvent(Event event) {
        pointOEventJoinViewModel = new PointOEventJoinViewModel(localDatabase.pointOEventJoinDAO());

        PointOEventJoin[] joins = new PointOEventJoin[event.getPoints().size()];
        for (int i = 0; i < event.getPoints().size(); i++) {
            Point point = event.getPoints().get(i);
            boolean start = i == 0;
            joins[i] = new PointOEventJoin(point.getId(), event.getId(), start, false);
        }
        pointOEventJoinViewModel.addJoins(joins).subscribe(longs -> checkSave(longs));
    }

    private void checkSave(long[] longs) {
        boolean savedAll = true;
        for (long aLong : longs) {
            if (aLong < 0) {
                savedAll = false;
            }
        }
        if (savedAll) {
            Toast.makeText(this, "Save to phone successfull", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Save to phone unsuccessfull", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves the currently created points to an arraylist to be provided later when the state is restored. Used when phone is flipped and state is destroyed
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        if (latLngArrayList.size() != arrayListWithCoords.size()) {
            latLngArrayList.clear();
            for (Marker marker : arrayListWithCoords) {
                latLngArrayList.add(marker.getPosition());
            }
        }

        outState.putParcelableArrayList("points", latLngArrayList);

        // Save the state of item position
    }


}
