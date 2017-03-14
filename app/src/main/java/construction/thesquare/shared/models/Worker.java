package construction.thesquare.shared.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import construction.thesquare.shared.data.model.Location;

/**
 * Created by gherg on 12/6/2016.
 */

public class Worker implements Serializable {
    public int id;
    public String identifier;
    @SerializedName("user_type") public int userType;
    public String picture;
    @SerializedName("post_code") public String zip;
    @SerializedName("onboarding_done") public boolean onboardingDone;
    @SerializedName("first_name") public String firstName;
    @SerializedName("last_name") public String lastName;
    public String email;
    @SerializedName("english_levels") public List<EnglishLevel> englishLevels;
    @SerializedName("reviews_avg") public float rating;
    @SerializedName("reviews_amount") public int numReviews;
    @SerializedName("available_now") public boolean now;
    public List<Role> roles;
    public List<Trade> trades;
    @SerializedName("years_experience") public int yearsExperience;
    public List<Qualification> qualifications;
    public List<Skill> skills;
    @SerializedName("worked_companies") public List<Company> companies;
    public Location location;
    @SerializedName("onboarding_skipped") public boolean onboardingSkipped;
    @SerializedName("new_job_matches_notifications") public boolean newJobMatchesNotifications;
    @SerializedName("job_offers_notifications") public boolean jobOffersNotifications;
    @SerializedName("job_booking_declines_notifications") public boolean jobBookingDeclinesNotifications;
    @SerializedName("review_notifications") public boolean reviewNotifications;
    public String address;
    public String bio;
    @SerializedName("english_level")
    public EnglishLevel englishLevel;
    @SerializedName("experience_types")
    public List<ExperienceType> experienceTypes;
    @SerializedName("commute_time")
    public int commuteTime;
    @SerializedName("filter_commute_time")
    public int filterCommuteTime;
    @SerializedName("passport_upload")
    public String passportUpload;
    @SerializedName("date_of_birth")
    public String dateOfBirth;
    public List<Application> applications;

    @SerializedName("matched_role") public Role matchedRole;
    public Nationality nationality;
    @SerializedName("ni_number")
    public String niNumber;
    public List<Language> languages; 
    @SerializedName("review_data") public ReviewData reviewData; 
    public List<Device> devices;
    public boolean liked;

    public class Device implements Serializable {
        @SerializedName("phone_number")
        public String phoneNumber;
    } 
}