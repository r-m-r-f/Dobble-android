package dobbleproject.dobble.Player;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayerInfo implements Parcelable {
    public String name;
    public String ip;
    public int port;

    public PlayerInfo(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    protected PlayerInfo(Parcel in) {
        name = in.readString();
        ip = in.readString();
        port = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(ip);
        dest.writeInt(port);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlayerInfo> CREATOR = new Creator<PlayerInfo>() {
        @Override
        public PlayerInfo createFromParcel(Parcel in) {
            return new PlayerInfo(in);
        }

        @Override
        public PlayerInfo[] newArray(int size) {
            return new PlayerInfo[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    // Parcelable methods

}
