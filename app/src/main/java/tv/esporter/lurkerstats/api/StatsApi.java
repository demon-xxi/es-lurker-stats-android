package tv.esporter.lurkerstats.api;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface StatsApi {
    @GET("{user}/streams/{period}?limit=50")
    Call<List<ChannelStat>> channelsStats(@Path("user") String user, @Path("period") String period);

    @GET("{user}/games/{period}?limit=50")
    Call<List<GameStat>> gamesStats(@Path("user") String user, @Path("period") String period);

    @GET("{user}/streams/{period}?limit=50")
    Observable<List<ChannelStat>> channelsStatsRx(@Path("user") String user, @Path("period") String period);

    @GET("{user}/games/{period}?limit=50")
    Observable<List<GameStat>> gamesStatsRx(@Path("user") String user, @Path("period") String period);

}

