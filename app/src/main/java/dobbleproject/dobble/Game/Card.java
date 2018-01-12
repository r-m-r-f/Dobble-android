package dobbleproject.dobble.Game;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Card implements Parcelable {
    private int order;
    private List<Integer> indexes;

    public Card(List<Integer> indexes, int order) {
        this.indexes = indexes;
        this.order = order;
    }

    protected Card(Parcel in) {
        order = in.readInt();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public List<Integer> getIndexes() {
        return indexes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(order);
    }
}
