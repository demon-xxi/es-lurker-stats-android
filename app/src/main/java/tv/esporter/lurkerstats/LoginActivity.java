package tv.esporter.lurkerstats;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.net.URLEncoder;
import java.util.Date;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tv.esporter.lurkerstats.api.ApiHelper;
import tv.esporter.lurkerstats.api.TwitchApi;
import tv.esporter.lurkerstats.api.TwitchStream;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    private static final int STEP_1 = 1;
    private static final int STEP_2 = 2;
    private static final int STEP_3 = 3;

    private static final String EXTRA_STEP = "STEP";
    private static final String EXTRA_STATE = "SATE";
    private static final String EXTRA_CODE = "CODE";

    final static String CLIENT_ID = "f09c6fvlq4wz2p6duxmdj6v0ea14pfi";
    final static String CLIENT_SECRET = "1037wzf6pjwfp94t600iy6ewqoq1gs3";
    final static String REDIRECT_URL = "http://lurker.esporter.tv/oauth_authorization";

    private static final String CODE_PART = "/?code=";
    final static String AUTH_URL = "https://api.twitch.tv/kraken/oauth2/authorize" +
            "?response_type=code" +
            "&client_id=" + CLIENT_ID +
            "&redirect_uri=" + REDIRECT_URL +
            "&scope=user_follows_edit+user_subscriptions" +
            "&popup=false&state=";

    // UI references.
    private WebView mWebView;
    private String mState;
    private int mStep = STEP_1;
    private String mCode;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mWebView =  (WebView)findViewById(R.id.login_web_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        assert mWebView != null;
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setBackgroundColor(Color.TRANSPARENT);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                step(STEP_2);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                int idx = url.indexOf(CODE_PART);
                if (idx > 0) {
                    mCode = url.substring(idx+CODE_PART.length());
                    step(STEP_3);
                    return;
                }
                super.onPageStarted(view, url, favicon);
            }
        });

        if (savedInstanceState != null){
            mWebView.restoreState(savedInstanceState);
            mState = savedInstanceState.getString(EXTRA_STATE);
            mCode = savedInstanceState.getString(EXTRA_CODE);
            step(savedInstanceState.getInt(EXTRA_STEP));
        } else {
            mState = Long.toString(new Date().getTime());
            String loginUrl = AUTH_URL+ mState;
            mWebView.loadUrl(loginUrl);
        }

    }

    void step(int step) {
        mStep = step;
        switch (step) {
            case STEP_1:
                mWebView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case STEP_2:
                mWebView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                break;
            case STEP_3:
                mWebView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);

                TwitchApi api = ApiHelper.getTwitchApi();
                api.getTokenRx(CLIENT_ID, CLIENT_SECRET, "authorization_code",
                        REDIRECT_URL, mCode, mState)
                        .observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .single()
                        .subscribe(tokenResponse -> {
                            Log.w("TOKEN", tokenResponse.access_token);
                        }, Throwable::printStackTrace);


                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_STEP, mStep);
        outState.putString(EXTRA_CODE, mCode);
        outState.putString(EXTRA_STATE, mState);
        mWebView.saveState(outState);
    }

//    /**
//     * Shows the progress UI and hides the login form.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    private void showProgress(final boolean show) {
//        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//        // for very easy animations. If available, use these APIs to fade-in
//        // the progress spinner.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
//
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
//        } else {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//        }
//    }

}

