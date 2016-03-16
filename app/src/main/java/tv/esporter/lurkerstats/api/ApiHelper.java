package tv.esporter.lurkerstats.api;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.Rfc3339DateJsonAdapter;

import java.io.IOException;
import java.util.Date;

import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;


public class ApiHelper {

    static Retrofit getRetrofitApi(final String baseUrl, final String token){

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(dispatcher.getMaxRequests()); //default 64


        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logger)
                .addInterceptor(chain -> {
            Request originalRequest = chain.request();
            Request newRequest = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer " + token).build();
            return chain.proceed(newRequest);
        })
                .dispatcher(dispatcher)
                .build();

        Moshi moshi = new Moshi.Builder()
                .add(Date.class, new Rfc3339DateJsonAdapter())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();

        return retrofit;
    }

    private static final String STATS_API_BASE = "http://lurker.esporter.tv/api/";
    static String StatsApiToken = "";
    public static StatsApi getStatsApi(){
        return getRetrofitApi(STATS_API_BASE, StatsApiToken).create(StatsApi.class);
    }

    private static final String TWITCH_API_BASE = "https://api.twitch.tv/kraken/";
    static String TwitchApiToken = "";

    public static TwitchApi getTwitchApi(){
        return getRetrofitApi(TWITCH_API_BASE, TwitchApiToken).create(TwitchApi.class);
    }

}
