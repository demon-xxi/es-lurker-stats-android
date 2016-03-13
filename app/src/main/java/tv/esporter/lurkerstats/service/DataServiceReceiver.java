package tv.esporter.lurkerstats.service;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataServiceReceiver extends ResultReceiver {

    private final Interface mReceiver;

    private static final int RESULT_CODE_PROFILE_SUCCESS    = 0x01;
    private static final int RESULT_CODE_PROFILE_FAILURE    = 0x02;
    private static final int RESULT_CODE_STATS_SUCCESS      = 0x11;
    private static final int RESULT_CODE_STATS_FAILURE      = 0x12;

    private static final String EXTRA_USERNAME = "tv.esporter.lurkerstats.extra.USERNAME";
    private static final String EXTRA_PERIOD = "tv.esporter.lurkerstats.extra.PERIOD";
    private static final String EXTRA_STATS_TYPE = "tv.esporter.lurkerstats.extra.STATS_TYPE";
    private static final String EXTRA_STATS = "tv.esporter.lurkerstats.extra.STATS";
    private static final String EXTRA_PROFILE = "tv.esporter.lurkerstats.extra.PROFILE";

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public DataServiceReceiver(Handler handler, Interface receiver) {
        super(handler);
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle bundle) {
        if (mReceiver  == null) return;

        switch (resultCode){
            case RESULT_CODE_PROFILE_SUCCESS:
                mReceiver.onReceiveUserProfileResult(
                        bundle.getString(EXTRA_USERNAME),
                        (UserProfile) bundle.getParcelable(EXTRA_PROFILE)
                );
                break;
            case RESULT_CODE_STATS_SUCCESS:
                mReceiver.onReceiveUserStatsResult(
                        bundle.getString(EXTRA_USERNAME),
                        StatsItem.Type.valueOf(bundle.getString(EXTRA_STATS_TYPE)),
                        bundle.getString(EXTRA_PERIOD),
                        bundle.<StatsItem>getParcelableArrayList(EXTRA_STATS)
                );
                break;
            default:
                Log.w("DataServiceReceiver", "Unhandled resultCode: " + resultCode);
        }
    }

    public void replyUserProfileSuccess(String username, UserProfile profile){
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_USERNAME, username);
        bundle.putParcelable(EXTRA_PROFILE, profile);
        this.send(RESULT_CODE_PROFILE_SUCCESS, bundle);
    }

    public void replyUserStatsSuccess(String username, StatsItem.Type type,
                                      String period, ArrayList<StatsItem> stats){
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_USERNAME, username);
        bundle.putString(EXTRA_STATS_TYPE, type.toString());
        bundle.putString(EXTRA_PERIOD, period);
        bundle.putParcelableArrayList(EXTRA_STATS, stats);

        this.send(RESULT_CODE_PROFILE_SUCCESS, bundle);
    }

    public interface Interface {
        void onReceiveUserStatsResult(String username, StatsItem.Type type,
                                      String period, List<StatsItem> stats);
        void onReceiveUserProfileResult(String username, UserProfile profile);
    }

}

