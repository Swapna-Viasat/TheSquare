package construction.thesquare.shared.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gherg on 12/6/2016.
 */

public class Company implements Serializable {
    public int id;
    public String name;
    public String description;
    public int owner;
    @SerializedName("worked_company") public String workedCompany;
    public boolean verified;
    @SerializedName("registration_number") public String registrationNumber;
    @SerializedName("contact_first_name") public String contactFirstName;
    @SerializedName("contact_last_name") public String contactLastName;
    @SerializedName("contact_phone") public String contactPhone;
    @SerializedName("contact_email") public String contactEmail;
    public String logo;
    @SerializedName("post_code") public String postCode;
    @SerializedName("worker_payments") public boolean workerPayments;
    @SerializedName("workers_submit_timesheets") public boolean workersSubmitTimesheets;
    @SerializedName("pay_workers_directly") public boolean payWorkersDirectly;

    public boolean selected;
}
