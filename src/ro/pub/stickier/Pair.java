package ro.pub.stickier;

import android.os.Parcel;
import android.os.Parcelable;

public class Pair implements Parcelable {
	public int e1;
	public int e2;
	
	public Pair(int element1, int element2){
		e1 = element1;
		e2 = element2;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
        out.writeInt(e1);
        out.writeInt(e2);
    }

    public Pair(Parcel in) {
        e1 = in.readInt();
        e2 = in.readInt();
    }
    
    public static final Parcelable.Creator<Pair> CREATOR = new Parcelable.Creator<Pair>() {
        public Pair createFromParcel(Parcel in) {
            return new Pair(in);
        }

        public Pair[] newArray(int size) {
            return new Pair[size];
        }
    };

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
