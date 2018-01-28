package com.prestonbui.glancesocial.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.prestonbui.glancesocial.fragments.CommunityFragment;
import com.prestonbui.glancesocial.fragments.FeedsFragment;
import com.prestonbui.glancesocial.fragments.ProfileFragment;

/**
 * Created by phuongbui on 10/14/17.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {


    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ProfileFragment.create();
            case 1:
                return FeedsFragment.create();
            case 2:
                return CommunityFragment.create();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
