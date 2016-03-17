package tv.esporter.lurkerstats.api;

import java.util.Date;

/**
 * Created by Sergey on 3/17/2016.
 */
public class TwitchUser extends LinksContainer {
    public long _id;
    public String display_name;
    public String name;
    public String logo;
    public Date updated_at;
    public Date created_at;
}
