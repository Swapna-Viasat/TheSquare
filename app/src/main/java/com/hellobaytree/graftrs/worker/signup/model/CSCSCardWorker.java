package com.hellobaytree.graftrs.worker.signup.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by juanmaggi on 3/9/16.
 */
public class CSCSCardWorker implements Serializable {

    public int id;
    @SerializedName("verification_status")
    public int verificationStatus;
    public String surname;
    @SerializedName("birth_date")
    public String birthDate;
    @SerializedName("registration_number")
    public String registrationNumber;
    @SerializedName("insurance_number")
    public String insuranceNumber;
    @SerializedName("card_picture")
    public String cardPicture;
    @SerializedName("expiry_date")
    public String expiryDate;
    @SerializedName("cscs_records")
    public CscsRecords cscsRecords;

    public class CscsRecords implements Serializable {
        @SerializedName("1")
        public List<CscsRecord> firstRecord;
        @SerializedName("2")
        public List<CscsRecord> secondRecord;
        @SerializedName("3")
        public List<CscsRecord> thirdRecord;

        public class CscsRecord implements Serializable {
            public String id;
            public String name;
            public Category category;

            public class Category implements Serializable {
                public int id;
                public String name;
            }
        }
    }
}
