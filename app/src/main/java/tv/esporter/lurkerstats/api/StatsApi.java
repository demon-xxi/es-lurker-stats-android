package tv.esporter.lurkerstats.api;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface StatsApi {
    @GET("{user}/streams/{period}")
    Call<List<ChannelStat>> channelsStats(@Path("user") String user, @Path("period") String period);

    @GET("{user}/games/{period}")
    Call<List<GameStat>> gamesStats(@Path("user") String user, @Path("period") String period);

    @GET("{user}/streams/{period}")
    Observable<List<ChannelStat>> channelsStatsRx(@Path("user") String user, @Path("period") String period);

    @GET("{user}/games/{period}")
    Observable<List<GameStat>> gamesStatsRx(@Path("user") String user, @Path("period") String period);

}

