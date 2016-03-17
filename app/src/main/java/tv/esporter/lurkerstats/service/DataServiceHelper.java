package tv.esporter.lurkerstats.service;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;


public class DataServiceHelper {

    public static final long EXTRA_LONG_TTL = 1000 * 3600 * 24 * 10;   // 10 days
    public static final long LONG_TTL = 1000 * 3600 * 24;   // 1 day
    public static final long SHORT_TTL = 1000 * 60 * 5;     // 5 min

    public static final String ACTION_FETCH_USER_PROFILE = "tv.esporter.lurkerstats.action.FETCH_USER_PROFILE";
    public static final String ACTION_FETCH_STATS = "tv.esporter.lurkerstats.action.FETCH_GAMES_STATS";

    public static final String EXTRA_USERNAME = "tv.esporter.lurkerstats.extra.USERNAME";
    public static final String EXTRA_PERIOD = "tv.esporter.lurkerstats.extra.PERIOD";
    public static final String EXTRA_STATS_TYPE = "tv.esporter.lurkerstats.extra.STATS_TYPE";

    public static final String EVENT_STATS_UPDATED = "tv.esporter.lurkerstats.action.EVENT_STATS_UPDATED";
    public static final String EVENT_PROFILE_UPDATED = "tv.esporter.lurkerstats.action.EVENT_PROFILE_UPDATED";


    /**
     * Starts this service to perform ACTION_FETCH_GAMES_STATS or ACTION_FETCH_CHANNELS_STATS action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFetchUserStats(Context context,
                                                 String username, String period,
                                                 StatsItem.Type type) {
        Intent intent = new Intent(context, DataService.class);
        intent.setAction(ACTION_FETCH_STATS);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_PERIOD, period);
        intent.putExtra(EXTRA_STATS_TYPE, type.toString());
        context.startService(intent);
    }

    /**
     * Starts this service to perform ACTION_FETCH_USER_PROFILE action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFetchUserProfile(Context context,
                                                   String username) {
        Intent intent = new Intent(context, DataService.class);
        intent.setAction(ACTION_FETCH_USER_PROFILE);
        intent.putExtra(EXTRA_USERNAME, username);
        context.startService(intent);
    }

    public static String getUserName(Intent intent) {
        return intent.getStringExtra(EXTRA_USERNAME);
    }

    public static String getPeriod(Intent intent) {
        return intent.getStringExtra(EXTRA_PERIOD);
    }

    public static StatsItem.Type getStatsType(Intent intent) {
        return StatsItem.Type.valueOf(intent.getStringExtra(EXTRA_STATS_TYPE));
    }

}

