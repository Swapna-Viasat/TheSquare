package com.hellobaytree.graftrs.worker.signup.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by juanmaggi on 3/9/16.
 */
public class CSCSCardWorker implements Parcelable {

    private int id;
    private int verification_status;
    private String surname;
    private String birth_date;
    private String registration_number;
    private String insurance_number;
    @SerializedName("card_picture")
    public String cardPicture;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getVerification_status() { return verification_status; }

    public void setVerification_status(int verification_status) { this.verification_status = verification_status; }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getRegistration_number() {
        return registration_number;
    }

    public void setRegistration_number(String registration_number) {
        this.registration_number = registration_number;
    }

    public String getInsurance_number() {
        return insurance_number;
    }

    public void setInsurance_number(String insurance_number) {
        this.insurance_number = insurance_number;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeInt(verification_status);
        out.writeString(surname);
        out.writeString(birth_date);
        out.writeString(registration_number);
        out.writeString(insurance_number);
    }

    private CSCSCardWorker(Parcel in) {
        id = in.readInt();
        verification_status = in.readInt();;
        surname = in.readString();
        birth_date = in.readString();
        registration_number = in.readString();
        insurance_number = in.readString();
    }

    public static final Parcelable.Creator<CSCSCardWorker> CREATOR = new Parcelable.Creator<CSCSCardWorker>() {

        public CSCSCardWorker createFromParcel(Parcel in) {
            return new CSCSCardWorker(in);
        }

        public CSCSCardWorker[] newArray(int size) {
            return new CSCSCardWorker[size];
        }
    };
}
