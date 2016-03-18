package tv.esporter.lurkerstats.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GameStat {
    public String game;
    public long duration;

    public String getSafeName(){
        try {
            return URLEncoder.encode(game, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return game;
        }
    }
}
