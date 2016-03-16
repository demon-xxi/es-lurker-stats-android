package tv.esporter.lurkerstats.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.snappydb.SnappyDB;
import com.snappydb.SnappydbException;

import java.util.ArrayList;

import io.supercharge.rxsnappy.RxSnappyClient;
import rx.Observable;
import rx.schedulers.Schedulers;
import tv.esporter.lurkerstats.api.ApiHelper;
import tv.esporter.lurkerstats.api.StatsApi;
import tv.esporter.lurkerstats.api.TwitchApi;
import tv.esporter.lurkerstats.api.TwitchChannel;
import tv.esporter.lurkerstats.util.Build;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class DataService extends IntentService {

    private static final String ACTION_FETCH_USER_PROFILE = "tv.esporter.lurkerstats.action.FETCH_USER_PROFILE";
    private static final String ACTION_FETCH_STATS = "tv.esporter.lurkerstats.action.FETCH_GAMES_STATS";

//    Cache<TwitchChannel> channelCache;
//    Cache<ArrayList<StatsItem>> statsCache;

    private RxSnappyClient cache;

    public DataService(){
        super("DataService");
    }

    @Override
    public void onCreate() {
        Log.w("DataService", "onCreate");
        super.onCreate();

        try {
//            channelCache = new Cache<>(getApplicationContext(), TwitchChannel.class);
//            statsCache = new Cache<>(getApplicationContext(), StatsItem[].class);
            cache = new RxSnappyClient(SnappyDB.with(getApplicationContext()));
        } catch (SnappydbException e) {
            // TODO: Handle exceptions and relay error to user ui
            e.printStackTrace();
            Log.e("DataService", "Failed to open database", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        Log.w("DataService", "onDestroy");
        super.onDestroy();
//        channelCache = null;
//        statsCache = null;
        cache = null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Log.w("DataService", "onHandleIntent: " + action);

            if (ACTION_FETCH_USER_PROFILE.equals(action)) {
                handleActionFetchUserProfile(DataServiceHelper.getUserName(intent));
            } else if (ACTION_FETCH_STATS.equals(action)) {
                handleActionFetchStats(DataServiceHelper.getUserName(intent),
                        DataServiceHelper.getPeriod(intent),
                        DataServiceHelper.getStatsType(intent));
            }
        }
    }

    /**
     * Handle action ACTION_FETCH_USER_PROFILE
     * in the provided background thread with the provided parameters.
     */
    private void handleActionFetchUserProfile(String username) {

        TwitchApi twitch = ApiHelper.getTwitchApi();

        twitch.channelRx(username)
                .doOnNext(twitchChannel1 -> {
                    cache.setObject(
                            Build.key(TwitchChannel.class.getSimpleName(),username),
                            twitchChannel1)
                            .toBlocking().first();
                })
                .toBlocking().subscribe(
                twitchChannel -> {
                    Intent intent = Build.intent(DataServiceHelper.EVENT_PROFILE_UPDATED)
                            .extra(DataServiceHelper.EXTRA_USERNAME, username)
                            .build();

                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                },
                // TODO: Emmit error to UI
                Throwable::printStackTrace
        );


    }

    private static final String GAME_BOX_ART_TEMPLATE  = "http://static-cdn.jtvnw.net/ttv-boxart/%s-136x190.jpg";

    /**
     * Handle action ACTION_FETCH_GAMES_STATS or ACTION_FETCH_CHANNELS_STATS
     * in the provided background thread with the provided parameters.
     */
    private void handleActionFetchStats(final String username, final String period, final StatsItem.Type type) {

        StatsApi api = ApiHelper.getStatsApi();
        TwitchApi twitch = ApiHelper.getTwitchApi();
        Observable<StatsItem> statsStream;

        switch (type) {
            case GAME:
                statsStream =  api.gamesStatsRx(username, period)
                        .flatMap(Observable::from)
                        .observeOn(Schedulers.newThread())
//                              .subscribeOn(AndroidSchedulers.mainThread())
                        .map(game ->
                                new StatsItem(StatsItem.Type.GAME,
                                        game.game, game.game, String.format(GAME_BOX_ART_TEMPLATE, game.game), game.duration)
                        );
                break;
            case CHANNEL:
                    statsStream =   Observable.merge(
                            api.channelsStatsRx(username, period)
                                    .flatMap(Observable::from)
                                    .observeOn(Schedulers.newThread())
                                    .map(chan ->

                                        cache.getObject(
                                                Build.key(TwitchChannel.class.getSimpleName(), chan.channel),
                                                DataServiceHelper.EXTRA_LONG_TTL,
                                                TwitchChannel.class
                                        )
                                        .onErrorResumeNext(
                                                twitch.channelRx(chan.channel)
                                                        .observeOn(Schedulers.newThread())
                                                .doOnNext(twitchChannel ->
                                                    cache.setObject(Build.key(
                                                            TwitchChannel.class.getSimpleName(),
                                                            chan.channel
                                                    ), twitchChannel).toBlocking().first()
                                                )
                                        )
                                        .single().map(twitchChannel -> new StatsItem(StatsItem.Type.CHANNEL,
                                                chan.channel, twitchChannel.display_name, twitchChannel.logo, chan.duration))

                                    ));
                break;
            default:
                // ensure statsStream != null
                return;
        }

        statsStream.toList()
                .toBlocking()
                .subscribe(
                        result -> {
                            ArrayList<StatsItem> stats = new ArrayList<>();
                            stats.addAll(result);

                            cache.setObject(Build.key(
                                    StatsItem.class.getSimpleName(),
                                    username, type, period
                            ), stats).toBlocking().first();

                            Intent intent = Build.intent(DataServiceHelper.EVENT_STATS_UPDATED)
                                    .extra(DataServiceHelper.EXTRA_USERNAME, username)
                                    .extra(DataServiceHelper.EXTRA_STATS_TYPE, type)
                                    .extra(DataServiceHelper.EXTRA_PERIOD, period)
                                    .build();

                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                        },
                        e -> {
                            // TODO: Emmit error to UI
                            e.printStackTrace();
                            Log.e("DataService", "handleActionFetchStats", e);
//                                    DataServiceHelper.replyUserStatsSuccess(receiver, username, type, period, stats);
                        }
                );

    }
}
