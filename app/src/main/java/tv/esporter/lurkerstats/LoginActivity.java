package tv.esporter.lurkerstats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tv.esporter.lurkerstats.api.ApiHelper;
import tv.esporter.lurkerstats.api.TwitchApi;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    private static final int STEP_1 = 1;
    private static final int STEP_2 = 2;
    private static final int STEP_3 = 3;
    private static final int STEP_4 = 4;

    private static final String EXTRA_STEP = "STEP";
    private static final String EXTRA_STATE = "SATE";
    private static final String EXTRA_CODE = "CODE";
    private static final String AUTHORIZATION_CODE = "authorization_code";

    final static String CLIENT_ID = "f09c6fvlq4wz2p6duxmdj6v0ea14pfi";
    final static String CLIENT_SECRET = "1037wzf6pjwfp94t600iy6ewqoq1gs3";
    final static String REDIRECT_URL = "http://lurker.esporter.tv/oauth_authorization";

    private static final String Q_CODE = "code";
    final static String AUTH_URL = "https://api.twitch.tv/kraken/oauth2/authorize" +
            "?response_type=code" +
            "&client_id=" + CLIENT_ID +
            "&redirect_uri=" + REDIRECT_URL +
            "&scope=user_read+user_follows_edit+user_subscriptions" +
            "&popup=false&state=";

    // UI references.
    private WebView mWebView;
    private String mState;
    private int mStep = STEP_1;
    private String mCode;
    private ProgressBar mProgressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mWebView = (WebView) findViewById(R.id.login_web_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        assert mWebView != null;
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setBackgroundColor(Color.TRANSPARENT);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mStep == STEP_1 && !url.startsWith(REDIRECT_URL)) step(STEP_2);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.startsWith(REDIRECT_URL)) {
                    mCode = Uri.parse(url).getQueryParameter(Q_CODE);
                    step(STEP_3);
                    return;
                }
                super.onPageStarted(view, url, favicon);
            }
        });

        if (savedInstanceState != null) {
            mWebView.restoreState(savedInstanceState);
            mState = savedInstanceState.getString(EXTRA_STATE);
            mCode = savedInstanceState.getString(EXTRA_CODE);
            step(savedInstanceState.getInt(EXTRA_STEP));
        } else {
            mState = Long.toString(new Date().getTime());
            step(STEP_1);

        }

    }

    void step(int step) {
        mStep = step;

        Context context = this;
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.kv_prefs), Context.MODE_PRIVATE);

        String k_twitch_access_token = getString(R.string.k_twitch_access_token);
        String k_twitch_scope = getString(R.string.k_twitch_scope);
        String k_twitch_username = getString(R.string.k_twitch_username);

        switch (step) {
            case STEP_1:
                mWebView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);

                String loginUrl = AUTH_URL + mState;
                CookieManager cookieManager = CookieManager.getInstance();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    CookieSyncManager.createInstance(this);
                    cookieManager.removeAllCookie();
                } else {
                    cookieManager.removeAllCookies(null);
                }
                mWebView.loadUrl(loginUrl);

                break;
            case STEP_2:
                mProgressBar.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
                mWebView.setVerticalScrollBarEnabled(true);
                break;
            case STEP_3:
                mWebView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);

                TwitchApi api = ApiHelper.getTwitchApi((String) null);
                api.getTokenRx(CLIENT_ID, CLIENT_SECRET, AUTHORIZATION_CODE,
                        REDIRECT_URL, mCode, mState)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .single()
                        .subscribe(tokenResponse -> {
                            sharedPref.edit()
                                    .putString(k_twitch_access_token,
                                            tokenResponse.access_token)
                                    .putStringSet(k_twitch_scope,
                                            new HashSet<>(Arrays.asList(tokenResponse.scope)))
                                    .apply();
                            step(STEP_4);

                        }, throwable -> {
                            throwable.printStackTrace();
                            Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_LONG).show();
                            step(STEP_1);
                        });
                break;
            case STEP_4:

                mWebView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);

                ApiHelper.getTwitchApi(this)
                        .meRx()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .single()
                        .subscribe(
                                twitchUser -> {
                                    sharedPref.edit()
                                            .putString(k_twitch_username,
                                                    twitchUser.name)
                                            .apply();

                                    transition();
                                }
                                ,
                                throwable -> {
                                    throwable.printStackTrace();
                                    Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_LONG).show();
                                    step(STEP_1);
                                }
                        );

                break;
        }
    }

    private void transition() {
        Intent intent = new Intent(this, ViewerActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_STEP, mStep);
        outState.putString(EXTRA_CODE, mCode);
        outState.putString(EXTRA_STATE, mState);
        mWebView.saveState(outState);
    }

}

