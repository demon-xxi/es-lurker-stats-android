package tv.esporter.lurkerstats.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Integrator {

    private static final String TWITCH_GAME_APP = "twitch://game/%s";
    private static final String TWITCH_GAME_WEB = "https://www.twitch.tv/directory/game/%s";

    private static final String TWITCH_CHANNEL_APP = "twitch://stream/%s";
    private static final String TWITCH_CHANNEL_WEB = "https://www.twitch.tv/%s";

    private static boolean openAnyUriIntent(Context context, Uri... uris){
        for (Uri uri : uris){
            Intent intent = Build.intent(Intent.ACTION_VIEW)
                    .data(uri).build();
            if (intent.resolveActivity(context.getPackageManager())==null) continue;
            context.startActivity(intent);
            return true;
        }
        return  false;
    }

    public static void openTwitchGame(Context context, String name){
        openAnyUriIntent(context,
                Uri.parse(String.format(TWITCH_GAME_APP, name)),
                Uri.parse(String.format(TWITCH_GAME_WEB, name))
        );
    }

    public static void openTwitchChannel(Context context, String name) {
        openAnyUriIntent(context,
                Uri.parse(String.format(TWITCH_CHANNEL_APP, name)),
                Uri.parse(String.format(TWITCH_CHANNEL_WEB, name))
        );
    }
}
