package tv.esporter.lurkerstats;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaticFragmentPagerAdapter extends FragmentPagerAdapter {


    private Context mContext;
    private Class[] mFragmentTypes;
    private String[] mTitles;
    private Bundle[] mArguments;

    public StaticFragmentPagerAdapter(FragmentManager fm, Context context,
                                      Class[] fragmentTypes, String[] titles, Bundle[] arguments) {

        super(fm);
        mContext = context;
        mFragmentTypes = fragmentTypes;
        mTitles = titles;
        mArguments = arguments;
    }

    @Override
    public Fragment getItem(int position) {
        if (position >= mFragmentTypes.length) return null;
        return Fragment.instantiate(mContext, mFragmentTypes[position].getName(), mArguments[position]);
    }

    @Override
    public int getCount() {
        return mFragmentTypes.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position >= mTitles.length) return null;
        return mTitles[position];
    }
}
