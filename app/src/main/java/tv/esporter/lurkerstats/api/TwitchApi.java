package tv.esporter.lurkerstats.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;
import tv.esporter.lurkerstats.service.TokenResponse;

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

    @FormUrlEncoded
    @POST("oauth2/token")
    Observable<TokenResponse> getTokenRx(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("grant_type") String grant_type,
            @Field("redirect_uri") String redirect_uri,
            @Field("code") String code,
            @Field("state") String state
            );


    @FormUrlEncoded
    @POST("oauth2/token")
    Call<TokenResponse> getToken(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("grant_type") String grant_type,
            @Field("redirect_uri") String redirect_uri,
            @Field("code") String code,
            @Field("state") String state
    );
}
