package no.teacherspet.tring.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import connection.NetworkManager;
import no.teacherspet.tring.R;
import no.teacherspet.tring.fragments.MostPopularEvents;
import no.teacherspet.tring.fragments.MyEvents;
import no.teacherspet.tring.fragments.NearbyEvents;
import no.teacherspet.tring.util.EventFragmentPagerAdapter;
import no.teacherspet.tring.util.PagerAdapter;

public class ListOfSavedEvents extends AppCompatActivity implements MyEvents.OnFragmentInteractionListener, NearbyEvents.OnFragmentInteractionListener, MostPopularEvents.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private PagerAdapter mPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_saved_events);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), 3);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(new EventFragmentPagerAdapter(getSupportFragmentManager(),
                ListOfSavedEvents.this));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.bringToFront();
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onResume() {
        supportInvalidateOptionsMenu();
        super.onResume();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
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
                Intent intent = new Intent(ListOfSavedEvents.this, OrientationSelector.class);
                intent.putExtra("Logout", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
        }

        supportInvalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}