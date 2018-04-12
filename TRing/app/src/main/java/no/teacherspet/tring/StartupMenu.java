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
import connection.NetworkManager;


/**
 * Created by magnus on 13.02.2018.
 */

public class StartupMenu extends AppCompatActivity{

    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION=1;
    private static HashMap<Integer, Event> testEvents;

    protected void onCreate(Bundle savedInstanceState) {
        requestAccess();
        super.onCreate(savedInstanceState);
        if(testEvents==null){
            testEvents=new HashMap<>();
        }
        setContentView(R.layout.activity_startupmenu);
    }


    public void orientering(View v){
        Intent intent = new Intent(StartupMenu.this,OrientationSelector.class);
        startActivity(intent);
    }

    public void createUser(View v){
        Intent intent = new Intent(StartupMenu.this,CreateUserActivity.class);
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
