package no.teacherspet.tring;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import connection.Point;

public class PerformOEvent extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private int positionViewed = 0;
    private ArrayList<Point> points;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        points = readPoints();
        setContentView(R.layout.activity_perform_oevent);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_used_in_event);
        mapFragment.getMapAsync(this);
        //Log.i("INFO:", getString(R.string.google_maps_key));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public ArrayList<Point> readPoints(){
        if(!StartupMenu.getTestEvents().isEmpty()) {
            return StartupMenu.getTestEvents().get(StartupMenu.getTestEvents().size() - 1).getPoints();
        }
        return null;
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
        if(points!=null) {
            for (Point point : points) {
                if (point != null) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude())).title((String) point.getProperty("event_name")));
                    builder.include(new LatLng(point.getLatitude(),point.getLongitude()));
                }
            }
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(avgPosition));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,-10));
        }
    }





    public void showLocationButtonPressed(View v) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Task getLocation = mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {

                        //Oppretter og viser en markor der hvor bruker er
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();
                        LatLng latlng = new LatLng(latitude, longitude);
                        final Marker posisjonsmarkor = mMap.addMarker(new MarkerOptions().position(latlng).title("HER ER DU"));
                        //Zoomer til posisjon
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                        positionViewed++;


                        //Markor fjernes etter 5 sekund
                        CountDownTimer synlig = new CountDownTimer(5000, 1000) {
                            int sek = 5;
                            @Override
                            public void onTick(long l) {
                                sek--;
                                Toast.makeText(getApplicationContext(), "Dette er " + positionViewed + ".gang posisjonen vises, markor fjernes om " + sek+  " sekunder" , Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFinish() {
                                posisjonsmarkor.remove();

                            }
                        }.start();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Du må gi appen tilgang til bruk av GPS." , Toast.LENGTH_LONG).show();

        }
    }

    private LatLng getAvgLatLng(){
        double lat=0;
        double lng=0;
        int numPoints=0;
        if (points != null) {
            for (Point point : points) {
                if (point != null) {
                    lat += point.getLatitude();
                    lng += point.getLongitude();
                    numPoints++;
                }
            }
        }
        lat=lat/numPoints;
        lng=lng/numPoints;
        return new LatLng(lat,lng);
    }

    public ArrayList<Point> getPoints() {
        return points;
    }
}

