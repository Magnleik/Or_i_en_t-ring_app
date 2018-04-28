package no.teacherspet.tring.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private Button logInButton;
    private Event activeEvent;
    private GeneralProgressDialog progressDialog;
    private UserViewModel userViewModel;
    private AlertDFragment alertFragment;

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
        logInButton = (Button) findViewById(R.id.selector_log_in_btn);
        alertFragment = new AlertDFragment();

        if(NetworkManager.getInstance().isAuthenticated()){
            logInButton.setEnabled(false);
        }

        if(!NetworkManager.getInstance().isAuthenticated()){
            userViewModel.getAllUsers().subscribe(users -> {
                Log.d("Room","Started checking users");
                checkUser(users);
            });
        }
    }
    private void continueEvent(){
        Intent intent = new Intent(OrientationSelector.this, PerformOEvent.class);
        intent.putExtra("MyEvent", activeEvent);
        startActivity(intent);
    }

    private void checkActiveEvent(List<RoomOEvent> activeEvents){
        Log.d("Room",String.format("%d active events found", activeEvents.size()));
        if(activeEvents.size() > 0){
            RoomOEvent roomEvent = activeEvents.get(0);
            Event event = new Event();
            event._setId(roomEvent.getId());
            for(String key : roomEvent.getProperties().keySet()){
                event.addProperty(key, roomEvent.getProperties().get(key));
            }
            joinViewModel.getPointsForOEvent(event.getId()).subscribe(roomPoints -> {
                Log.d("Room",String.format("%d points found", roomPoints.size()));
                if(roomPoints.size() > 0){
                    joinViewModel.getJoinsForOEvent(event.getId()).subscribe(joins -> addPointsToEvent(event, roomPoints, joins));
                }
            });
        }
        Toast.makeText(this, String.format(getString(R.string.active_events_formatted), activeEvents.size()), Toast.LENGTH_SHORT).show();
    }
    private void addPointsToEvent(Event event, List<RoomPoint> roomPoints, List<PointOEventJoin> joins){
        for (RoomPoint roomPoint : roomPoints){
            for (PointOEventJoin join : joins){
                if(roomPoint.getId() == join.getPointID()){
                    Point point = setupPoint(roomPoint, join.isVisited());
                    if(join.isStart()){
                        Log.d("Room",String.format("Start point is point %d", join.getPointID()));
                        event.setStartPoint(point);
                    }
                    else{
                        event.addPost(point);
                    }
                }
            }
        }
        Log.d("Room",String.format("Event %d recreated from Room", event.getId()));
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
        Log.d("Room",String.format("%d users found",roomUsers.size()));
        if (roomUsers.size() > 0) {
            RoomUser[] users = new RoomUser[roomUsers.size()];
            for (int i = 0; i < roomUsers.size(); i++) {
                users[i] = roomUsers.get(i);
            }
            NetworkManager.getInstance().logInWithToken(users[0].getToken(), new ICallbackAdapter<Boolean>() {
                @Override
                public void onResponse(Boolean object) {
                    progressDialog.hide();
                    if (object != null) {
                        if (object) {
                            logInButton.setEnabled(false);
                            Toast.makeText(OrientationSelector.this, R.string.logged_in, Toast.LENGTH_SHORT).show();
                            logInButton.setEnabled(false);
                        } else {
                            userViewModel.deleteUsers(users).subscribe(integers ->{
                                    Log.d("Room",String.format("%d users deleted", users.length));
                                    startActivity(new Intent(OrientationSelector.this, LogInActivity.class));
                                    });
                        }
                    } else {
                        userViewModel.deleteUsers(users).subscribe(integers ->{
                            Log.d("Room",String.format("%d users deleted", users.length));
                            startActivity(new Intent(OrientationSelector.this, LogInActivity.class));
                        });
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    progressDialog.hide();
                    userViewModel.deleteUsers(users).subscribe(integers ->{
                        Log.d("Room",String.format("%d users deleted", users.length));
                        startActivity(new Intent(OrientationSelector.this, LogInActivity.class));
                    });
                }
            });
        } else {
            progressDialog.hide();
            Toast.makeText(this, R.string.no_user_found, Toast.LENGTH_SHORT).show();
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
        if(NetworkManager.getInstance().isAuthenticated()) {
            Intent intent = new Intent(OrientationSelector.this, CreateOEvent.class);
            startActivity(intent);
        }else{
            if(alertFragment.isAdded()){
                alertFragment.dismiss();
            }
            alertFragment.show(getSupportFragmentManager(),"must_log_in");
        }
    }

    public void logInButton(View v){
        Intent intent = new Intent(OrientationSelector.this,LogInActivity.class);
        startActivity(intent);
    }

    private void logout(){
        NetworkManager.getInstance().logOut();

        if(!NetworkManager.getInstance().isAuthenticated()){
            Toast.makeText(getApplicationContext(), R.string.logout_successful, Toast.LENGTH_SHORT).show();
            userViewModel.getAllUsers().subscribe(roomUsers -> {
                RoomUser[] users = new RoomUser[roomUsers.size()];
                for (int i = 0; i < roomUsers.size(); i++) {
                    users[i] = roomUsers.get(i);
                }
                userViewModel.deleteUsers(users).subscribe(integer ->{
                    Log.d("Room",String.format("%d users deleted", users.length));
                    Toast.makeText(this, R.string.local_user_deleted, Toast.LENGTH_SHORT).show();
                });
            });
        }

        logInButton.setEnabled(true);
    }

    @Override
    protected void onResume() {
        logInButton.setEnabled(!NetworkManager.getInstance().isAuthenticated());

        if(getIntent().getBooleanExtra("Logout", false)){
            logout();
        }
        eventViewModel.getActiveEvent().subscribe(roomOEvents -> {
            Log.d("Room","Started checking active events");
            checkActiveEvent(roomOEvents);
        });
        supportInvalidateOptionsMenu();
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
                    Toast.makeText(getApplicationContext(), R.string.access_granted_to_TRing, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), R.string.access_denied,Toast.LENGTH_SHORT).show();
                }
        }
    }

    public static class AlertDFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    // Set Dialog Title
                    .setTitle(R.string.must_log_in)
                    // Set Dialog Message
                    .setMessage(R.string.must_log_in_for_access)

                    // Positive button
                    .setPositiveButton(R.string.log_in, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ((OrientationSelector)getActivity()).logInButton(null);
                        }
                    })

                    // Negative Button
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,	int which) {
                            dismiss();
                        }
                    }).create();
        }
    }
}
