package tv.esporter.lurkerstats;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaticFragmentPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> mFragments = new ArrayList<>();
    ArrayList<String> mTitles = new ArrayList<>();

    public StaticFragmentPagerAdapter(FragmentManager fm,
                                      Fragment[] fragments, String[] titles) {
        this(fm, Arrays.asList(fragments), Arrays.asList(titles));
    }


    public StaticFragmentPagerAdapter(FragmentManager fm,
                                      List<Fragment> fragments, List<String> titles) {
        super(fm);
        mFragments.addAll(fragments);
        mTitles.addAll(titles);
    }

    @Override
    public Fragment getItem(int position) {
        if (position >= mFragments.size()) return null;
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position >= mTitles.size()) return null;
        return mTitles.get(position);
    }
}
