package tv.esporter.lurkerstats;

import org.junit.Test;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import tv.esporter.lurkerstats.api.ApiHelper;
import tv.esporter.lurkerstats.api.ChannelStat;
import tv.esporter.lurkerstats.api.GameStat;
import tv.esporter.lurkerstats.api.StatsApi;
import tv.esporter.lurkerstats.api.StreamContainer;
import tv.esporter.lurkerstats.api.TwitchApi;
import tv.esporter.lurkerstats.api.TwitchChannel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TwitchApiUnitTest {
    @Test
    public void channel_lirik() throws Exception {

        TwitchApi api = ApiHelper.getTwitchApi();

        Call<TwitchChannel> call = api.channel("lirik");
        Response<TwitchChannel> resp = call.execute();

        assertEquals(200, resp.code());
        assertEquals("LIRIK", resp.body().display_name);

    }

    @Test
    public void stream_lirik() throws Exception {

        TwitchApi api = ApiHelper.getTwitchApi();

        Call<StreamContainer> call = api.stream("lirik");
        Response<StreamContainer> resp = call.execute();

        assertEquals(200, resp.code());
        assertEquals(20180521472L, resp.body().stream._id);

    }

}
