package com.mobile.cupboardmanager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Lawrence on 30/11/2014.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = { "Shopping", "Cupboard" };

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new ShoppingFragment();
            case 1:
                // Games fragment activity
                return new CupboardFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}
