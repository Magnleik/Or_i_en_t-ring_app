package no.teacherspet.tring;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by magnus on 10.04.2018.
 */

class EventFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 3;
    private static final String[] titles = new String[] {"My events", "Nearby events", "Popular events"};
    private Context context;

    public EventFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return MyEvents.newInstance("","");
        }
        if(position == 1){
            return NearbyEvents.newInstance("","");
        }
        if(position == 2){
            return MostPopularEvents.newInstance("","");
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
