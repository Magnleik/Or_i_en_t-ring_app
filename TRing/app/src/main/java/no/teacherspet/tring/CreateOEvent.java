package no.teacherspet.tring;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class CreateOEvent extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Marker> arrayListWithCoords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_oevent);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_under_creation);
        mapFragment.getMapAsync(this);
        //Log.i("INFO:", getString(R.string.google_maps_key));
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
        // Add a marker in Sydney and move the camera
        LatLng gløs = new LatLng(63.416136, 10.405297);
        mMap.addMarker(new MarkerOptions().position(gløs).title("Gløs<3"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(gløs));
    }

    public void createPoints(View v) {

        Toast.makeText(getApplicationContext(), "Klikk på kartet for å legge til punkter.", Toast.LENGTH_LONG).show();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            public void onMapClick(LatLng latLng) {
                Marker Point = mMap.addMarker(new MarkerOptions().position(latLng).title("Punkt " + (arrayListWithCoords.size()+1)));
                arrayListWithCoords.add(Point);
                // Sjekk at punkt blir registrert
                // Toast.makeText(getApplicationContext(), "" + arrayListWithCoords.get(arrayListWithCoords.size() -1) , Toast.LENGTH_LONG).show();

            }
        });

    }

    public void deleteLastPoint(View v) {
        if (arrayListWithCoords.size() > 0) {
            Marker lastMarker = arrayListWithCoords.get(arrayListWithCoords.size() - 1);
            lastMarker.remove();
            arrayListWithCoords.remove(lastMarker);
        }
    }

    public void saveEvent(View v) {
        EditText eventTitleField = (EditText)findViewById(R.id.create_event_name);
        String eventTitle = eventTitleField.getText().toString();
        Toast.makeText(getApplicationContext(), "Lagret ruten '" + eventTitle + "', " + arrayListWithCoords.size()+  " punkt registrert" , Toast.LENGTH_LONG).show();
        ArrayList<Marker> finalList = arrayListWithCoords;

        //LAGRE
        //Reset

        eventTitleField.setText("");
        arrayListWithCoords.clear();
        mMap.clear();
        //Add startpoint om man vil lage ny rute?
        mMap.addMarker(new MarkerOptions().position(new LatLng(63.416136, 10.405297)).title("Gløs<3"));

    }




}