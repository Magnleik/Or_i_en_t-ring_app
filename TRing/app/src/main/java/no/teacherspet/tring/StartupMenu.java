package no.teacherspet.tring;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


/**
 * Created by magnus on 13.02.2018.
 */

public class StartupMenu extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startupmenu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    public void orientering(View v){
        Intent intent = new Intent(StartupMenu.this,OrientationSelector.class);
        startActivity(intent);
    }
}
