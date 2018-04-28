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
import android.util.Log;
import android.view.Menu;
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
import java.util.List;

import connection.Event;
import connection.ICallbackAdapter;
import connection.NetworkManager;
import connection.Point;
import no.teacherspet.tring.R;
import no.teacherspet.tring.util.RoomSaveAndLoad;
import no.teacherspet.tring.util.RoomInteract;

public class CreateOEvent extends AppCompatActivity implements OnMapReadyCallback, RoomInteract {

    private GoogleMap mMap;
    private ArrayList<Marker> arrayListWithCoords = new ArrayList<>();
    private ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    private NetworkManager networkManager;
    private LocationRequest locationRequest;
    private LatLng clickedPosition;
    private Marker startPoint;
    private Location currentLocation;
    private RoomSaveAndLoad roomSaveAndLoad;
    private int eventID;

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
        roomSaveAndLoad = new RoomSaveAndLoad(getApplicationContext(), this);
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
                long startTime = System.currentTimeMillis();
                if (currentLocation != null) {
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
                }
                if (System.currentTimeMillis() - startTime >= 5000) {
                    Toast.makeText(CreateOEvent.this, "Could not find position", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if ((latLngArrayList.size() > 0) && (arrayListWithCoords.size() == 0)) {
            for (LatLng latlng : latLngArrayList) {
                Marker Point = mMap.addMarker(new MarkerOptions().position(latlng).title(R.string.point + " " + (arrayListWithCoords.size() + 1)));
                arrayListWithCoords.add(Point);
            }
        }
        Toast.makeText(getApplicationContext(), R.string.click_map_add_points_toast, Toast.LENGTH_SHORT).show();
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
        builder.setTitle(R.string.add_new_point);
        EditText input = new EditText(this);
        input.setHint(R.string.name);
        if (marker != null) {
            builder.setTitle(R.string.change_name);
            input.setText(marker.getTitle());
        }
        builder.setView(input);
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
        CharSequence[] elements = {getString(R.string.set_as_startpoint), getString(R.string.edit), getString(R.string.delete)};
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
            point = mMap.addMarker(new MarkerOptions().position(location).title(getString(R.string.point) + " " + (arrayListWithCoords.size() + 1)).draggable(true));
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
        Toast.makeText(getApplicationContext(), getString(R.string.added)+ " " + positions.size() + " " + getString(R.string.points) +".", Toast.LENGTH_SHORT).show();
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
        final Boolean[] hasFocused = {true};
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
                    if (mMap != null && hasFocused[0]) {
                        hasFocused[0] = false;
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
                    }
                }
            };
            lm.requestLocationUpdates(locationRequest, mLocationCallback, getMainLooper());
        } else {
            Toast.makeText(getApplicationContext(), R.string.location_permission_prompt_toast, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Adds a marker to your position if it is available
     *
     * @param v Button that fires the method
     */
    public void addMarkerMyPosition(View v) {
        if(currentLocation!=null) {
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
                addNewMarker(null, new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Position not yet found",Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), R.string.event_name_promt_toast, Toast.LENGTH_SHORT).show();
        } else if (startPoint == null) {
            Toast.makeText(getApplicationContext(), R.string.event_startpoint_prompt_toast, Toast.LENGTH_SHORT).show();
        } else {
            Event event = new Event();
            String eventTitle = eventTitleField.getText().toString();
            event.addProperty("event_name", eventTitle);
            Point sp = new Point(startPoint.getPosition().latitude, startPoint.getPosition().longitude, startPoint.getTitle());
            event.setStartPoint(sp);
            for (Marker marker : arrayListWithCoords) {
                if (!marker.equals(startPoint)) {
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
                        Toast.makeText(getApplicationContext(), R.string.failed_create_event_toast, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), String.format(getString(R.string.event_added_formated), event.getProperty("event_name")), Toast.LENGTH_SHORT).show();
                        eventID = object.getId();
                        roomSaveAndLoad.saveRoomEvent(object);

                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.couldnt_connect_to_net, Toast.LENGTH_SHORT).show();
                }
            });
            //StartupMenu.addEvent(event);
            //Toast.makeText(getApplicationContext(), "Lagret ruten '" + eventTitle + "', " + arrayListWithCoords.size() + " punkt registrert", Toast.LENGTH_SHORT).show();
            //LAGRE
            //Reset
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.general_menu, menu);

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
                NetworkManager.getInstance().logOut();
                finish();
                break;
        }

        supportInvalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void whenRoomFinished(Object savedAll) {
        NetworkManager.getInstance().subscribeToEvent(eventID, new ICallbackAdapter<List<Event>>() {
            @Override
            public void onResponse(List<Event> object) {
                if(object != null){
                    Log.d("Subscribe", String.format("List<Event> has events: %d", object.size()));
                }
                else {
                    Log.d("Subscribe", "List<Event> is null");
                }
                finish();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Subscribe", t.getMessage());
                finish();
            }
        });

    }
}
