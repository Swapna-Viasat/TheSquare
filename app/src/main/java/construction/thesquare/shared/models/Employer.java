package construction.thesquare.shared.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gherg on 12/29/2016.
 */

public class Employer implements Serializable {
    public int id;
    public String identifier;
    @SerializedName("user_type") public int userType;
    @SerializedName("onboarding_done") public boolean onboardingDone;
    @SerializedName("valid_company_employer") public boolean validCompanyEmployer;
    @SerializedName("reviews_amount") public int reviewCount;
    @SerializedName("reviews_avg") public int reviewInt;
    @SerializedName("first_name") public String firstName;
    @SerializedName("last_name") public String lastName;
    @SerializedName("email_confirmed") public boolean emailConfirmed;
    @SerializedName("onboarding_skipped") public boolean onboardingSkipped;
    public String email;
    @SerializedName("job_title") public String jobTitle;
    @SerializedName("worker_app_notifications") public boolean workerAppNotifications;
    @SerializedName("job_notifications") public boolean jobNotifications;
    @SerializedName("reviews_notifications") public boolean reviewNotifications;
    public Company company;
    public String picture;

    // subscription related fields
    @SerializedName("plan_bookings") public int bookedWithPlan;
    @SerializedName("max_plan_bookings") public int maxForPlan;
    @SerializedName("topup_bookings") public int bookedWithTopups;
    @SerializedName("max_topup_bookings") public int maxForTopups;
    @SerializedName("plan_end_date") public String planExpiration;
    @SerializedName("topup_end_date") public String topupExpiration;
    @SerializedName("plan_name") public String planName;
}
