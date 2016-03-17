package tv.esporter.lurkerstats.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Sergey on 3/17/2016.
 */
public class OauthInterceptor implements Interceptor {

    private static final String AUTHORIZATION = "Authorization";
    private static final String HEADER_FMT = "%s %s";

    final String token;
    final String auth;

    public OauthInterceptor(String token, String auth) {
        this.token = token;
        this.auth = auth;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder requestBuilder = original.newBuilder()
                .header("Accept", "application/json")
                .header(AUTHORIZATION, String.format(HEADER_FMT, auth, token))
                .method(original.method(), original.body());

        return chain.proceed(requestBuilder.build());
    }
}
