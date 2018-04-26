package no.teacherspet.tring.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import connection.Event;
import connection.ICallbackAdapter;
import connection.NetworkManager;
import io.reactivex.disposables.Disposable;
import no.teacherspet.tring.Database.Entities.RoomUser;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.UserViewModel;
import no.teacherspet.tring.R;


/**
 * Created by magnus on 13.02.2018.
 */

public class StartupMenu extends AppCompatActivity{

    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION=1;
    private static HashMap<Integer, Event> testEvents;
    LocalDatabase localDatabase;
    UserViewModel userViewModel;
    Disposable user;

    protected void onCreate(Bundle savedInstanceState) {
        requestAccess();

        localDatabase = LocalDatabase.getInstance(this);
        userViewModel = new UserViewModel(localDatabase.userDAO());

        //Checks if we should start createUserActivity
        if(NetworkManager.getInstance().isAuthenticated()){
            user = userViewModel.getAllUsers().subscribe(users -> checkUser(users));
        }

        super.onCreate(savedInstanceState);
        if(testEvents==null){
            testEvents=new HashMap<>();
        }
        setContentView(R.layout.activity_startupmenu);
    }

    //Changes to createUserActivity if a roomUser has not been created
    private void checkUser(List<RoomUser> roomUser){
        if(roomUser.size() > 0){
            NetworkManager.getInstance().logInWithToken(roomUser.get(0).getToken(), new ICallbackAdapter<Boolean>() {
                @Override
                public void onResponse(Boolean object) {
                    if(object != null){
                        if(object) {
                            Toast.makeText(StartupMenu.this, "Logged in", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(StartupMenu.this, OrientationSelector.class));
                        }
                        else{
                            userViewModel.deleteUsers(roomUser.get(0)).subscribe(integers ->
                                    startActivity(new Intent(StartupMenu.this, LogInActivity.class)));
                        }
                    }
                    else{
                        userViewModel.deleteUsers(roomUser.get(0)).subscribe(integers ->
                                    startActivity(new Intent(StartupMenu.this, LogInActivity.class)));
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    userViewModel.deleteUsers(roomUser.get(0)).subscribe(integers ->
                            startActivity(new Intent(StartupMenu.this, LogInActivity.class)));
                }
            });
        }
        else{
            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show();
        }
    }


    public void orientering(View v){
        Intent intent = new Intent(StartupMenu.this,OrientationSelector.class);
        startActivity(intent);
    }

    public void createUserButton(View v){
        Intent intent = new Intent(StartupMenu.this,CreateUserActivity.class);
        startActivity(intent);
    }

    public void logInButton(View v){
        Intent intent = new Intent(StartupMenu.this,LogInActivity.class);
        startActivity(intent);
    }

    public void logOutButton(View v){
        NetworkManager.getInstance().logOut();
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

    public static void addEvent(Event event){
        testEvents.put(testEvents.size(),event);
    }
    public static HashMap<Integer,Event> getTestEvents(){

        if (testEvents==null) {
            return new HashMap<>();
        }
        return testEvents;
    }
}
