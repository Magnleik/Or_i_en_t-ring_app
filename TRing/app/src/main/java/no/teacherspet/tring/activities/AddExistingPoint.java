package no.teacherspet.tring.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
    ArrayList<LatLng> selectedPoints;
    Marker selectedMarker;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        setResult(RESULT_CANCELED, null);
        finish();
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "App needs permission to access location services on phone to run", Toast.LENGTH_LONG).show();
            finish();
        } else {
            lm.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    networkManager.getNearbyPoints(location.getLatitude(), location.getLongitude(), 1000, new ICallbackAdapter<ArrayList<Point>>() {
                        @Override
                        public void onResponse(ArrayList<Point> object) {
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            if (object != null) {
                                if (!object.isEmpty()) {
                                    for (Point point : object) {
                                        mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude())));
                                        builder.include(new LatLng(point.getLatitude(), point.getLongitude()));
                                    }
                                    Toast.makeText(getApplicationContext(), "Select points you wish to add", Toast.LENGTH_SHORT).show();
                                    LatLngBounds bounds = builder.build();

                                    mMap.setOnMapLoadedCallback(() -> {
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                                        mMap.setLatLngBoundsForCameraTarget(bounds);
                                        mMap.moveCamera(CameraUpdateFactory.zoomOut());
                                    });
                                }
                                mMap.setOnMarkerClickListener(marker -> {
                                    if (selectedPoints.contains(marker.getPosition())) {
                                        selectedPoints.remove(marker.getPosition());
                                        marker.setIcon(BitmapDescriptorFactory.defaultMarker());
                                        return false;
                                    } else {
                                        if (selectedMarker != null) {
                                            selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
                                        }
                                        selectedMarker = marker;
                                        selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                        return true;
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            });
        }
    }

    public void addButtonClick(View v) {
        if (selectedMarker != null) {
            selectedPoints.add(selectedMarker.getPosition());
            selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            selectedMarker = null;
        }
    }

    public void doneAddingClick(View v) {
        Intent intent = new Intent();
        intent.putExtra("selectedPositions", selectedPoints);
        setResult(RESULT_OK, intent);
        finish();
    }


}
