package tv.esporter.lurkerstats.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Integrator {

    private static final String TWITCH_GAME_APP = "twitch://game/%s";
    private static final String TWITCH_GAME_WEB = "https://www.twitch.tv/directory/game/%s";

    public static void OpenTwitchGame(Context context, String name){
        Intent intent = Build.intent(Intent.ACTION_VIEW)
                .data(Uri.parse(String.format(TWITCH_GAME_APP, name))).build();

        if (intent.resolveActivity(context.getPackageManager()) != null){
            context.startActivity(intent);
        } else {
            Intent webintent = Build.intent(Intent.ACTION_VIEW)
                    .data(Uri.parse(String.format(TWITCH_GAME_WEB, name))).build();
            context.startActivity(webintent);
        }
    }
}
