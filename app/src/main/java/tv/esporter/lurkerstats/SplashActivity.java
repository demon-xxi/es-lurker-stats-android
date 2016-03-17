package tv.esporter.lurkerstats;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.kv_prefs), Context.MODE_PRIVATE);
        String token = sharedPref.getString(getString(R.string.k_twitch_access_token),null);
        String username = sharedPref.getString(getString(R.string.k_twitch_username),null);

        if (token != null && username != null && !token.isEmpty() && !username.isEmpty()){
            Intent intent = new Intent(this, ViewerActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
