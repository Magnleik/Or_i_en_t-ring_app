package no.teacherspet.tring.util;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import no.teacherspet.tring.R;
import no.teacherspet.tring.fragments.MyEvents;
import no.teacherspet.tring.fragments.NearbyEvents;

/**
 * Created by magnus on 10.04.2018.
 */

public class EventFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 2;
    private static final String[] titles = new String[]{"0","1",}; //Initialize with dummy values for localization.
    private Context context;

    public EventFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

        titles[0] = context.getString(R.string.my_events);
        titles[1] = context.getString(R.string.nearby_events);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return MyEvents.newInstance("","");
        }
        if(position == 1){
            return NearbyEvents.newInstance("","");
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return titles[position];
    }
}
