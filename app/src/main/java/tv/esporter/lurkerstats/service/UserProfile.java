package tv.esporter.lurkerstats.service;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;


public class UserProfile implements Parcelable {
    public final String name;
    public final Uri avata;

    public UserProfile(String name, Uri avata) {
        this.name = name;
        this.avata = avata;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeParcelable(this.avata, 0);
    }

    protected UserProfile(Parcel in) {
        this.name = in.readString();
        this.avata = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Parcelable.Creator<UserProfile> CREATOR = new Parcelable.Creator<UserProfile>() {
        public UserProfile createFromParcel(Parcel source) {
            return new UserProfile(source);
        }

        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };
}
