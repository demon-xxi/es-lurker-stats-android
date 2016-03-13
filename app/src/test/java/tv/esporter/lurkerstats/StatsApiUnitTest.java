package tv.esporter.lurkerstats;

import com.squareup.moshi.Moshi;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import tv.esporter.lurkerstats.api.ApiHelper;
import tv.esporter.lurkerstats.api.ChannelStat;
import tv.esporter.lurkerstats.api.GameStat;
import tv.esporter.lurkerstats.api.StatsApi;
import tv.esporter.lurkerstats.api.TwitchApi;
import tv.esporter.lurkerstats.api.TwitchChannel;
import tv.esporter.lurkerstats.service.StatsItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class StatsApiUnitTest {
    @Test
    public void channelsStats_lirik_works() throws Exception {

        StatsApi api = ApiHelper.getStatsApi();

        Call<List<ChannelStat>> stats = api.channelsStats("lirik", "lastmonth");
        Response<List<ChannelStat>> resp = stats.execute();

        assertEquals(200, resp.code());
        assertFalse(resp.body().isEmpty());

    }

    @Test
    public void gamesStats_lirik_works() throws Exception {

        StatsApi api = ApiHelper.getStatsApi();

        Call<List<GameStat>> stats = api.gamesStats("lirik", "lastmonth");
        Response<List<GameStat>> resp = stats.execute();

        assertEquals(200, resp.code());
        assertFalse(resp.body().isEmpty());

    }

    @Test
    public void channelsStats_lirik_details() throws Exception {

        Moshi moshi = new Moshi.Builder().build();

        StatsApi api = ApiHelper.getStatsApi();
        TwitchApi tapi = ApiHelper.getTwitchApi();

//        Scheduler scheduler = new TestScheduler();

        Observable.merge(
        api.channelsStatsRx("demon_xxi", "lastmonth")
                .flatMap(Observable::from)
                .observeOn(Schedulers.newThread())
//                .observeOn(Schedulers.io())
//                .subscribeOn(scheduler)
                .map(chan ->
                    tapi.channelRx(chan.channel).map(tc -> new StatsItem(StatsItem.Type.CHANNEL,
                                chan.channel, tc.display_name, tc.logo, chan.duration, "")).single()
                ))
                .map(result -> moshi.adapter(StatsItem.class).toJson(result))
                .toBlocking()
                .subscribe(
                result -> System.out.println(result),
                e -> System.out.println(e.getMessage())
        );


    }
}
