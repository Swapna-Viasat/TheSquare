package com.hellobaytree.graftrs.shared.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by swapna on 1/30/2017.
 */
public class Nationality implements Parcelable {

    public int id;
    public String name;

    protected Nationality(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<Nationality> CREATOR = new Creator<Nationality>() {
        @Override
        public Nationality createFromParcel(Parcel in) {
            return new Nationality(in);
        }

        @Override
        public Nationality[] newArray(int size) {
            return new Nationality[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
    }
}
