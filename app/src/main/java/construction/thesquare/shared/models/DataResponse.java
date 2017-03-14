package construction.thesquare.shared.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gherg on 12/6/2016.
 */

public class DataResponse implements Serializable {

    public Response response;

    public static class Response implements Serializable {
        @SerializedName("payment_preferences") public List<PaymentPref> paymentPrefs;
        public List<Trade> trades;
        public List<Skill> skills;
        public List<Qualification> qualifications;
        public List<Role> roles;
        public List<Country> countries;
        public Worker2 worker;
        public Job2 job;
        @SerializedName("experience_types") public List<ExperienceType> experienceTypes;
        @SerializedName("worked_companies") public List<Company> workedCompanies;
        @SerializedName("user_types") public List<UserType> userTypes;
        @SerializedName("experience_qualifications") public List<ExperienceQualification> experienceQualifications;

        // You can thank Alexei the backend guy for this horseshit
        public class Worker2 implements Serializable {
            @SerializedName("english_level") public List<EnglishLevel> englishLevels;
        }

        public class Job2 implements Serializable {
            @SerializedName("budget_type") public List<Job.BudgetType> budgetTypes;
            public List<Job.Status> statuses;
        }
    }
}
