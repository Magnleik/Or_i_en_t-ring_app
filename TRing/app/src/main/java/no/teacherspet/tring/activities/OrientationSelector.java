package no.teacherspet.tring.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import connection.Event;
import connection.Point;
import no.teacherspet.tring.Database.Entities.RoomOEvent;
import no.teacherspet.tring.Database.Entities.RoomPoint;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.OEventViewModel;
import no.teacherspet.tring.Database.ViewModels.PointOEventJoinViewModel;
import no.teacherspet.tring.R;

public class OrientationSelector extends AppCompatActivity {

    private LocalDatabase localDatabase;
    private OEventViewModel eventViewModel;
    private PointOEventJoinViewModel joinViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientation_selector);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        localDatabase = LocalDatabase.getInstance(this);
        eventViewModel = new OEventViewModel(localDatabase.oEventDAO());
        joinViewModel = new PointOEventJoinViewModel(localDatabase.pointOEventJoinDAO());

        eventViewModel.getActiveEvent().subscribe(roomOEvents -> checkActiveEvent(roomOEvents));
    }

    private void checkActiveEvent(List<RoomOEvent> activeEvents){
        //TODO Start PerformOEvent with this event
        if(activeEvents.size() > 0){
            RoomOEvent roomEvent = activeEvents.get(0);
            Event activeEvent = new Event();
            activeEvent._setId(roomEvent.getId());
            for(String key : roomEvent.getProperties().keySet()){
                activeEvent.addProperty(key, roomEvent.getProperties().get(key));
            }
            //TODO Need to get visited/not visited from joins
            joinViewModel.getStartPoint(activeEvent.getId()).subscribe(startPoints -> {
                if(startPoints.size() > 0){
                    joinViewModel.getStartPoint(activeEvent.getId()).subscribe(points -> addPointsToEvent(activeEvent, startPoints.get(0), points));
                }
            });
        }
        Toast.makeText(this, "Active events: " + activeEvents.size(), Toast.LENGTH_SHORT).show();
    }
    private void addPointsToEvent(Event event, RoomPoint startPoint, List<RoomPoint> roomPoints){
        event.setStartPoint(setupPoint(startPoint));
        for(RoomPoint roomPoint : roomPoints){
            event.addPost(setupPoint(roomPoint));
        }
        //TODO Start PerformOEvent with event
    }
    private Point setupPoint(RoomPoint roomPoint){
        Point point = new Point(roomPoint.getLatLng().latitude, roomPoint.getLatLng().longitude, "placeholder");
        point._setId(roomPoint.getId());
        for(String key : roomPoint.getProperties().keySet()){
            point.addProperty(key, roomPoint.getProperties().get(key));
        }
        return point;
    }

    /**
     * Moves to the lists of available events. Method gets called when the "Gjennomfør løp" button is pressed
     * @param v
     */
    public void goToOrientationList (View v){
        Intent intent = new Intent(OrientationSelector.this,ListOfSavedEvents.class);
        startActivity(intent);
    }

    /**
     * Goes to the screen for creating new events. Method gets called when the "Lag nytt løp" button is pressed
     * @param v
     */
    public void createEvent (View v){
        Intent intent = new Intent(OrientationSelector.this, CreateOEvent.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
