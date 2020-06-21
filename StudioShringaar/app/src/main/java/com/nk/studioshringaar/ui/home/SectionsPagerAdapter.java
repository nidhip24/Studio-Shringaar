package com.nk.studioshringaar.ui.home;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static int[] TAB_TITLES;
    private String[] title;
    private final Context mContext;

    private int count;

    public SectionsPagerAdapter(Context context, FragmentManager fm, int c, int[] titles) {
        super(fm);
        mContext = context;
        count = c;
        TAB_TITLES = titles;
    }

    public SectionsPagerAdapter(Context context, FragmentManager fm, int c, String[] ti) {
        super(fm);
        mContext = context;
        count = c;
        title = ti;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
//        String temp = mContext.getResources().getString(TAB_TITLES[position])+" a "+position;
//        return PlaceholderFragment.newInstance(position + 1, TAB_TITLES,mContext);
//        TabContent c =new TabContent(position,TAB_TITLES,mContext);
        return TabContent.newInstance(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return count;
    }
}