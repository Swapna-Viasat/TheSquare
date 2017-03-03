package com.hellobaytree.graftrs.shared.reviews;

import com.google.gson.annotations.SerializedName;
import com.hellobaytree.graftrs.shared.models.WorkerSummary;

import java.io.Serializable;

/**
 * Created by Evgheni on 11/11/2016.
 */

public class Review implements Serializable {

    public static final int TYPE_GIVEN = 1;
    public static final int TYPE_RECEIVED = 2;
    public static final int CAT_PUBLISHED = 2;
    public static final int CAT_PENDING = 1;

    public static final int TAB_GIVEN = 5;
    public static final int TAB_RECEIVED = 6;

    public int id;
    public String date;
    public Status status;
    public Type type;
    @SerializedName("avg_rate") public float rating;
    @SerializedName("quality")
    public int quality;
    @SerializedName("reliability")
    public int reliability;
    @SerializedName("attitude")
    public int attitude;
    @SerializedName("safe")
    public int safe;
  /*  @SerializedName("environment")
    public int environment;
    @SerializedName("team")
    public int team;
    @SerializedName("payers")
    public int payers;
    @SerializedName("induction")
    public int induction;*/
    @SerializedName("work_together_again") public boolean wouldHireAgain;
    @SerializedName("got_hired") public boolean gotHired;
    @SerializedName("email_address")
    public String requestEmail;
    @SerializedName("reviewer_first_name")
    public String requestFirstName;
    @SerializedName("reviewer_last_name")
    public String requestLastName;
    @SerializedName("reviewer_contact_phone")
    public String requestMobile;
    @SerializedName("reviewer_company_name")
    public String requestCompany;
    @SerializedName("worker_summary") public WorkerSummary workerSummary;
    @SerializedName("date_review_requested")
    public String dateReviewRequested;
    @SerializedName("company")
    public String company;
    @SerializedName("automated_request")
    public String automatedRequest;
    @SerializedName("pending_verification")
    public boolean pendingVerification;

}
