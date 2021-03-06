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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import connection.Event;
import connection.ICallbackAdapter;
import connection.NetworkManager;
import connection.Point;
import no.teacherspet.tring.R;
import no.teacherspet.tring.util.RoomInteract;
import no.teacherspet.tring.util.RoomSaveAndLoad;

public class CreateOEvent extends AppCompatActivity implements OnMapReadyCallback, RoomInteract {

    private GoogleMap mMap;
    private ArrayList<Point> arrayListWithCoords = new ArrayList<>();
    private ArrayList<Point> latLngArrayList = new ArrayList<>();
    private FusedLocationProviderClient lm;
    private LocationCallback mLocationCallback;
    private Point startPoint;
    private Location currentLocation;
    private RoomSaveAndLoad roomSaveAndLoad;
    private ClusterManager manager;
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
            latLngArrayList = (ArrayList<Point>) savedInstanceState.getSerializable("points");
        }
        roomSaveAndLoad = new RoomSaveAndLoad(getApplicationContext(), this);
    }

    @Override
    /*
    Removes the location request if there is one present
     */
    protected void onDestroy() {
        super.onDestroy();
        if (lm != null && mLocationCallback != null) {
            lm.removeLocationUpdates(mLocationCallback);
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
    /*
    moves and focuses the map once it is ready to.
     */
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        manager = new ClusterManager<Point>(this, mMap);
        manager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener() {
            @Override
            public boolean onClusterItemClick(ClusterItem clusterItem) {
                openEditDialog((Point) clusterItem);
                return false;
            }
        });
        mMap.setOnCameraIdleListener(manager);
        mMap.setOnMarkerClickListener(manager);
        mMap.setOnInfoWindowClickListener(manager);

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
            for (Point point : latLngArrayList) {
                Point myPoint = point;
                arrayListWithCoords.add(myPoint);
            }
        }
        Toast.makeText(getApplicationContext(), R.string.click_map_add_points_toast, Toast.LENGTH_SHORT).show();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            public void onMapClick(LatLng latLng) {
                openAddDialog(null, latLng);
            }
        });
        manager.cluster();
    }

    /**
     * Opens a dialog to set and define a point on the map to be added to the list of points in the event
     *
     * @param marker   the marker that is being edited. If adding a new point, this can be null
     * @param position the Latitude and Longitude of the point pressed on the map
     */
    private void openAddDialog(Point marker, LatLng position) {
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
                    //marker.setTitle(input.getText().toString());
                    marker.addProperty("name", input.getText().toString());
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

    /**
     * Opens a dialog with editing options of a marker. Can be to set as starting point, edit the attributes of the marker and delete it from the list of points.
     *
     * @param marker
     */
    private void openEditDialog(Point marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(marker.getTitle());
        CharSequence[] elements = {getString(R.string.set_as_startpoint), getString(R.string.edit), getString(R.string.delete)};
        builder.setItems(elements, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (startPoint != null) {
                            ((DefaultClusterRenderer) manager.getRenderer()).getMarker(startPoint).setIcon(BitmapDescriptorFactory.defaultMarker());
                        }
                        ((DefaultClusterRenderer) manager.getRenderer()).getMarker(marker).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
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
        intent.putExtra("map_center", mMap.getCameraPosition());
        intent.putExtra("map_radius", getMapRadius());
        intent.putExtra("map_bounds", mMap.getProjection().getVisibleRegion().latLngBounds);
        startActivityForResult(intent, 2);
    }

    private float getMapRadius(){
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        float[] dist = new float[1];
        Location.distanceBetween(bounds.northeast.latitude,bounds.northeast.longitude,bounds.southwest.latitude,bounds.southwest.longitude,dist);
        return dist[0];
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

    /**
     * Adds a marker to the map.
     *
     * @param name     Title of the marker
     * @param location Latitude and Longitude where the marker is to be placed
     */
    private void addNewMarker(String name, LatLng location) {
        Point point;
        if (name != null) {
            point = new Point(location.latitude, location.longitude, null, name);
            manager.addItem(point);
            manager.cluster();
            arrayListWithCoords.add(point);
        } else {
            point = new Point(location.latitude, location.longitude, null, getString(R.string.point) + " " + (arrayListWithCoords.size() + 1));
            manager.addItem(point);
            manager.cluster();
            arrayListWithCoords.add(point);
        }
    }

    /**
     * Adds a marker to the map.
     *
     * @param point The point to be added
     */
    private void addNewMarker(Point point) {
        manager.addItem(point);
        manager.cluster();
        arrayListWithCoords.add(point);
    }


    /**
     * Adds a list of existing points to the current points in the event
     *
     * @param data
     */
    private void addExistingMarkers(Intent data) {
        Serializable items = data.getSerializableExtra("selectedPoints");
        ArrayList<Point> positions = (ArrayList<Point>) items;
        for (Point position : positions) {
            addNewMarker(position);
        }
        Toast.makeText(getApplicationContext(), getString(R.string.added) + " " + positions.size() + " " + getString(R.string.points) + ".", Toast.LENGTH_SHORT).show();
    }

    /**
     * Removes the latest element in the marker array. Method gets called when the "Slett forrige" button is pressed
     *
     * @param marker to be removed
     */
    private void deletePoint(Point marker) {
        if (marker.equals(startPoint)) {
            startPoint = null;
        }
        if (arrayListWithCoords.size() > 0) {
            manager.removeItem(marker);
            manager.cluster();
            arrayListWithCoords.remove(marker);
        }
    }

    /**
     * creates a locationRequest to update the CurrentLocation variable as often as possible. If a reading is to inaccurate, it will discard it
     */
    private void createLocationRequest() {

        final Boolean[] hasFocused = {true};
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lm = LocationServices.getFusedLocationProviderClient(this);
            LocationRequest locationRequest = new LocationRequest();

            locationRequest.setInterval(1000).setFastestInterval(500).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            mLocationCallback = new LocationCallback() {
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
        if (currentLocation != null) {
            boolean shouldAdd = true;
            Location markerLocation;
            for (Point marker : arrayListWithCoords) {
                markerLocation = new Location("");
                markerLocation.setLatitude(marker.getPosition().latitude);
                markerLocation.setLongitude(marker.getPosition().longitude);
                if (currentLocation.distanceTo(markerLocation) <= 5) {
                    shouldAdd = false;
                }
            }
            if (shouldAdd) {
                addNewMarker(null, new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.pos_not_yet_found, Toast.LENGTH_SHORT).show();
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
            Point sp = startPoint;
            event.setStartPoint(sp);
            for (Point marker : arrayListWithCoords) {
                if (!marker.equals(startPoint)) {
                    if (event.getPoints() == null) {
                        event.setStartPoint(marker);
                    } else {
                        event.addPost(marker);
                    }
                }
            }

            // Kalkuler distanse
            double distance = 0;

            for (Point point : event.getPoints()) {
                int index = event.getPoints().indexOf(point);
                if (index == event.getPoints().size() - 1) {
                    break;
                }
                distance += point.getDistanceFromPoint(new LatLng(event.getPoints().get(index + 1).getLatitude(), event.getPoints().get(index + 1).getLongitude()));
            }

            event.addProperty("dist", distance + "");

            NetworkManager.getInstance().addEvent(event, new ICallbackAdapter<Event>() {
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
            for (Point marker : arrayListWithCoords) {
                latLngArrayList.add(marker);
            }
        }
        outState.putSerializable("points", latLngArrayList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
    /*
     * Handles commands if any element in the toolbar gets pressed. If necessary, it sends a broadcast to the fragments of the activity
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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
                if (object != null) {
                    Log.d("Subscribe", String.format("List<Event> has events: %d", object.size()));
                } else {
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
