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
import static org.junit.Assert.assertNotNull;

public class TwitchApiUnitTest {
    @Test
    public void testChannel() throws Exception {

        TwitchApi api = ApiHelper.getTwitchApi((String) null);

        Call<TwitchChannel> call = api.channel("test_channel");
        Response<TwitchChannel> resp = call.execute();

        assertEquals(200, resp.code());
        assertEquals("Test_channel", resp.body().display_name);

    }

    @Test
    public void testStream() throws Exception {

        TwitchApi api = ApiHelper.getTwitchApi((String) null);

        Call<StreamContainer> call = api.stream("test_channel");
        Response<StreamContainer> resp = call.execute();

        assertEquals(200, resp.code());
        assertNotNull(resp.body());

    }

    @Test
    public void testGameStat() throws Exception {

        GameStat game =  new GameStat();
        game.game = "Some Complicated Game: +%";

        assertEquals("Some+Complicated+Game%3A+%2B%25", game.getSafeName());
    }

}
