package construction.thesquare.shared.data;

import java.util.HashMap;
import java.util.List;

import construction.thesquare.employer.signup.model.Employer;
import construction.thesquare.employer.signup.model.EmployerVerify;
import construction.thesquare.employer.subscription.model.CardsResponse;
import construction.thesquare.employer.subscription.model.CreateCardRequest;
import construction.thesquare.employer.subscription.model.CreateCardResponse;
import construction.thesquare.employer.subscription.model.RemoveCardResponse;
import construction.thesquare.shared.applications.model.Feedback;
import construction.thesquare.shared.data.model.LoginUser;
import construction.thesquare.shared.data.model.Logout;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.model.SMSSent;
import construction.thesquare.shared.data.model.response.EmployerJobResponse;
import construction.thesquare.shared.data.model.response.JobWorkersResponse;
import construction.thesquare.shared.data.model.response.QuickInviteResponse;
import construction.thesquare.shared.models.Company;
import construction.thesquare.shared.models.ContactCategory;
import construction.thesquare.shared.models.DataResponse;
import construction.thesquare.shared.models.EnglishLevel;
import construction.thesquare.shared.models.ExperienceQualification;
import construction.thesquare.shared.models.ExperienceType;
import construction.thesquare.shared.models.Job;
import construction.thesquare.shared.models.Qualification;
import construction.thesquare.shared.models.Role;
import construction.thesquare.shared.models.RolesRequest;
import construction.thesquare.shared.models.Skill;
import construction.thesquare.shared.models.StaticData;
import construction.thesquare.shared.models.StatusMessageResponse;
import construction.thesquare.shared.models.Trade;
import construction.thesquare.shared.reviews.Review;
import construction.thesquare.shared.reviews.ReviewResponse;
import construction.thesquare.shared.reviews.ReviewUpdateResponse;
import construction.thesquare.shared.reviews.ReviewsResponse;
import construction.thesquare.worker.help.model.HelpWorkerResponse;
import construction.thesquare.worker.jobmatches.model.Application;
import construction.thesquare.worker.jobmatches.model.MatchesResponse;
import construction.thesquare.worker.jobmatches.model.Ordering;
import construction.thesquare.worker.myjobs.model.JobsResponse;
import construction.thesquare.worker.signup.model.CSCSCardWorker;
import construction.thesquare.worker.signup.model.Worker;
import construction.thesquare.worker.signup.model.WorkerVerify;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BaseApiInterface {


    /**
     * Data endpoints
     */

    @PATCH("/employers/{company_id}/company/")
    Call<ResponseBody> updateLogo(@Path("company_id") int companyId,
                                  @Body HashMap<String, String> body);

    @GET("/data/")
    Call<DataResponse> fetchData();

    @GET("/data/qualifications/")
    Call<ResponseObject<List<Qualification>>> fetchQualifications();

    @GET("/data/experience_qualifications/")
    Call<ResponseObject<List<ExperienceQualification>>> fetchExperienceQualifications();

    @GET("/data/experience_qualifications/")
    Call<ResponseObject<List<Qualification>>> fetchRequirements();

    @GET("/data/{pk}/role_qualifications/")
    Call<ResponseObject<List<Qualification>>> fetchRoleQualifications(@Path("pk") int roleId);

    @GET("/data/worked_companies/")
    Call<ResponseObject<List<Company>>> fetchCompanies();

    @GET("/data/roles/")
    Call<ResponseObject<List<Role>>> fetchRoles();

    @GET("/role/summary/")
    Call<ResponseObject<List<Role>>> fetchRolesBrief();

    @GET("/data/trades/")
    Call<ResponseObject<List<Trade>>> fetchTrades();

    @GET("/data/experience_types/")
    Call<ResponseObject<List<ExperienceType>>> fetchExperienceTypes();

    @GET("/data/skills/")
    Call<ResponseObject<List<Skill>>> fetchSkills();

    @GET("/data/{pk}/role_skills/")
    Call<ResponseObject<List<Skill>>> fetchRoleSkills(@Path("pk") int roleId);

    @GET("/data/statics/")
    Call<ResponseObject<StaticData>> fetchStaticData();

    @POST("/role/qualifications/")
    Call<ResponseObject<List<Qualification>>> fetchRoleQualifications(@Body RolesRequest request);

    @POST("/role/skills/")
    Call<ResponseObject<List<Skill>>> fetchRoleSkills(@Body RolesRequest request);

    @GET("/data/english_levels/")
    Call<ResponseObject<List<EnglishLevel>>> fetchEnglishLevels();

    @POST("/users/login/")
    Call<ResponseObject<LoginUser>> loginUser(@Body HashMap<String, String> loginRequest);

    @POST("/users/forgot_password/")
    Call<StatusMessageResponse> forgotPassword(@Body HashMap<String, String> submitRequest);

    @GET("/users/me/")
    Call<ResponseObject<construction.thesquare.shared.models.Worker>> meWorker();

    @GET("/users/me/")
    Call<ResponseObject<construction.thesquare.shared.models.Employer>> meEmployer();

    @GET("/workers/{pk}/")
    Call<ResponseObject<construction.thesquare.shared.models.Worker>>
    getFilteredWorker(@Path("pk") int userId, @Query("fields") List<String> requiredFields);

    /////////////////////////////////Employers/////////////////////////////////
    @POST("/employers/")
    Call<ResponseObject<Employer>> registrationEmployer(@Body HashMap<String, String> registrationRequest);

    @POST("/employers/verify/")
    Call<ResponseObject<EmployerVerify>> verifyEmployerNumber(@Body HashMap<String, String> verificationNumberRequest);

    @PATCH("/employers/{pk}/")
    Call<ResponseObject<Employer>> persistOnboardingEmployer(@Path("pk") int id, @Body HashMap<String, Object> onBoardingRequestEmployer);

    @PATCH("/employers/{pk}/")
    Call<ResponseObject<construction.thesquare.shared.models.Employer>>
    patchEmployer(@Path("pk") int id, @Body HashMap<String, Object> body);

    @PATCH("/employers/{pk}/company/")
    Call<ResponseObject<Employer>> persistOnboardingEmployerCompany(@Path("pk") int id, @Body HashMap<String, Object> onBoardingRequestEmployerCompany);

    @Multipart
    @PATCH("/employers/{pk}/company/")
    Call<ResponseObject<Employer>> uploadProfileImageEmployerCompany(@Path("pk") int id, @Part MultipartBody.Part file);

    @PATCH("/employers/{pk}/company/")
    Call<ResponseObject<Employer>>
    uploadProfileImageEmployerCompany2(@Path("pk") int id, @Body HashMap<String, String> body);

    @GET("/employers/{pk}")
    Call<ResponseObject<Employer>> getEmployerProfile(@Path("pk") int id);

    @POST("/employers/logout/")
    Call<ResponseObject<Logout>> logoutEmployer();

    @POST("/employers/resend_verification_sms/")
    Call<ResponseObject<SMSSent>> resendSMSEmployer(@Body HashMap<String, String> resendSMSRequest);

    @POST("/employers/login/")
    Call<ResponseObject<Employer>> loginEmployer(@Body HashMap<String, String> loginRequest);

    // TODO: when adding other tabs to "my workers" move the params out
    @GET("/employers/{pk}/workers/?like=true")
    Call<ResponseObject<List<construction.thesquare.employer.mygraftrs.model.Worker>>>
    fetchWorkers(@Path("pk") int id);

    @POST("/workers/{pk}/job_offer/")
    Call<QuickInviteResponse> quickInvite(@Body HashMap<String, Object> body, @Path("pk") int id);


    /////////////////////////////////Workers/////////////////////////////////
    @POST("/workers/")
    Call<ResponseObject<Worker>> registrationWorker(@Body HashMap<String, String> registrationRequest);

    @POST("/workers/verify/")
    Call<ResponseObject<WorkerVerify>> verifyWorkerNumber(@Body HashMap<String, String> verificationNumberRequest);

    @PATCH("/workers/{pk}/")
    Call<ResponseObject<construction.thesquare.shared.models.Worker>>
    patchWorker(@Path("pk") int id, @Body HashMap<String, Object> body);

    @PATCH("/workers/{pk}/cscs_card/")
    Call<ResponseObject<CSCSCardWorker>>
    persistOnboardingWorkerCSCSCard(@Path("pk") int id, @Body HashMap<String, Object> onBoardingWorkerCSCSCardRequest);

    @Multipart
    @PATCH("/workers/{pk}/")
    Call<ResponseObject<construction.thesquare.shared.models.Worker>> uploadProfileImageWorker(@Path("pk") int id, @Part MultipartBody.Part file);

    @PATCH("/workers/{pk}/")
    Call<ResponseObject<Worker>> persistWorkerLocation(@Path("pk") int id, @Body HashMap<String, Object> workerLocationRequest);

    @GET("/workers/{pk}/")
    Call<ResponseObject<construction.thesquare.shared.models.Worker>> getWorkerProfile(@Path("pk") int id);

    @GET("/workers/{pk}/cscs_card/")
    Call<ResponseObject<CSCSCardWorker>> getWorkerCSCSCard(@Path("pk") int id);

    @POST("/workers/logout/")
    Call<ResponseObject<Logout>> logoutWorker();

    @POST("/workers/{pk}/like/")
    Call<ResponseObject<StatusMessageResponse>> likeWorker(@Path("pk") int workerId);

    @POST("/workers/{pk}/unlike/")
    Call<ResponseObject<StatusMessageResponse>> unlikeWorker(@Path("pk") int workerId);

    @POST("/workers/resend_verification_sms/")
    Call<ResponseObject<SMSSent>> resendSMSWorker(@Body HashMap<String, String> resendSMSRequest);

    @POST("/workers/login/")
    Call<ResponseObject<Worker>> loginWorker(@Body HashMap<String, String> loginRequest);

    @GET("/workers/{pk}/my_jobs/")
    Call<JobsResponse> getMyJobs(@Path("pk") int workerId,
                                 @Query("status") Integer status,
                                 @Query("like") boolean liked,
                                 @Query("offer") boolean isOffer);

    @GET("/data/nationality/")
    Call<ResponseObject<List<construction.thesquare.shared.models.Nationality>>> fetchNationality();

    @GET("data/language/")
    Call<ResponseObject<List<construction.thesquare.shared.models.Language>>> fetchLanguage();


    /////////////////////////////////Jobs/////////////////////////////////
    @POST("/jobs/")
    Call<ResponseObject<Job>> createJob(@Body HashMap<String, Object> createJobRequest);

    @POST("/jobs/{pk}/cancel/")
    Call<ResponseBody> cancelJob(@Path("pk") int jobId);

    @GET("/jobs/{pk}/")
    Call<ResponseObject<construction.thesquare.worker.jobmatches.model.Job>>
    fetchSingleJob(@Path("pk") int jobId);

    @GET("/jobs/{pk}/")
    Call<ResponseObject<Job>> fetchJob(@Path("pk") int jobId);

    @PATCH("/jobs/{pk}/")
    Call<ResponseObject<Job>> editJob(@Path("pk") int id, @Body HashMap<String, Object> editJobRequest);

    @GET("/jobs/")
    Call<ResponseObject<Job[]>> getJobList(@Query("page") int page);

    @GET("/jobs/")
    Call<EmployerJobResponse> fetchJobs();

    @DELETE("/jobs/{pk}/")
    Call<ResponseObject<Job>> deleteJob(@Path("pk") int id);

    @DELETE("/jobs/{pk}/")
    Call<ResponseObject<Job>> removeJob(@Path("pk") int id);

    @GET("jobs/{pk}/workers/")
    Call<ResponseObject<Worker[]>> getJobLiveWorkers(@Path("pk") int id);

    // TODO: extract url params into the method params
    @GET("jobs/{pk}/workers/?fields=id,picture,applications,first_name,last_name,matched_role,liked")
    Call<JobWorkersResponse> fetchJobWorkers(@Path("pk") int id, @Query("status") int status);

    // TODO: extract url params into the method params
    @GET("jobs/{pk}/workers/?fields=id,picture,applications,first_name,last_name,matched_role,liked")
    Call<JobWorkersResponse> fetchJobWorkerMatches(@Path("pk") int id);

    @POST("jobs/{pk}/like/")
    Call<ResponseObject<StatusMessageResponse>> likeJob(@Path("pk") int id);

    @POST("jobs/{pk}/unlike/")
    Call<ResponseObject<StatusMessageResponse>> unlikeJob(@Path("pk") int id);

    @POST("jobs/{pk}/apply/")
    Call<ResponseObject<Application>> applyJob(@Path("pk") int id);

    @GET("/jobs/")
    Call<MatchesResponse> getJobMatches(@Query("ordering") Ordering ordering,
                                        @Query("filter_commute_time") Integer commuteTime);

    @POST("applications/{pk}/accept/")
    Call<ResponseBody> acceptApplication(@Path("pk") int id);


    @GET("/reviews/")
    Call<ReviewsResponse> getReviews();

    @GET("/reviews/{pk}/")
    Call<ReviewResponse> getReview(@Path("pk") int id);

    @PATCH("/reviews/{pk}/")
    Call<ReviewUpdateResponse> updateReview(@Path("pk") int id, @Body Review review);

    @POST("/reviews/request_review/")
    Call<ReviewsResponse> requestReview(@Body Review review);

    @GET("/workers/{pk}/")
    Call<ResponseObject<construction.thesquare.shared.models.Worker>> getWorkerAggregateReview(@Path("pk") int id);

    //    APPLICATIONS
    @POST("/applications/{pk}/cancel_booking/")
    Call<ResponseObject<Application>> cancelBooking(@Path("pk") int id, @Body Feedback feedbackMessage);

    /**
     * Credit cards
     */

    @GET("/payments/credit_cards/")
    Call<CardsResponse> fetchCards();

    @POST("/payments/credit_cards/")
    Call<CreateCardResponse> addCard(@Body CreateCardRequest data);

    @DELETE("/payments/credit_cards/{pk}/")
    Call<RemoveCardResponse> removeCard(@Path("pk") int id);

    @POST("/payments/subscribe/")
    Call<ResponseObject> subscribe(@Body HashMap<String, Object> body);

    @POST("/payments/manage/setup/")
    Call<ResponseObject> setupPayment(@Body HashMap<String, Object> body);

    @DELETE("/payments/manage/cancel_all/")
    Call<ResponseBody> cancelAll();

    @POST("/payments/manage/manual_subscription/")
    Call<ResponseBody> submitAlternativePayment(@Body HashMap<String, Object> body);

    /**
     * Firebase token!
     */
    @POST("/workers/link_firebase/")
    Call<ResponseBody> sendWorkerToken(@Body HashMap<String, Object> body);

    @POST("/employers/link_firebase/")
    Call<ResponseBody> sendEmployerToken(@Body HashMap<String, Object> body);

    /**
     * Contact
     */
    @GET("/data/contact_categories/")
    Call<ResponseObject<List<ContactCategory>>> fetchContactCategories();

    @POST("/contact/")
    Call<StatusMessageResponse> postContactMessage(@Body HashMap<String, String> body);

    /**
     * Help
     */
    @GET("/faq/")
    Call<HelpWorkerResponse> getSearchData(@Query("search") String search);
}