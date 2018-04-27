package no.teacherspet.tring.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import connection.Event;
import connection.NetworkManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientation_selector);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        localDatabase = LocalDatabase.getInstance(this);
        eventViewModel = new OEventViewModel(localDatabase.oEventDAO());
        joinViewModel = new PointOEventJoinViewModel(localDatabase.pointOEventJoinDAO());

        //TODO Start PerformOEvent with this event
        eventViewModel.getActiveEvent().subscribe(roomOEvents -> checkActiveEvent(roomOEvents));
    }

    private void checkActiveEvent(List<RoomOEvent> activeEvents){
        if(activeEvents.size() > 0){
            RoomOEvent roomEvent = activeEvents.get(0);
            Event activeEvent = new Event();
            activeEvent._setId(roomEvent.getId());
            for(String key : roomEvent.getProperties().keySet()){
                activeEvent.addProperty(key, roomEvent.getProperties().get(key));
            }
            joinViewModel.getStartPoint(activeEvent.getId()).subscribe(startPoints -> {
                if(startPoints.size() > 0){
                    joinViewModel.getJoinsForOEvent(activeEvent.getId()).subscribe(joins ->
                        joinViewModel.getStartPoint(activeEvent.getId()).subscribe(points -> addPointsToEvent(activeEvent, startPoints.get(0), points, joins))
                    );
                }
            });
        }
        Toast.makeText(this, "Active events: " + activeEvents.size(), Toast.LENGTH_SHORT).show();
    }
    private void addPointsToEvent(Event event, RoomPoint startPoint, List<RoomPoint> roomPoints, List<PointOEventJoin> joins){
        event.setStartPoint(setupPoint(startPoint, getVisited(startPoint, joins)));
        for(RoomPoint roomPoint : roomPoints){
            event.addPost(setupPoint(roomPoint, getVisited(roomPoint, joins)));
        }
        Intent intent = new Intent(OrientationSelector.this, PerformOEvent.class);
        intent.putExtra("MyEvent", event);
        startActivity(intent);

    }
    private boolean getVisited(RoomPoint point, List<PointOEventJoin> joins){
        for(PointOEventJoin join : joins){
            if(join.getPointID() == point.getId()){
                return join.isVisited();
            }
        }
        return false;
    }
    private Point setupPoint(RoomPoint roomPoint, boolean visited){
        Point point = new Point(roomPoint.getLatLng().latitude, roomPoint.getLatLng().longitude, "placeholder");
        point._setId(roomPoint.getId());
        //TODO Add visited field to Point
        //point.setVisited(visited);
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
                finish();
                break;
            case (R.id.log_out_menu):
                NetworkManager.getInstance().logOut();
                break;
        }

        supportInvalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }
}
