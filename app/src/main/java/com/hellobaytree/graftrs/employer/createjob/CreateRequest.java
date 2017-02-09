package com.hellobaytree.graftrs.employer.createjob;

import com.google.gson.annotations.SerializedName;
import com.hellobaytree.graftrs.shared.data.model.Location;
import com.hellobaytree.graftrs.shared.models.ExperienceQualification;
import com.hellobaytree.graftrs.shared.models.ExperienceType;
import com.hellobaytree.graftrs.shared.models.Qualification;
import com.hellobaytree.graftrs.shared.models.Role;
import com.hellobaytree.graftrs.shared.models.Trade;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * Created by gherg on 12/12/2016.
 */

public class CreateRequest implements Serializable {

    public int id;
    public Calendar rawDate;

    public int status;
    public int role;
    public Role roleObject;
    public String roleName;
    public int experience;
    @SerializedName("workers_quantity") public int workersQuantity;
    // trades
    public int[] trades;
    public List<Trade> tradeObjects;
    public List<String> tradeStrings;
    // experience qualifications
    public int[] expQualifications;
    public List<ExperienceQualification> expQualificationObjects;
    public List<String> expQualificationStrings;
    // qualifications
    public int[] qualifications;
    public List<Qualification> qualificationObjects;
    public List<String> qualificationStrings;
    // experience types
    @SerializedName("experience_type") public int[] experienceTypes;
    public List<ExperienceType> experienceTypeObjects;
    public List<String> experienceTypeStrings;
    // skills
    public  int[] skills;
    public List<String> skillStrings;
    @SerializedName("english_level") public int english;
    @SerializedName("budget_type") public int budgetType;
    public float budget;
    @SerializedName("pay_overtime") public boolean overtime;
    @SerializedName("pay_overtime_value") public int overtimeValue;
    @SerializedName("contact_name") public String contactName;
    @SerializedName("contact_phone") public String contactPhone;
    @SerializedName("contact_phone_number") public long contactPhoneNumber;
    @SerializedName("contact_country_code") public int contactCountryCode;

    public String num;
    public String cd;

    public String address;
    public String description;
    @SerializedName("extra_notes") public String notes;

    public String date;
    public String time;
    public String logo;

    @SerializedName("location_name") public String locationName;
    public Location location;
}