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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        CameraPosition camPos = getIntent().getParcelableExtra("map_center");
            networkManager.getNearbyPoints(camPos.target.latitude, camPos.target.longitude, 1000, new ICallbackAdapter<ArrayList<Point>>() {
            @Override
            public void onResponse(ArrayList<Point> object) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                if (object != null) {
                    if (!object.isEmpty()) {
                        for (Point point : object) {
                            mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude())));
                            builder.include(new LatLng(point.getLatitude(), point.getLongitude()));
                        }
                        Toast.makeText(getApplicationContext(), R.string.select_points_toast, Toast.LENGTH_SHORT).show();
                        LatLngBounds bounds = builder.build();

                        mMap.setOnMapLoadedCallback(() -> {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                            mMap.setLatLngBoundsForCameraTarget(bounds);
                            mMap.moveCamera(CameraUpdateFactory.zoomOut());
                        });
                    }else{
                        Toast.makeText(getApplicationContext(), R.string.no_known_points_in_area, Toast.LENGTH_SHORT).show();
                        finish();
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
                Toast.makeText(getApplicationContext(), R.string.something_wrong_toast, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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
        if(!NetworkManager.getInstance().isAuthenticated()){
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
