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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

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
    private FusedLocationProviderClient lm;
    private LatLng position;
    private NetworkManager networkManager;
    private LocationRequest locationRequest;
    private Marker startPoint;
    LocalDatabase localDatabase;
    PointViewModel pointViewModel;
    OEventViewModel oEventViewModel;
    PointOEventJoinViewModel pointOEventJoinViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_oevent);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_under_creation);
        mapFragment.getMapAsync(this);
        //Log.i("INFO:", getString(R.string.google_maps_key));
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lm = LocationServices.getFusedLocationProviderClient(this);
            if (savedInstanceState != null) {
                latLngArrayList = savedInstanceState.getParcelableArrayList("points");
            }
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "App needs permission to access location services on phone to run", Toast.LENGTH_LONG).show();
            finish();
        } else {
            lm.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        position = new LatLng(location.getLatitude(), location.getLongitude());
                    } else {
                        position = new LatLng(10.416136, 10.405297);
                    }
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                }
            });
        }

        if ((latLngArrayList.size() > 0) && (arrayListWithCoords.size() == 0)) {
            for (LatLng latlng : latLngArrayList) {
                Marker Point = mMap.addMarker(new MarkerOptions().position(latlng).title("Punkt " + (arrayListWithCoords.size() + 1)));
                arrayListWithCoords.add(Point);
            }
        }
        Toast.makeText(getApplicationContext(), "Klikk på kartet for å legge til punkter.", Toast.LENGTH_SHORT).show();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            public void onMapClick(LatLng latLng) {
                position = latLng;
                openAddDialog(null);
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

    public void openAddDialog(Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Legg til nytt punkt");
        EditText input = new EditText(this);
        input.setHint("Navn");
        if(marker!=null) {
            builder.setTitle("Endre navn");
            input.setText(marker.getTitle());
        }
        builder.setView(input);
        builder.setPositiveButton("Legg til", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(marker!=null){
                    marker.setTitle(input.getText().toString());
                }
                else {
                    addNewMarker(input.getText().toString());
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
                        startPoint = marker;
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        dialog.dismiss();
                        break;
                    case 1:
                        position = marker.getPosition();
                        openAddDialog(marker);
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
        switch (resultCode) {
            case 2:
                if (data != null) {
                    if (resultCode == RESULT_OK) {
                        addExistingMarkers(data);
                    }
                }
        }
    }

    private void addNewMarker(String name) {
        Marker point;
        if (name != null) {
            point = mMap.addMarker(new MarkerOptions().position(position).title(name).draggable(true));
            arrayListWithCoords.add(point);
        } else {
            point = mMap.addMarker(new MarkerOptions().position(position).title("Punkt " + (arrayListWithCoords.size() + 1)).draggable(true));
            arrayListWithCoords.add(point);
        }
    }


    private void addExistingMarkers(Intent data) {
        ArrayList<LatLng> positions = data.getParcelableArrayListExtra("selectedPositions");
        for (LatLng position : positions) {
            this.position = position;
            //TODO hent ut navn til ulike markers
            addNewMarker(null);
        }
        Toast.makeText(getApplicationContext(), "Added " + positions.size() + " points.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Removes the latest element in the marker array. Method gets called when the "Slett forrige" button is pressed
     *
     * @param marker to be removed
     */
    public void deletePoint(Marker marker) {
        if (marker == startPoint) {
            startPoint = null;
        }
        if (arrayListWithCoords.size() > 0) {
            marker.remove();
            arrayListWithCoords.remove(marker);
        }
    }

    //Adds marker on the users location

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000).setFastestInterval(500).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void addMarkerMyPosition(View v) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            createLocationRequest();

            lm.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {

                        position = new LatLng(location.getLatitude(), location.getLongitude());
                        addNewMarker(null);
                    }
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "Du må gi appen tilgang til bruk av GPS.", Toast.LENGTH_LONG).show();

        }
    }

    /**
     * Creates a new event with the given markers as posts. Gives the event the name found in the EditText on top of the screen. Method gets called when The "Ferdig" button is pressed
     *
     * @param v
     */
    public void saveEvent(View v) {
        EditText eventTitleField = (EditText) findViewById(R.id.create_event_name);
        Event event = new Event();
        String eventTitle = eventTitleField.getText().toString();
        event.addProperty("event_name", eventTitle);
        for (Marker marker : arrayListWithCoords) {
            if (event.getPoints() == null) {
                event.setStartPoint(new Point(marker.getPosition().latitude, marker.getPosition().longitude, marker.getTitle()));
            } else {
                event.addPost(new Point(marker.getPosition().latitude, marker.getPosition().longitude, marker.getTitle()));
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
                    finish();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "Couldn't connect to internet", Toast.LENGTH_SHORT).show();
            }
        });
        saveEventToRoom(event);
        //StartupMenu.addEvent(event);
        //Toast.makeText(getApplicationContext(), "Lagret ruten '" + eventTitle + "', " + arrayListWithCoords.size() + " punkt registrert", Toast.LENGTH_LONG).show();
        //LAGRE
        //Reset
    }

    /**
     * Saves the Event and Points, and adds connections between them in the Room database
     */
    private void saveEventToRoom(Event event) {
        localDatabase = LocalDatabase.getInstance(this);
        pointViewModel = new PointViewModel(localDatabase.pointDAO());
        oEventViewModel = new OEventViewModel(localDatabase.oEventDAO());
        pointOEventJoinViewModel = new PointOEventJoinViewModel(localDatabase.pointOEventJoinDAO());

        RoomOEvent newevent = new RoomOEvent(event.getId(), event._getAllProperties());
        oEventViewModel.addOEvents(newevent).subscribe(longs -> checkSave(longs));

        RoomPoint[] roomPoints = new RoomPoint[event.getPoints().size()];
        PointOEventJoin[] joins = new PointOEventJoin[event.getPoints().size()];
        RoomPoint roomPoint;
        PointOEventJoin join;
        Point point;
        for (int i = 0; i < event.getPoints().size(); i++) {
            point = event.getPoints().get(i);
            roomPoint = new RoomPoint(point.getId(), point._getAllProperties(), new LatLng(point.getLatitude(), point.getLongitude()));

            if (i > 0) {
                join = new PointOEventJoin(point.getId(), event.getId(), false);
            } else {
                join = new PointOEventJoin(point.getId(), event.getId(), true);
            }
            roomPoints[i] = roomPoint;
            joins[i] = join;
        }
        pointViewModel.addPoints(roomPoints).subscribe(longs -> checkSave(longs));
        pointOEventJoinViewModel.addJoin(joins).subscribe(longs -> checkSave(longs));

    }

    private void checkSave(long[] longs) {
        boolean allSaved = true;
        for (int i = 0; i < longs.length; i++) {
            if (longs[i] < 0) {
                allSaved = false;
            }
        }
        if (allSaved) {
            Toast.makeText(this, "Save successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
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
