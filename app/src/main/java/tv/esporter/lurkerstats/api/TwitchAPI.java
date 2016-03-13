package tv.esporter.lurkerstats.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Interface to some of the REST API methods defined
 * in https://github.com/cconger/Twitch-API
 */
public interface TwitchApi {
    @GET("channels/{username}")
    Call<TwitchChannel> channel(@Path("username") String user);

    @GET("streams/{username}")
    Call<StreamContainer> stream(@Path("username") String user);

    @GET("channels/{username}")
    Observable<TwitchChannel> channelRx(@Path("username") String user);

    @GET("streams/{username}")
    Observable<StreamContainer> streamRx(@Path("username") String user);
}
