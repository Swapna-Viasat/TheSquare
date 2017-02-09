package com.hellobaytree.graftrs.shared.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by juanmaggi on 27/10/16.
 */
public class User implements Parcelable {

    protected int id;
    protected String country_code;
    protected String phone_number;
    protected String first_name;
    protected String last_name;
    protected String picture;
    protected String email;
    protected boolean onboarding_done;
    protected boolean onboarding_skipped;

    public User() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isOnboarding_done() {
        return onboarding_done;
    }

    public void setOnboarding_done(boolean onboarding_done) {
        this.onboarding_done = onboarding_done;
    }

    public boolean isOnboarding_skipped() {
        return onboarding_skipped;
    }

    public void setOnboarding_skipped(boolean onboarding_skipped) {
        this.onboarding_skipped = onboarding_skipped;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(country_code);
        out.writeString(phone_number);
        out.writeString(first_name);
        out.writeString(last_name);
        out.writeString(picture);
        out.writeString(email);
        out.writeByte((byte) (onboarding_done ? 1 : 0));
        out.writeByte((byte) (onboarding_skipped ? 1 : 0));
    }

    private User(Parcel in) {
        id = in.readInt();
        country_code = in.readString();
        phone_number = in.readString();
        first_name = in.readString();
        last_name = in.readString();
        picture = in.readString();
        email = in.readString();
        onboarding_done = in.readByte() != 0;
        onboarding_skipped = in.readByte() != 0;
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

}
