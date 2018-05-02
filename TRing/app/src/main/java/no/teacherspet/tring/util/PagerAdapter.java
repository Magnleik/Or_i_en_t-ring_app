package no.teacherspet.tring.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import no.teacherspet.tring.fragments.MyEvents;
import no.teacherspet.tring.fragments.NearbyEvents;

/**
 * Created by petterbjorkaas on 06/03/2018.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int numberOfTabs;

    public PagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MyEvents myEvents = new MyEvents();
                return myEvents;
            case 1:
                NearbyEvents nearbyEvents = new NearbyEvents();
                return nearbyEvents;
            default:
                return null;


        }

    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
