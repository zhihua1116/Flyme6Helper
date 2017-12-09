package com.coderstory.FTool.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by cc on 2016/10/9.
 */

public class TabFragmentAdapter extends FragmentPagerAdapter {

    FragmentManager fm;
    private Bundle args;
    private List<Fragment> fragments;

    public TabFragmentAdapter(FragmentManager fm, List<Fragment> fragments, Bundle args) {
        super(fm);
        this.fragments = fragments;
        this.args = args;
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragments.get(position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}
