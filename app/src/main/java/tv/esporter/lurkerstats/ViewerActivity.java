package tv.esporter.lurkerstats;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import tv.esporter.lurkerstats.service.DataServiceHelper;
import tv.esporter.lurkerstats.service.StatsItem;
import tv.esporter.lurkerstats.service.UserProfile;

public class ViewerActivity extends AppCompatActivity
        implements OnStatsItemListFragmentInteractionListener, DataServiceHelper.Interface {

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


    private StatsListFragment mGamesFragment;
    private StatsListFragment mChannelsFragment;
    private DataServiceHelper mDataServiceHelper;
    private Toolbar mToolbar;

    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (getIntent().hasExtra(EXTRA_USERNAME)){
            mUserName = getIntent().getStringExtra(EXTRA_USERNAME);
        } else {
            mUserName = getString(R.string.default_username);
        }


        final ActionBar ab = getSupportActionBar();
        if (ab != null){
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(getIntent().hasExtra(EXTRA_USERNAME));
            ab.setTitle(mUserName);
        }

        mToolbar.setTitle(mUserName);


        mDataServiceHelper = new DataServiceHelper(new Handler(), this);

        mChannelsFragment = new StatsListFragment();
        mGamesFragment = new StatsListFragment();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mFragmentPagerAdapter = new StaticFragmentPagerAdapter(getSupportFragmentManager(),
                new StatsListFragment[]{mChannelsFragment, mGamesFragment},
                new String[]{"Channels", "Games"});


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mFragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show());

        mDataServiceHelper.startActionFetchUserProfile(this,
                mUserName);

        mDataServiceHelper.startActionFetchUserStats(this,
                mUserName, "currentmonth", StatsItem.Type.GAME);

        mDataServiceHelper.startActionFetchUserStats(this,
                mUserName, "currentmonth", StatsItem.Type.CHANNEL);
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

    @Override
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
        Log.d("ProfileResult", username);
//        mToolbar.setTitle(profile.name);
//        mToolbar.setLogo();
//        mToolbar.setSubtitle(profile.name);
    }

}
