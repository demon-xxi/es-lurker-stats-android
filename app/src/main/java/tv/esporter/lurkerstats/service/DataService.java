package tv.esporter.lurkerstats.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Random;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class DataService extends IntentService {

    private static final String ACTION_FETCH_USER_PROFILE = "tv.esporter.lurkerstats.action.FETCH_USER_PROFILE";
    private static final String ACTION_FETCH_STATS = "tv.esporter.lurkerstats.action.FETCH_GAMES_STATS";

    private static final String EXTRA_USERNAME = "tv.esporter.lurkerstats.extra.USERNAME";
    private static final String EXTRA_PERIOD = "tv.esporter.lurkerstats.extra.PERIOD";
    private static final String EXTRA_STATS_TYPE = "tv.esporter.lurkerstats.extra.STATS_TYPE";
    private static final String EXTRA_STATS = "tv.esporter.lurkerstats.extra.STATS";
    private static final String EXTRA_PROFILE = "tv.esporter.lurkerstats.extra.PROFILE";
    private static final String EXTRA_RECEIVER = "tv.esporter.lurkerstats.extra.RECEIVER";

    public DataService() {
        super("DataService");
    }

    /**
     * Starts this service to perform ACTION_FETCH_GAMES_STATS or ACTION_FETCH_CHANNELS_STATS action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFetchUserStats(Context context, DataServiceReceiver mDataServiceReceiver,
                                                 String username, String period,
                                                 StatsItem.Type type) {
        Intent intent = new Intent(context, DataService.class);
        intent.setAction(ACTION_FETCH_STATS);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_PERIOD, period);
        intent.putExtra(EXTRA_STATS_TYPE, type.toString());
        intent.putExtra(EXTRA_RECEIVER, mDataServiceReceiver);
        context.startService(intent);
    }

    /**
     * Starts this service to perform ACTION_FETCH_USER_PROFILE action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFetchUserProfile(Context context, DataServiceReceiver mDataServiceReceiver,
                                                   String username) {
        Intent intent = new Intent(context, DataService.class);
        intent.setAction(ACTION_FETCH_USER_PROFILE);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_RECEIVER, mDataServiceReceiver);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            final DataServiceReceiver receiver = intent.getParcelableExtra(EXTRA_RECEIVER);

            if (ACTION_FETCH_USER_PROFILE.equals(action)) {
                handleActionFetchUserProfile(intent.getStringExtra(EXTRA_USERNAME), receiver);
            } else if (ACTION_FETCH_STATS.equals(action)) {
                handleActionFetchUserProfile(intent.getStringExtra(EXTRA_USERNAME),
                        intent.getStringExtra(EXTRA_PERIOD),
                        StatsItem.Type.valueOf(intent.getStringExtra(EXTRA_STATS_TYPE)), receiver);
            }
        }
    }

    /**
     * Handle action ACTION_FETCH_USER_PROFILE
     * in the provided background thread with the provided parameters.
     */
    private void handleActionFetchUserProfile(String username, DataServiceReceiver receiver) {
        UserProfile profile = new UserProfile("demon_xxi",
                Uri.parse("https://static-cdn.jtvnw.net/jtv_user_pictures/demon_xxi-profile_image-6e334affccfca491-300x300.png"));

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        receiver.replyUserProfileSuccess(username, profile);
    }

    /**
     * Handle action ACTION_FETCH_GAMES_STATS or ACTION_FETCH_CHANNELS_STATS
     * in the provided background thread with the provided parameters.
     */
    private void handleActionFetchUserProfile(String username, String period, StatsItem.Type type, DataServiceReceiver receiver) {

        ArrayList<StatsItem> stats = new ArrayList<>();

        Random rand = new Random();
        // Add some sample items.
        for (int i = 1; i <= 25; i++) {
            int hrs = rand.nextInt(100);
            stats.add(new StatsItem(
                    type,
                    String.format("%s %d", type.toString(), i),
                    String.format("%s %d", type.toString(), i),
                    Uri.parse("http://static-cdn.jtvnw.net/ttv-boxart/Stardew%20Valley-272x380.jpg"),
                    hrs,
                    String.format("%d hrs", i)
            ));
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        receiver.replyUserStatsSuccess(username, type, period, stats);
    }
}
