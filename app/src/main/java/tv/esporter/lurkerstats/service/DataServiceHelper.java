package tv.esporter.lurkerstats.service;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataServiceHelper {

    private final Interface mHandler;
    private final ResultReceiver mReceiver;

    private static final int RESULT_CODE_PROFILE_SUCCESS    = 0x01;
    private static final int RESULT_CODE_PROFILE_FAILURE    = 0x02;
    private static final int RESULT_CODE_STATS_SUCCESS      = 0x11;
    private static final int RESULT_CODE_STATS_FAILURE      = 0x12;

    private static final String ACTION_FETCH_USER_PROFILE = "tv.esporter.lurkerstats.action.FETCH_USER_PROFILE";
    private static final String ACTION_FETCH_STATS = "tv.esporter.lurkerstats.action.FETCH_GAMES_STATS";

    private static final String EXTRA_USERNAME = "tv.esporter.lurkerstats.extra.USERNAME";
    private static final String EXTRA_PERIOD = "tv.esporter.lurkerstats.extra.PERIOD";
    private static final String EXTRA_STATS_TYPE = "tv.esporter.lurkerstats.extra.STATS_TYPE";
    private static final String EXTRA_STATS = "tv.esporter.lurkerstats.extra.STATS";
    private static final String EXTRA_PROFILE = "tv.esporter.lurkerstats.extra.PROFILE";
    private static final String EXTRA_RECEIVER = "tv.esporter.lurkerstats.extra.RECEIVER";

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link DataServiceHelper.Interface} methods will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public DataServiceHelper(Handler handler, Interface receiver) {
        mHandler = receiver;
        mReceiver = new ResultReceiver(handler){
            @Override
            protected void onReceiveResult(int resultCode, Bundle bundle) {
                if (mHandler == null) return;

                switch (resultCode){
                    case RESULT_CODE_PROFILE_SUCCESS:
                        mHandler.onReceiveUserProfileResult(
                                bundle.getString(EXTRA_USERNAME),
                                (UserProfile) bundle.getParcelable(EXTRA_PROFILE)
                        );
                        break;
                    case RESULT_CODE_STATS_SUCCESS:
                        mHandler.onReceiveUserStatsResult(
                                bundle.getString(EXTRA_USERNAME),
                                StatsItem.Type.valueOf(bundle.getString(EXTRA_STATS_TYPE)),
                                bundle.getString(EXTRA_PERIOD),
                                bundle.<StatsItem>getParcelableArrayList(EXTRA_STATS)
                        );
                        break;
                    default:
                        Log.w("DataServiceHelper", "Unhandled resultCode: " + resultCode);
                }
            }
        };
    }

    /**
     * Starts this service to perform ACTION_FETCH_GAMES_STATS or ACTION_FETCH_CHANNELS_STATS action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public void startActionFetchUserStats(Context context,
                                                 String username, String period,
                                                 StatsItem.Type type) {
        Intent intent = new Intent(context, DataService.class);
        intent.setAction(ACTION_FETCH_STATS);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_PERIOD, period);
        intent.putExtra(EXTRA_STATS_TYPE, type.toString());
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        context.startService(intent);
    }

    /**
     * Starts this service to perform ACTION_FETCH_USER_PROFILE action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public void startActionFetchUserProfile(Context context,
                                                   String username) {
        Intent intent = new Intent(context, DataService.class);
        intent.setAction(ACTION_FETCH_USER_PROFILE);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        context.startService(intent);
    }

    public static void replyUserProfileSuccess(ResultReceiver receiver, String username, UserProfile profile){
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_USERNAME, username);
        bundle.putParcelable(EXTRA_PROFILE, profile);
        receiver.send(RESULT_CODE_PROFILE_SUCCESS, bundle);
    }

    public static void replyUserStatsSuccess(ResultReceiver receiver, String username, StatsItem.Type type,
                                      String period, ArrayList<StatsItem> stats){
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_USERNAME, username);
        bundle.putString(EXTRA_STATS_TYPE, type.toString());
        bundle.putString(EXTRA_PERIOD, period);
        bundle.putParcelableArrayList(EXTRA_STATS, stats);

        receiver.send(RESULT_CODE_STATS_SUCCESS, bundle);
    }

    public static ResultReceiver getIntentResultReceiver(Intent intent){
        return intent.getParcelableExtra(EXTRA_RECEIVER);
    }

    public static String getUserName(Intent intent){
        return intent.getStringExtra(EXTRA_USERNAME);
    }

    public static String getPeriod(Intent intent){
        return intent.getStringExtra(EXTRA_PERIOD);
    }

    public static StatsItem.Type getStatsType(Intent intent){
        return StatsItem.Type.valueOf(intent.getStringExtra(EXTRA_STATS_TYPE));
    }

    public interface Interface {
        void onReceiveUserStatsResult(String username, StatsItem.Type type,
                                      String period, List<StatsItem> stats);
        void onReceiveUserProfileResult(String username, UserProfile profile);
    }

}

