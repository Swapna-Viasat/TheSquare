package com.hellobaytree.graftrs.shared.models;

import com.google.gson.annotations.SerializedName;
import com.hellobaytree.graftrs.shared.data.model.Location;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gherg on 12/6/2016.
 */

public class Job implements Serializable {

    public static final int TAB_OLD = 3;
    public static final int TAB_DRAFT = 1;
    public static final int TAB_LIVE = 2;

    public int id;
    public int experience;
    public boolean liked;
    public Status status;
    public float budget;
    public Role role;
    public String address;
    @SerializedName("start_datetime") public String start;
    @SerializedName("budget_type") public BudgetType budgetType;
    @SerializedName("pay_overtime") public boolean payOvertime;
    @SerializedName("pay_overtime_value") public String overtimeRate;
    @SerializedName("english_level") public int english;
    @SerializedName("contact_name") public String contactName;
    @SerializedName("contact_phone") public String contactPhone;
    // added new fields
    @SerializedName("contact_phone_number") public long contactPhoneNumber;
    @SerializedName("contact_country_code") public int contactCountryCode;
    //
    @SerializedName("workers_quantity") public int workersQuantity;
    @SerializedName("location_name") public String locationName;
    @SerializedName("cscs_required") public boolean cscsRequired;
    @SerializedName("private") public boolean privateJob;
    @SerializedName("is_apprenticeship") public boolean isApprenticeship;
    public Owner owner;

    public List<Skill> skills;
    public List<Qualification> qualifications;
    @SerializedName("experience_type") public List<ExperienceType> experienceTypes;
    public List<Trade> trades;

    public int worker;
    public String description;
    public Company company;

    public class Status implements Serializable {
        public int id;
        public String name;
    }

    public class BudgetType implements Serializable {
        public int id;
        public String name;
    }

    public class Company implements Serializable {
        public String logo;
        public String name;
    }

    public class Owner implements Serializable {
        public String picture;
    }

    public Location location;
}