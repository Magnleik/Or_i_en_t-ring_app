package no.teacherspet.tring.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;

import connection.ICallbackAdapter;
import connection.NetworkManager;
import connection.Point;
import no.teacherspet.tring.R;

/**
 * Created by magnus on 24.04.2018.
 */

public class AddExistingPoint extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    FusedLocationProviderClient lm;
    NetworkManager networkManager;
    ArrayList<Point> selectedPoints;
    Point selectedPoint;
    ClusterManager<Point> manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkManager = NetworkManager.getInstance();
        selectedPoints = new ArrayList<>();
        setContentView(R.layout.activity_add_existing_point);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_of_existing_points);
        mapFragment.getMapAsync(this);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lm = LocationServices.getFusedLocationProviderClient(this);
        }
    }

    @Override
    /*
    Gets called when the map is set up. Sets up the clustermanager and sets the map to focus on the correct place. Also defines the clickListeners
     */
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        manager = new ClusterManager<Point>(this, mMap);
        manager.setAnimation(false);
        manager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Point>() {
            @Override
            public boolean onClusterItemClick(Point point) {
                //Toast.makeText(AddExistingPoint.this,"ClusterItem clicked", Toast.LENGTH_SHORT).show();

                ((DefaultClusterRenderer) manager.getRenderer()).getMarker(point).showInfoWindow();

                if (selectedPoints.contains(point)) {
                    selectedPoints.remove(point);
                    ((DefaultClusterRenderer) manager.getRenderer()).getMarker(point).setIcon(BitmapDescriptorFactory.defaultMarker());
                    return false;
                } else {
                    if (selectedPoint != null) {
                        ((DefaultClusterRenderer) manager.getRenderer()).getMarker(selectedPoint).setIcon(BitmapDescriptorFactory.defaultMarker());
                    }
                    selectedPoint = point;
                    ((DefaultClusterRenderer) manager.getRenderer()).getMarker(selectedPoint).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    return true;
                }
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (selectedPoint != null) {
                    if (((DefaultClusterRenderer) manager.getRenderer()).getMarker(selectedPoint) != null) {
                        ((DefaultClusterRenderer) manager.getRenderer()).getMarker(selectedPoint).setIcon(BitmapDescriptorFactory.defaultMarker());
                    }
                }
                selectedPoint = null;
            }
        });
        mMap.setOnCameraIdleListener(manager);
        mMap.setOnMarkerClickListener(manager);
        mMap.setOnInfoWindowClickListener(manager);
        mMap.setMaxZoomPreference(20);

        CameraPosition camPos = getIntent().getParcelableExtra("map_center");
        networkManager.getNearbyPoints(camPos.target.latitude, camPos.target.longitude, 1000, new ICallbackAdapter<ArrayList<Point>>() {
            @Override
            public void onResponse(ArrayList<Point> object) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                if (object != null) {
                    if (!object.isEmpty()) {
                        for (Point point : object) {
                            manager.addItem(point);
                            builder.include(new LatLng(point.getLatitude(), point.getLongitude()));
                        }
                        Toast.makeText(getApplicationContext(), R.string.select_points_toast, Toast.LENGTH_SHORT).show();
                        LatLngBounds bounds = builder.build();

                        mMap.setOnMapLoadedCallback(() -> {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                            mMap.setLatLngBoundsForCameraTarget(bounds);
                            mMap.moveCamera(CameraUpdateFactory.zoomOut());
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_known_points_in_area, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.something_wrong_toast, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Gets called when a user presses the add button. Adds the currently selected point to a list of selevted points if there is any
     *
     * @param v the button pressed
     */
    public void addButtonClick(View v) {
        if (selectedPoint != null) {
            selectedPoints.add(selectedPoint);
            ((DefaultClusterRenderer) manager.getRenderer()).getMarker(selectedPoint).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            selectedPoint = null;
        }
    }

    /**
     * Gets called when a user presses the done button. Finishes the activity and sends the list of selected points back to the parent activity.
     *
     * @param v the button pressed
     */
    public void doneAddingClick(View v) {
        Intent intent = new Intent();
        intent.putExtra("selectedPoints", selectedPoints);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    /*
     * Defines the menu elements to be present in the activity
     */
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
                setResult(RESULT_CANCELED, null);
                finish();
                break;
            case (R.id.log_out_menu):
                Intent intent = new Intent(this, OrientationSelector.class);
                intent.putExtra("Logout", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
        }
        supportInvalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

}
