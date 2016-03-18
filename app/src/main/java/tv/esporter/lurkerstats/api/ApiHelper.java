package tv.esporter.lurkerstats.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.Rfc3339DateJsonAdapter;

import java.util.Date;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import tv.esporter.lurkerstats.R;


public class ApiHelper {


    private static final String BEARER = "Bearer";
    private static final String OAUTH = "OAuth";

    @NonNull
    static Retrofit getRetrofitApi(final String baseUrl, final String token, final String auth){

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(dispatcher.getMaxRequests()); //default 64

        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(HttpLoggingInterceptor.Level.BASIC);


        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (token != null){
            builder.addInterceptor(new OauthInterceptor(token, auth));
        }

        builder = builder.addInterceptor(logger);

        OkHttpClient httpClient = builder.dispatcher(dispatcher).build();

        Moshi moshi = new Moshi.Builder()
                .add(Date.class, new Rfc3339DateJsonAdapter())
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();
    }

    private static final String STATS_API_BASE = "http://lurker.esporter.tv/api/";
    static String StatsApiToken = "";
    public static StatsApi getStatsApi(){
        return getRetrofitApi(STATS_API_BASE, StatsApiToken, BEARER).create(StatsApi.class);
    }

    private static final String TWITCH_API_BASE = "https://api.twitch.tv/kraken/";

//    public static TwitchApi getTwitchApi(){
//        return getTwitchApi((String)null);
//    }

    public static TwitchApi getTwitchApi(String token){
        return getRetrofitApi(TWITCH_API_BASE, token, OAUTH).create(TwitchApi.class);
    }

    public static TwitchApi getTwitchApi(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.kv_prefs), Context.MODE_PRIVATE);
        String token = sharedPref.getString(context.getString(R.string.k_twitch_access_token),null);
        return getTwitchApi(token);
    }

}
