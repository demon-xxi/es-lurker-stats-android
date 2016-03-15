package tv.esporter.lurkerstats.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.snappydb.SnappydbException;

import java.util.ArrayList;

import rx.Observable;
import rx.schedulers.Schedulers;
import tv.esporter.lurkerstats.api.ApiHelper;
import tv.esporter.lurkerstats.api.StatsApi;
import tv.esporter.lurkerstats.api.TwitchApi;
import tv.esporter.lurkerstats.api.TwitchChannel;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class DataService extends IntentService {

    private static final String ACTION_FETCH_USER_PROFILE = "tv.esporter.lurkerstats.action.FETCH_USER_PROFILE";
    private static final String ACTION_FETCH_STATS = "tv.esporter.lurkerstats.action.FETCH_GAMES_STATS";

    public DataService() {
        super("DataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();

            final ResultReceiver receiver = DataServiceHelper.getIntentResultReceiver(intent);

            if (ACTION_FETCH_USER_PROFILE.equals(action)) {
                handleActionFetchUserProfile(DataServiceHelper.getUserName(intent), receiver);
            } else if (ACTION_FETCH_STATS.equals(action)) {
                handleActionFetchStats(DataServiceHelper.getUserName(intent),
                        DataServiceHelper.getPeriod(intent),
                        DataServiceHelper.getStatsType(intent), receiver);
            }
        }
    }

    /**
     * Handle action ACTION_FETCH_USER_PROFILE
     * in the provided background thread with the provided parameters.
     */
    private void handleActionFetchUserProfile(String username, ResultReceiver receiver) {

        TwitchApi twitch = ApiHelper.getTwitchApi();

        UserProfile profile = new UserProfile(username,
                Uri.parse("https://static-cdn.jtvnw.net/jtv_user_pictures/demon_xxi-profile_image-6e334affccfca491-300x300.png"));

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DataServiceHelper.replyUserProfileSuccess(receiver, username, profile);
    }

    private static final String GAME_BOX_ART_TEMPLATE  = "http://static-cdn.jtvnw.net/ttv-boxart/%s-136x190.jpg";

    /**
     * Handle action ACTION_FETCH_GAMES_STATS or ACTION_FETCH_CHANNELS_STATS
     * in the provided background thread with the provided parameters.
     */
    private void handleActionFetchStats(final String username, final String period, final StatsItem.Type type, final ResultReceiver receiver) {


        StatsApi api = ApiHelper.getStatsApi();
        TwitchApi twitch = ApiHelper.getTwitchApi();

        switch (type) {
            case GAME:

                api.gamesStatsRx(username, period)
                        .flatMap(Observable::from)
                        .observeOn(Schedulers.newThread())
//                              .subscribeOn(AndroidSchedulers.mainThread())
                        .map(game ->
                                new StatsItem(StatsItem.Type.GAME,
                                        game.game, game.game, String.format(GAME_BOX_ART_TEMPLATE, game.game), game.duration)
                        )
                        .toList()
                        .subscribe(
                                result -> {
                                    ArrayList<StatsItem> stats = new ArrayList<>();
                                    stats.addAll(result);
                                    DataServiceHelper.replyUserStatsSuccess(receiver, username, type, period, stats);
                                },
                                e -> {
                                    Log.e("DataService", e.getMessage());
//                                    DataServiceHelper.replyUserStatsSuccess(receiver, username, type, period, stats);
                                }
                        );

                break;
            case CHANNEL:

                try {
                    Cache<TwitchChannel> channelCache = new Cache<>(getCacheDir().getAbsolutePath(), TwitchChannel.class);

                    Observable.merge(
                            api.channelsStatsRx(username, period)
                                    .flatMap(Observable::from)
                                    .observeOn(Schedulers.newThread())
    //                              .subscribeOn(AndroidSchedulers.mainThread())
                                    .map(chan -> {
                                            TwitchChannel stream = null;
                                            try {
                                                stream = channelCache.get(chan.channel);
                                            } catch (SnappydbException e) {
//                                                e.printStackTrace();
                                            }

                                        if (stream == null){
//                                            Log.v("DataService", "FETCHING: " + chan.channel);
                                            return  twitch.channelRx(chan.channel).map(tc -> {
                                                try {
                                                    channelCache.put(chan.channel, tc);
                                                } catch (SnappydbException e) {
                                                    e.printStackTrace();
                                                }
                                                return new StatsItem(StatsItem.Type.CHANNEL,
                                                        chan.channel, tc.display_name, tc.logo, chan.duration);
                                            }).single();
                                        } else {
//                                            Log.v("DataService", "CACHED: " + stream.name);
                                            return  Observable.just(stream).map(tc -> new StatsItem(StatsItem.Type.CHANNEL,
                                                    chan.channel, tc.display_name, tc.logo, chan.duration)).single();
                                        }
                                    }
                                    ))
                            .toList()
                            .subscribe(
                                    result -> {
                                        ArrayList<StatsItem> stats = new ArrayList<>();
                                        stats.addAll(result);
                                        DataServiceHelper.replyUserStatsSuccess(receiver, username, type, period, stats);
                                    },
                                    e -> {
                                        e.printStackTrace();
                                        Log.e("DataService", e.getMessage());
    //                                    DataServiceHelper.replyUserStatsSuccess(receiver, username, type, period, stats);
                                    }
                            );

                } catch (SnappydbException e) {
                    e.printStackTrace();
                    Log.e("DataService", e.getMessage());
                }

                break;
        }

    }
}
