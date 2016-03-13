package tv.esporter.lurkerstats;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import tv.esporter.lurkerstats.service.DataService;
import tv.esporter.lurkerstats.service.DataServiceReceiver;
import tv.esporter.lurkerstats.service.StatsItem;
import tv.esporter.lurkerstats.service.UserProfile;

public class ViewerActivity extends AppCompatActivity
        implements OnStatsItemListFragmentInteractionListener, DataServiceReceiver.Interface {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private FragmentPagerAdapter mFragmentPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private StatsItemListFragment mGamesFragment;
    private StatsItemListFragment mChannelsFragment;
    private DataServiceReceiver mDataServiceReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setLogo(R.drawable.lirik_profile_image);

        mDataServiceReceiver = new DataServiceReceiver(new Handler(), this);

        mChannelsFragment = new StatsItemListFragment();
        mGamesFragment = new StatsItemListFragment();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mFragmentPagerAdapter = new StaticFragmentPagerAdapter(getSupportFragmentManager(),
                new StatsItemListFragment[]{mChannelsFragment, mGamesFragment},
                new String[] {"Channels", "Games"});


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mFragmentPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        DataService.startActionFetchUserStats(this, mDataServiceReceiver,
                "demon_xxi", "currentmonth", StatsItem.Type.GAME);

        DataService.startActionFetchUserStats(this, mDataServiceReceiver,
                "demon_xxi", "currentmonth", StatsItem.Type.CHANNEL);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(StatsItem item) {
        Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceiveUserStatsResult(String username, StatsItem.Type type,
                                         String period, List<StatsItem> stats) {
        Log.i("UserStatsResult", username);
        switch (type) {
            case GAME:
                mGamesFragment.setData(stats);
                break;
            case CHANNEL:
                mChannelsFragment.setData(stats);
                break;
        }
    }

    @Override
    public void onReceiveUserProfileResult(String username, UserProfile profile) {
        Log.i("ProfileResult", username);
    }

}
