package no.teacherspet.tring.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import connection.Event;
import connection.Point;
import no.teacherspet.tring.Database.Entities.PointOEventJoin;
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
    private Button continueButton;
    private Event activeEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientation_selector);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        localDatabase = LocalDatabase.getInstance(this);
        eventViewModel = new OEventViewModel(localDatabase.oEventDAO());
        joinViewModel = new PointOEventJoinViewModel(localDatabase.pointOEventJoinDAO());

        continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setEnabled(false);
        continueButton.setOnClickListener(v -> continueEvent());

        eventViewModel.getActiveEvent().subscribe(roomOEvents -> checkActiveEvent(roomOEvents));
    }
    private void continueEvent(){
        Intent intent = new Intent(OrientationSelector.this, PerformOEvent.class);
        intent.putExtra("MyEvent", activeEvent);
        startActivity(intent);
    }

    private void checkActiveEvent(List<RoomOEvent> activeEvents){
        if(activeEvents.size() > 0){
            RoomOEvent roomEvent = activeEvents.get(0);
            Event event = new Event();
            event._setId(roomEvent.getId());
            for(String key : roomEvent.getProperties().keySet()){
                event.addProperty(key, roomEvent.getProperties().get(key));
            }
            joinViewModel.getPointsForOEvent(event.getId()).subscribe(roomPoints -> {
                if(roomPoints.size() > 0){
                    joinViewModel.getJoinsForOEvent(event.getId()).subscribe(joins -> addPointsToEvent(event, roomPoints, joins));
                }
            });
        }
        Toast.makeText(this, "Active events: " + activeEvents.size(), Toast.LENGTH_SHORT).show();
    }
    private void addPointsToEvent(Event event, List<RoomPoint> roomPoints, List<PointOEventJoin> joins){
        for (RoomPoint roomPoint : roomPoints){
            for (PointOEventJoin join : joins){
                if(roomPoint.getId() == join.getPointID()){
                    Point point = setupPoint(roomPoint, join.isVisited());
                    if(join.isStart()){
                        event.setStartPoint(point);
                    }
                    else{
                        event.addPost(point);
                    }
                }
            }
        }
        activeEvent = event;
        continueButton.setEnabled(true);
    }
    private Point setupPoint(RoomPoint roomPoint, boolean visited){
        Point point = new Point(roomPoint.getLatLng().latitude, roomPoint.getLatLng().longitude, "placeholder");
        point._setId(roomPoint.getId());
        point.setVisited(visited);
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
