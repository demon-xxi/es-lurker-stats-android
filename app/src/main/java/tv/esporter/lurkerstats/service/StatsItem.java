package tv.esporter.lurkerstats.service;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Sergey on 3/12/2016.
 *
 */
public class StatsItem implements Parcelable {

    public String name;
    public String title;
    public Long value;
    public String image;
    public Type type;

    public StatsItem() {
    }

    public StatsItem(Type type, String name, String title, String image, Long value) {
        this.name = name;
        this.title = title;
        this.value = value;
        this.image = image;
        this.type = type;
    }


    public enum Type {
        GAME, CHANNEL
    }


    /**
     * An array of sample (dummy) items.
     */
    public static final List<StatsItem> GAMES = new ArrayList<>();
    public static final List<StatsItem> CHANNELS = new ArrayList<>();

    private static final int COUNT = 25;

    static {
        Random rand = new Random();
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            long hrs = rand.nextInt(100);
            GAMES.add(new StatsItem(
                    Type.GAME,
                    String.format("Game %d", i),
                    String.format("Game %d", i),
                    "http://static-cdn.jtvnw.net/ttv-boxart/Stardew%20Valley-272x380.jpg",
                    hrs
            ));
        }

        for (int i = 1; i <= COUNT; i++) {
            long hrs = rand.nextInt(100);
            CHANNELS.add(new StatsItem(
                    Type.CHANNEL,
                    String.format("Channel %d", i),
                    String.format("Channel %d", i),
                    "http://static-cdn.jtvnw.net/jtv_user_pictures/lirik-profile_image-b3ff7b706b3de124-300x300.png",
                    hrs
            ));
        }
    }


    @Override
    public String toString() {
        return String.format("%s (%s)", type, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatsItem statsItem = (StatsItem) o;

        if (name != null ? !name.equals(statsItem.name) : statsItem.name != null) return false;
        return type == statsItem.type;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.title);
        dest.writeValue(this.value);
        dest.writeString(this.image);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    protected StatsItem(Parcel in) {
        this.name = in.readString();
        this.title = in.readString();
        this.value = (Long) in.readValue(Long.class.getClassLoader());
        this.image = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Type.values()[tmpType];
    }

    public static final Parcelable.Creator<StatsItem> CREATOR = new Parcelable.Creator<StatsItem>() {
        public StatsItem createFromParcel(Parcel source) {
            return new StatsItem(source);
        }

        public StatsItem[] newArray(int size) {
            return new StatsItem[size];
        }
    };
}
