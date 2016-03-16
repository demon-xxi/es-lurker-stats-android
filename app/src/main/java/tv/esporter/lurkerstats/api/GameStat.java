package tv.esporter.lurkerstats.api;

import android.util.Xml;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GameStat {
    public String game;
    public long duration;

    public String getSafeName(){
        try {
            return URLEncoder.encode(game, Xml.Encoding.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return game;
        }
    }

    public GameStat(String game) {
        this.game = game;
    }
}
