package no.teacherspet.tring.activities;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkManager = NetworkManager.getInstance();
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "App needs permission to access location services on phone to run", Toast.LENGTH_LONG).show();
            finish();
        } else {
            lm.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        networkManager.getNearbyPoints(location.getLatitude(), location.getLongitude(), 200, new ICallbackAdapter<ArrayList<Point>>() {
                            @Override
                            public void onResponse(ArrayList<Point> object) {
                                for(Point point:object){
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(),point.getLongitude())));
                                    Toast.makeText(getApplicationContext(),"Select points you wish to add",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }
}
