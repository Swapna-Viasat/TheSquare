package com.hellobaytree.graftrs.employer.myjobs.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.createjob.CreateJobActivity;
import com.hellobaytree.graftrs.employer.createjob.CreateRequest;
import com.hellobaytree.graftrs.employer.createjob.PreviewJobActivity;
import com.hellobaytree.graftrs.employer.myjobs.JobsContract;
import com.hellobaytree.graftrs.employer.myjobs.JobsPresenter;
import com.hellobaytree.graftrs.employer.myjobs.adapter.JobsAdapter;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.models.DataResponse;
import com.hellobaytree.graftrs.shared.models.ExperienceType;
import com.hellobaytree.graftrs.shared.models.Job;
import com.hellobaytree.graftrs.shared.models.Qualification;
import com.hellobaytree.graftrs.shared.models.Skill;
import com.hellobaytree.graftrs.shared.models.Trade;
import com.hellobaytree.graftrs.shared.utils.Constants;
import com.hellobaytree.graftrs.shared.utils.DateUtils;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.utils.TextTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/29/2016.
 */

public class JobsListFragment extends Fragment
        implements JobsContract.View, JobsAdapter.JobsListener {

    public static final String TAG = "JobsListFragment";
    private JobsContract.UserActionsListener mUserActionsListener;
    private List<Job> data = new ArrayList<>();
    private JobsAdapter adapter;
    private List<String> skillStrings = new ArrayList<>();
    private List<String> tradeStrings = new ArrayList<>();
    private List<String> requirementStrings = new ArrayList<>();
    private List<String> qualificationStrings = new ArrayList<>();
    private List<String> experienceTypeStrings = new ArrayList<>();

    @BindView(R.id.no_matches) ViewGroup noMatches;
    @BindView(R.id.rv) RecyclerView rv;

    public static JobsListFragment newInstance(int type) {
        JobsListFragment fragment = new JobsListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_EMPLOYER_JOB_TAB, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserActionsListener = new JobsPresenter(this, getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jobs_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new JobsAdapter(data, this);
        adapter.registerAdapterDataObserver(observer);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserActionsListener.fetchJobs();
    }

    @OnClick(R.id.no_matches)
    public void create() {
        Intent intent = new Intent(getActivity(), CreateJobActivity.class);
        getActivity().startActivity(intent);
    }

    @Override
    public void onJob(Job job) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_employer_content, JobDetailsFragment
                        .newInstance(job.id))
                .addToBackStack("")
                .commit();
    }

    @Override
    public void onViewDraft(Job job) {
        // Toast.makeText(getContext(), "Under Development", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), PreviewJobActivity.class);
        intent.putExtra("request", prepareDraftDetails(job));
        startActivity(intent);
    }

    @Override
    public void onAction(final Job job, int action) {
        switch (action) {
            case JobsAdapter.ACTION_DELETE:
                DialogBuilder.showDeleteDraftDialog(getContext(), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                removeJob(job.id);
                            }
                        });
                break;
            case JobsAdapter.ACTION_REPUBLISH:
                // Toast.makeText(getContext(), "Under Development", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), PreviewJobActivity.class);
                intent.putExtra("request", prepareDraftDetails(job));
                startActivity(intent);
                break;
        }
    }

    @Override
    public void displayJobs(List<Job> jobs) {
        //
        data.clear();
        for (Job job : jobs) {
            if (job.status.id == getArguments().getInt(Constants.KEY_EMPLOYER_JOB_TAB)) {
                data.add(job);
            }
        }
        for (Job job : jobs) {
            if (3 == getArguments().getInt(Constants.KEY_EMPLOYER_JOB_TAB) &&
                    job.status.id == 4) {
                data.add(job);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showProgress(boolean show) {
        //
    }

    private RecyclerView.AdapterDataObserver observer =
            new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (data.isEmpty()) {
                noMatches.setVisibility(View.VISIBLE);
            } else {
                noMatches.setVisibility(View.GONE);
            }
        }
    };

    private CreateRequest prepareDraftDetails(Job job) {
        CreateRequest result = new CreateRequest();
        try {

            result.id = job.id;

            result.roleName = job.role.name;
            result.role = job.role.id;
            result.roleObject = job.role;
            result.experience = job.experience;
            result.budget = job.budget;
            result.budgetType = job.budgetType.id;
            result.location = job.location;
            result.locationName = job.locationName;
            result.description = job.description;
            result.english = job.english;
            result.overtime = job.payOvertime;
            result.overtimeValue = job.overtimeRate;
            String englishString = "Basic";
            switch (job.english) {
                case 3:
                    englishString = "Fluent";
                    break;
                case 4:
                    englishString = "Native";
                    break;
            }
            result.englishLevelString = englishString;

            /**
             * Loading trades.
             */
            if (null != job.trades && !job.trades.isEmpty()) {
                TextTools.log(TAG, "trades not null");
                int[] tradeIds = new int[job.trades.size()];
                for (int i = 0; i < job.trades.size(); i++) {
                    tradeIds[i] = job.trades.get(i).id;
                    tradeStrings.add(job.trades.get(i).name);
                }
                result.trades = tradeIds;
                result.tradeStrings = tradeStrings;
            }

            /**
             * Loading qualifications.
             */
            if (null != job.qualifications) {
                if (!job.qualifications.isEmpty()) {
                    // filtering out qualifications that are requirements
                    List<Qualification> q2 = new ArrayList<>();
                    for (Qualification q : job.qualifications) {
                        if (!q.onExperience) {
                            q2.add(q);
                        }
                    }
                    int[] qualificationIds = new int[q2.size()];
                    List<String> qualificationStrings = new ArrayList<>();
                    for (int i = 0; i < q2.size(); i++) {
                        qualificationIds[i] = q2.get(i).id;
                        qualificationStrings.add(q2.get(i).name);
                    }
                    result.qualificationObjects = q2;
                    result.qualifications = qualificationIds;
                    result.qualificationStrings = qualificationStrings;
                }
            }

            /**
             * Loading skills.
             */
            if (null != job.skills) {
                int[] skillIds = new int[job.skills.size()];
                for (int i = 0; i < job.skills.size(); i++) {
                    skillIds[i] = job.skills.get(i).id;
                    skillStrings.add(job.skills.get(i).name);
                }
                result.skills = skillIds;
                result.skillStrings = skillStrings;
            }

            /**
             * Loading requirements.
             */
            if (null != job.qualifications) {
                if (!job.qualifications.isEmpty()) {
                    // extracting the requirements from qualifications
                    List<Qualification> q2 = new ArrayList<>();
                    for (Qualification q : job.qualifications) {
                        if (q.onExperience) {
                            q2.add(q);
                        }
                    }
                    int[] requirementIds = new int[q2.size()];
                    List<String> requirementStrings = new ArrayList<>();
                    for (int i = 0; i < q2.size(); i++) {
                        requirementIds[i] = q2.get(i).id;
                        requirementStrings.add(q2.get(i).name);
                    }
                    result.requirementObjects = q2;
                    result.requirements = requirementIds;
                    result.requirementStrings = requirementStrings;
                }
            }

            /**
             * Loading experience types.
             */
            if (null != job.experienceTypes) {
                int[] experienceTypeIds = new int[job.experienceTypes.size()];
                for (int i = 0; i < job.experienceTypes.size(); i++) {
                    experienceTypeIds[i] = job.experienceTypes.get(i).id;
                    experienceTypeStrings.add(job.experienceTypes.get(i).name);
                }
                result.experienceTypes = experienceTypeIds;
                result.experienceTypeStrings = experienceTypeStrings;
            }

            /**
             * Date and time.
             */
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date = simpleDateFormat.parse(job.start);
            calendar.setTime(date);
            result.rawDate = calendar;
            result.date = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)
                    + "-"
                    + String.valueOf(calendar.get(Calendar.MONTH)+1)
                    + "-"
                    + calendar.get(Calendar.YEAR));
            result.time = DateUtils.time(calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE));

            /**
             * Reporting to info. and logo.
             */
            result.contactName = job.contactName;
            result.contactPhone = job.contactPhone;
            result.contactCountryCode = job.contactCountryCode;
            result.contactPhoneNumber = job.contactPhoneNumber;
            result.address = job.address;
            result.logo = job.company.logo;
            result.notes = job.notes;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void removeJob(int id) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .removeJob(id)
                .enqueue(new Callback<ResponseObject<Job>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Job>> call,
                                           Response<ResponseObject<Job>> response) {
                        //
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            //
                            mUserActionsListener.fetchJobs();
                            //
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Job>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }
}