package tv.esporter.lurkerstats.service;

import android.os.Parcel;
import android.os.Parcelable;

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

    @Override
    public String toString() {
        return String.format("%s (%s)", type, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatsItem statsItem = (StatsItem) o;

        return name != null ? name.equals(statsItem.name) : statsItem.name == null && type == statsItem.type;

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
