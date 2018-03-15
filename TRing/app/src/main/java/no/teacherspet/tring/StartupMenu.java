package no.teacherspet.tring;

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

import connection.Event;
import io.reactivex.disposables.Disposable;
import no.teacherspet.tring.Database.Entities.RoomUser;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.UserViewModel;


/**
 * Created by magnus on 13.02.2018.
 */

public class StartupMenu extends AppCompatActivity{

    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION=1;
    private static HashMap<Integer, Event> testEvents;
    LocalDatabase localDatabase;
    Disposable user;

    protected void onCreate(Bundle savedInstanceState) {
        requestAccess();

        //Starting create user activity
        localDatabase = LocalDatabase.getInstance(this);
        UserViewModel userViewModel = new UserViewModel(localDatabase.userDAO());

        user = userViewModel.getPersonalUser()
                .defaultIfEmpty(new RoomUser(-1,false, "", ""))
                .subscribe(user1 -> createUser(user1));

        super.onCreate(savedInstanceState);
        if(testEvents==null){
            testEvents=new HashMap<>();
        }
        setContentView(R.layout.activity_startupmenu);
    }

    //Changes to createUserActivity if a roomUser has not been created
    private void createUser(RoomUser roomUser){
        if(roomUser.getId() < 0){
            startActivity(new Intent(this, CreateUserActivity.class));
        }
        else{
            this.user.dispose();
        }
    }


    public void orientering(View v){
        Intent intent = new Intent(StartupMenu.this,OrientationSelector.class);
        startActivity(intent);
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
