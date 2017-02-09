package com.hellobaytree.graftrs.shared.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by swapna on 1/30/2017.
 */
public class Language implements Parcelable{

    public int id;
    public String name;

    protected Language(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<Language> CREATOR = new Creator<Language>() {
        @Override
        public Language createFromParcel(Parcel in) {
            return new Language(in);
        }

        @Override
        public Language[] newArray(int size) {
            return new Language[size];
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
