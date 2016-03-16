package tv.esporter.lurkerstats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.snappydb.SnappyDB;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.supercharge.rxsnappy.RxSnappyClient;
import io.supercharge.rxsnappy.exception.CacheExpiredException;
import io.supercharge.rxsnappy.exception.MissingDataException;
import tv.esporter.lurkerstats.api.TwitchChannel;
import tv.esporter.lurkerstats.service.DataServiceHelper;
import tv.esporter.lurkerstats.service.StatsItem;
import tv.esporter.lurkerstats.util.Build;

public class ViewerActivity extends AppCompatActivity {

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

    private static final String EXTRA_USERNAME = "tv.esporter.lurkerstats.extra.USERNAME";


//    private StatsListFragment mGamesFragment;
//    private StatsListFragment mChannelsFragment;
//    private DataServiceHelper mDataServiceHelper;
    private ActionBar ab;
    private String mUserName;

//    Cache<TwitchChannel> channelCache;
//    Cache<ArrayList<StatsItem>> statsCache;

    private static final String PERIOD  = "currentmonth";
    private RxSnappyClient cache;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.v(">>>> ViewerActivity", "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getIntent().hasExtra(EXTRA_USERNAME)){
            mUserName = getIntent().getStringExtra(EXTRA_USERNAME);
        } else {
            mUserName = getString(R.string.default_username);
        }

        ab = getSupportActionBar();
        if (ab != null){
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(getIntent().hasExtra(EXTRA_USERNAME));
            ab.setTitle(mUserName);
        }

//        mDataServiceHelper = new DataServiceHelper(new Handler(), this);

        Bundle channelsBundle = new Bundle();
        channelsBundle.putSerializable(DataServiceHelper.EXTRA_STATS_TYPE, StatsItem.Type.CHANNEL);
        Bundle gamesBundle = new Bundle();
        gamesBundle.putSerializable(DataServiceHelper.EXTRA_STATS_TYPE, StatsItem.Type.GAME);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mFragmentPagerAdapter = new StaticFragmentPagerAdapter(getSupportFragmentManager(),
                this,
                new Class[]{StatsListFragment.class, StatsListFragment.class},
                new String[]{"Channels", "Games"}, new Bundle[]{channelsBundle, gamesBundle});


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mFragmentPagerAdapter);

        // hack way to get references to fragments
//        mChannelsFragment = (StatsListFragment) mFragmentPagerAdapter.instantiateItem(mViewPager, 0);
//        mGamesFragment = (StatsListFragment) mFragmentPagerAdapter.instantiateItem(mViewPager, 1);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        try {
            cache = new RxSnappyClient(SnappyDB.with(getApplicationContext()));
