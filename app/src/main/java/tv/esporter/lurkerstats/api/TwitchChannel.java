package tv.esporter.lurkerstats.api;

import java.util.Date;

/**
 * See example JSON representation at https://api.twitch.tv/kraken/channels/lirik
 */
public class TwitchChannel extends LinksContainer {
    public long _id;
    public String display_name;
    public String name;
    public String logo;
    public String status;
    public Date updated_at;
    public Date created_at;
}
