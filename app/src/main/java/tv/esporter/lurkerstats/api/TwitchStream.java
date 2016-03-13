package tv.esporter.lurkerstats.api;

import java.util.Date;

/**
 * See example JSON representation at https://api.twitch.tv/kraken/streams/lirik
 */
public class TwitchStream extends LinksContainer {
    public long _id;
    public String game;
    public Date created_at;
    public TwitchChannel channel;
}
