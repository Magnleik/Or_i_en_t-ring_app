package no.teacherspet.tring;

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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import connection.Event;
import connection.Point;
import no.teacherspet.tring.Database.Entities.PointOEventJoin;
import no.teacherspet.tring.Database.Entities.RoomOEvent;
import no.teacherspet.tring.Database.Entities.RoomPoint;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.OEventViewModel;
import no.teacherspet.tring.Database.ViewModels.PointOEventJoinViewModel;
import no.teacherspet.tring.Database.ViewModels.PointViewModel;

public class CreateOEvent extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Marker> arrayListWithCoords = new ArrayList<>();
    private ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    private FusedLocationProviderClient lm;
    private LatLng position;
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

    //TODO Lagre OEvent i lokal database etter oppretting

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
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            position = new LatLng(10.416136, 10.405297);
            mMap.addMarker(new MarkerOptions().position(position).title("Gløs<3"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,15));
        }
        else{
            lm.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null){
                        position = new LatLng(location.getLatitude(),location.getLongitude());
                    }
                    else{
                        position = new LatLng(10.416136, 10.405297);
                    }
                    mMap.addMarker(new MarkerOptions().position(position).title("Gløs<3"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,15));
                }
            });
        }

        if ((latLngArrayList.size()>0) && (arrayListWithCoords.size() == 0)) {
            for (LatLng latlgn : latLngArrayList) {
                Marker Point = mMap.addMarker(new MarkerOptions().position(latlgn).title("Punkt " + (arrayListWithCoords.size() + 1)));
                arrayListWithCoords.add(Point);
                this.createPoints(findViewById(R.id.map_under_creation));
            }
        }
    }

    public void createPoints(View v) {

        Toast.makeText(getApplicationContext(), "Klikk på kartet for å legge til punkter.", Toast.LENGTH_LONG).show();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            public void onMapClick(LatLng latLng) {
                //Marker Point = mMap.addMarker(new MarkerOptions().position(latLng).title("Punkt " + (arrayListWithCoords.size()+1)));
                //arrayListWithCoords.add(Point);
                position = latLng;
                Intent intent = new Intent(CreateOEvent.this,PopupPointDesc.class);
                startActivityForResult(intent,1);
                // Sjekk at punkt blir registrert
                // Toast.makeText(getApplicationContext(), "" + arrayListWithCoords.get(arrayListWithCoords.size() -1) , Toast.LENGTH_LONG).show();

            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(data!=null) {
            String name = data.getStringExtra("MarkerName");
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    if (name != null) {
                        Marker point = mMap.addMarker(new MarkerOptions().position(position).title(name));
                        arrayListWithCoords.add(point);
                    } else {
                        Marker point = mMap.addMarker(new MarkerOptions().position(position).title("Punkt " + (arrayListWithCoords.size() + 1)));
                        arrayListWithCoords.add(point);
                    }

                }
            }
        }
    }

    public void deleteLastPoint(View v) {
        if (arrayListWithCoords.size() > 0) {
            Marker lastMarker = arrayListWithCoords.get(arrayListWithCoords.size() - 1);
            lastMarker.remove();
            arrayListWithCoords.remove(lastMarker);
        }
    }

    public void saveEvent(View v) {
        EditText eventTitleField = (EditText) findViewById(R.id.create_event_name);
        Event event = new Event();
        String eventTitle = eventTitleField.getText().toString();
        event.addProperty("event_name",eventTitle);
        for(Marker marker:arrayListWithCoords){
            event.addPost(new Point(marker.getPosition().latitude,marker.getPosition().longitude,marker.getTitle()));
        }
        StartupMenu.addEvent(event);
        saveEventToRoom(event);
        Toast.makeText(getApplicationContext(), "Lagret ruten '" + eventTitle + "', " + arrayListWithCoords.size() + " punkt registrert", Toast.LENGTH_LONG).show();
        //LAGRE
        //Reset

        eventTitleField.setText("");
        arrayListWithCoords.clear();
        mMap.clear();
        //Add startpoint om man vil lage ny rute?
        mMap.addMarker(new MarkerOptions().position(new LatLng(63.416136, 10.405297)).title("Gløs<3"));
    }

    /**
     * Saves the Event and Points, and adds connections between them in the Room database
     */
    private void saveEventToRoom(Event event){
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

            if(i > 0){
                join = new PointOEventJoin(point.getId(), event.getId(), false);
            }
            else{
                join = new PointOEventJoin(point.getId(), event.getId(), true);
            }
            roomPoints[i] = roomPoint;
            joins[i] = join;
        }
        pointViewModel.addPoints(roomPoints).subscribe(longs -> checkSave(longs));
        pointOEventJoinViewModel.addJoin(joins).subscribe(longs -> checkSave(longs));

    }

    private void checkSave(long[] longs) {
        boolean allSaved= true;
        for (int i = 0; i < longs.length; i++) {
            if(longs[i] < 0){
                allSaved = false;
            }
        }
        if(allSaved){
            Toast.makeText(this, "Save successfully", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
        }
    }

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
