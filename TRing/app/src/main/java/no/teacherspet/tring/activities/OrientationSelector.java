package no.teacherspet.tring.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import connection.Event;
import connection.ICallbackAdapter;
import connection.NetworkManager;
import connection.Point;
import no.teacherspet.tring.Database.Entities.PointOEventJoin;
import no.teacherspet.tring.Database.Entities.RoomOEvent;
import no.teacherspet.tring.Database.Entities.RoomPoint;
import no.teacherspet.tring.Database.Entities.RoomUser;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.OEventViewModel;
import no.teacherspet.tring.Database.ViewModels.PointOEventJoinViewModel;
import no.teacherspet.tring.Database.ViewModels.UserViewModel;
import no.teacherspet.tring.R;
import no.teacherspet.tring.util.GeneralProgressDialog;

public class OrientationSelector extends AppCompatActivity {

    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION=1;
    private LocalDatabase localDatabase;
    private OEventViewModel eventViewModel;
    private PointOEventJoinViewModel joinViewModel;
    private Button continueButton;
    private Event activeEvent;
    private GeneralProgressDialog progressDialog;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientation_selector);
        requestAccess();
        progressDialog = new GeneralProgressDialog(this,this);
        localDatabase = LocalDatabase.getInstance(this);
        userViewModel = new UserViewModel(localDatabase.userDAO());
        eventViewModel = new OEventViewModel(localDatabase.oEventDAO());
        joinViewModel = new PointOEventJoinViewModel(localDatabase.pointOEventJoinDAO());

        continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setEnabled(false);
        continueButton.setOnClickListener(v -> continueEvent());

        if(!NetworkManager.getInstance().isAuthenticated()){
            userViewModel.getAllUsers().subscribe(users -> checkUser(users));
        }

        //TODO Start PerformOEvent with this event
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
    //Changes to createUserActivity if a roomUser has not been created
    private void checkUser(List<RoomUser> roomUsers){
        progressDialog.show();
        if (roomUsers.size() > 0) {
            RoomUser[] users = new RoomUser[roomUsers.size()];
            for (int i = 0; i < roomUsers.size(); i++) {
                users[i] = roomUsers.get(i);
            }
            NetworkManager.getInstance().logInWithToken(roomUsers.get(0).getToken(), new ICallbackAdapter<Boolean>() {
                @Override
                public void onResponse(Boolean object) {
                    progressDialog.hide();
                    if (object != null) {
                        if (object) {
                            Toast.makeText(OrientationSelector.this, "Logged in", Toast.LENGTH_SHORT).show();
                        } else {
                            userViewModel.deleteUsers(users).subscribe(integers ->
                                    startActivity(new Intent(OrientationSelector.this, LogInActivity.class)));
                        }
                    } else {
                        userViewModel.deleteUsers(users).subscribe(integers ->
                                startActivity(new Intent(OrientationSelector.this, LogInActivity.class)));
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    progressDialog.hide();
                    userViewModel.deleteUsers(users).subscribe(integers ->
                            startActivity(new Intent(OrientationSelector.this, LogInActivity.class)));
                }
            });
        } else {
            progressDialog.hide();
            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show();
        }
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

    public void logInButton(View v){
        Intent intent = new Intent(OrientationSelector.this,LogInActivity.class);
        startActivity(intent);
    }

    private void logout(){
        NetworkManager.getInstance().logOut();

        if(!NetworkManager.getInstance().isAuthenticated()){
            Toast.makeText(getApplicationContext(), "Log out successful", Toast.LENGTH_SHORT).show();
            userViewModel.getAllUsers().subscribe(roomUsers -> {
                RoomUser[] users = new RoomUser[roomUsers.size()];
                for (int i = 0; i < roomUsers.size(); i++) {
                    users[i] = roomUsers.get(i);
                }
                userViewModel.deleteUsers(users).subscribe(integer ->{
                    Toast.makeText(this, "Local user deleted", Toast.LENGTH_SHORT).show();
                });
            });
        }
    }

    @Override
    protected void onResume() {
        if(getIntent().getBooleanExtra("Logout", false)){
            logout();
        }
        super.onResume();
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
                logout();
                break;
        }

        supportInvalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    private boolean requestAccess(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_ACCESS_FINE_LOCATION);
            return true;
        }
        else{
            return false;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[],int[] grantResults){
        switch (requestCode){
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION:
                if((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(getApplicationContext(), "Access granted to TRing", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Access denied",Toast.LENGTH_SHORT).show();
                }
        }
    }
}