//            channelCache = new Cache<>(getApplicationContext(), TwitchChannel.class);
//            statsCache = new Cache<>(getApplicationContext(), StatsItem[].class);
        } catch (SnappydbException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("ViewerActivity", "Error during cache initialization", e);
            finish();
        }

        IntentFilter filter = new IntentFilter(DataServiceHelper.EVENT_PROFILE_UPDATED);
        filter.addAction(DataServiceHelper.EVENT_STATS_UPDATED);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, filter);

    }


    @Override
    protected void onStart() {
        super.onStart();

        loadUserProfile();
        loadStats(StatsItem.Type.CHANNEL, PERIOD);
        loadStats(StatsItem.Type.GAME, PERIOD);
    }

    // Our handler for received Intents.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()){
                case DataServiceHelper.EVENT_PROFILE_UPDATED:
                    if (!intent.getStringExtra(DataServiceHelper.EXTRA_USERNAME).equals(mUserName)) return;
                    loadUserProfile();
                    break;
                case DataServiceHelper.EVENT_STATS_UPDATED:
                    if (!intent.getStringExtra(DataServiceHelper.EXTRA_USERNAME).equals(mUserName)) return;
                    if (!intent.getStringExtra(DataServiceHelper.EXTRA_PERIOD).equals(PERIOD)) return;
                    loadStats((StatsItem.Type) intent.getSerializableExtra(DataServiceHelper.EXTRA_STATS_TYPE),
                            PERIOD);
                    break;
            }


            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
        }
    };

    private void loadStats(StatsItem.Type type, String period) {
        String key = Build.key(mUserName, type, period);

        try {
            // try getting fresh data first
        ArrayList<StatsItem> stats =
                cache.getObject(Build.key(StatsItem.class.getSimpleName(), key),
                        DataServiceHelper.SHORT_TTL,
                new ArrayList<StatsItem>().getClass()).toBlocking().first();
            setStats(stats, type);
            return;
        } catch (CacheExpiredException | MissingDataException e) {
            // get any data
            try{
                ArrayList<StatsItem> stats =
                        cache.getObject(Build.key(StatsItem.class.getSimpleName(), key),
                                new ArrayList<StatsItem>().getClass()).toBlocking().first();
                setStats(stats, type);
            } catch (MissingDataException e2){
                // ignoring
            }
        }

        // request fresh data
        DataServiceHelper.startActionFetchUserStats(this, mUserName, period, type);
    }

    private void setStats(List<StatsItem> statsItems, StatsItem.Type type) {
        StatsListFragment sub = dataSubscribers.get(type);
        if (sub != null) sub.setData(statsItems);
    }

    private void loadUserProfile() {
        try {
            TwitchChannel profile = cache.getObject(Build.key(TwitchChannel.class.getSimpleName(), mUserName),
                    DataServiceHelper.LONG_TTL,
                    TwitchChannel.class).toBlocking().first();

            setProfile(profile);
            return;
        } catch (CacheExpiredException | MissingDataException e1) {
            try {
                TwitchChannel profile = cache.getObject(Build.key(TwitchChannel.class.getSimpleName(), mUserName),
                        TwitchChannel.class).toBlocking().first();
                setProfile(profile);
            } catch (MissingDataException e2){
                // ignoring
            }
        }
        // request profile update
        DataServiceHelper.startActionFetchUserProfile(this, mUserName);
    }

    private void setProfile(TwitchChannel channel) {
        if (ab != null) ab.setTitle(channel.display_name);
    }


//    @Override
//    protected void onPause() {
//        // Unregister since the activity is paused.
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(
//                mMessageReceiver);
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        // Register to receive messages.
//        // We are registering an observer (mMessageReceiver) to receive Intents
//        // with actions named "custom-event-name".
//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                mMessageReceiver, new IntentFilter("custom-event-name"));
//        super.onResume();
//    }

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

        switch (item.getItemId()){
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onListFragmentInteraction(StatsItem item) {

        switch (item.type) {
            case GAME:
                break;
            case CHANNEL:
                Intent intent = new Intent(this, ViewerActivity.class);
                intent.putExtra(EXTRA_USERNAME, item.name);
                startActivity(intent);
                break;
        }

    }

//    @Override
//    public void onReceiveUserStatsResult(String username, StatsItem.Type type,
//                                         String period, List<StatsItem> stats) {
//        Log.i("UserStatsResult", username);
//        switch (type) {
//            case GAME:
//                mGamesFragment.setData(stats);
//                break;
//            case CHANNEL:
//                mChannelsFragment.setData(stats);
//                break;
//        }
//    }


    private Map<StatsItem.Type, StatsListFragment> dataSubscribers = new HashMap<>();
    public void subscibeForData(StatsItem.Type type, StatsListFragment statsListFragment) {
        dataSubscribers.put(type, statsListFragment);
    }

    public void unSubscibeForData(StatsListFragment statsListFragment) {
        for (Map.Entry e: dataSubscribers.entrySet()){
            if (e.getValue() != statsListFragment) continue;
            dataSubscribers.remove(e.getKey());
            break;
        }
    }

//    @Override
//    public void onReceiveUserProfileResult(String usernamee) {
//        Log.d("ProfileResult", username);
////        mToolbar.setTitle(profile.name);
////        mToolbar.setLogo();
////        mToolbar.setSubtitle(profile.name);
//    }

}
