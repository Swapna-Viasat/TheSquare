package com.hellobaytree.graftrs.employer.myjobs.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.createjob.CreateJobActivity;
import com.hellobaytree.graftrs.employer.createjob.CreateRequest;
import com.hellobaytree.graftrs.employer.createjob.PreviewJobActivity;
import com.hellobaytree.graftrs.employer.myjobs.adapter.JobsPagerAdapter;
import com.hellobaytree.graftrs.employer.myjobs.dialog.CreateJobDialog;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.response.EmployerJobResponse;
import com.hellobaytree.graftrs.shared.models.DataResponse;
import com.hellobaytree.graftrs.shared.models.Job;
import com.hellobaytree.graftrs.shared.models.Skill;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by juanmaggi on 13/6/16.
 */
public class JobsFragment extends Fragment
        implements CreateJobDialog.CreateJobDialogListener {

    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;

    private CreateJobDialog dialog;

    public static JobsFragment getInstance() {
        JobsFragment fragment = new JobsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_employer_jobs, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle("My Jobs");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.setAdapter(new JobsPagerAdapter(getContext(),
                getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar_employer, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.createEmployerJob:
                // create();
                fetchJobs();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCreateDialog(List<Job> jobs) {
        dialog = CreateJobDialog.newInstance(this, jobs);
        dialog.setCancelable(false);
        // not sure why but apparently some people might minimize the app by this time
        // so must watch out for an illegal state exception
        // as proven by
        // https://fabric.io/evgheni-gherghelejius-projects/android/apps/com.hellobaytree.graftrs.graftrs/issues/5876768e0aeb16625b0025ff
        try {
            dialog.show(getChildFragmentManager(), "hello");
        } catch (IllegalStateException e) {
            dialog = null;
            e.printStackTrace();
        }
    }

    public void onCancel() {
        dialog.dismiss();
    }

    public void onCreateNew(Job job) {
        dialog.dismiss();
        create();
    }

    public void onDuplicate(Job job) {
        dialog.dismiss();
        //Toast.makeText(getContext(), "duplicating " + job.role.name, Toast.LENGTH_SHORT).show();
        if (null != prepareJobForRepublishing(job)) {
            Intent intent = new Intent(getActivity(), PreviewJobActivity.class);
            intent.putExtra("request", prepareJobForRepublishing(job));
            startActivity(intent);
        }
    }

    private CreateRequest prepareJobForRepublishing(Job job) {
        try {
            CreateRequest result = new CreateRequest();
            //
            result.roleName = job.role.name;
            result.role = job.role.id;
            result.roleObject = job.role;
            result.experience = job.experience;
            result.budget = job.budget;
            result.budgetType = job.budgetType.id;
            result.location = job.location;
            result.locationName = job.locationName;
            result.contactName = job.contactName;
            result.contactPhone = job.contactPhone;
            result.contactCountryCode = job.contactCountryCode;
            result.contactPhoneNumber = job.contactPhoneNumber;
            result.address = job.address;
            result.description = job.description;
            result.english = job.english;
            result.overtime = job.payOvertime;
            result.overtimeValue = job.overtimeRate;
            String englishString = "Basic";
            switch (job.english) {
                case 2:
                    englishString = "Fluent";
                    break;
                case 3:
                    englishString = "Native";
                    break;
            }
            result.englishLevelString = englishString;

            /**
             * Loading Trades!
             */
            if (null != job.trades) {
                if (!job.trades.isEmpty()) {
                    result.tradeObjects = job.trades;
                    int[] tradeIds = new int[job.trades.size()];
                    List<String> tradeStrings = new ArrayList<>();
                    for (int i = 0; i < job.trades.size(); i++) {
                        tradeIds[i] = job.trades.get(i).id;
                        tradeStrings.add(job.trades.get(i).name);
                    }
                    result.trades = tradeIds;
                    result.tradeStrings = tradeStrings;
                }
            }

            /**
             * Loading qualifications!
             */
            if (null != job.qualifications) {
                if (!job.qualifications.isEmpty()) {
                    result.qualificationObjects = job.qualifications;
                    int[] qualificationIds = new int[job.qualifications.size()];
                    List<String> qualificationStrings = new ArrayList<>();
                    for (int i = 0; i < job.qualifications.size(); i++) {
                        qualificationIds[i] = job.qualifications.get(i).id;
                        qualificationStrings.add(job.qualifications.get(i).name);
                    }
                    result.qualifications = qualificationIds;
                    result.qualificationStrings = qualificationStrings;
                }
            }

            /**
             * Loading skills!
             */
            if (null != job.skills) {
                if (!job.skills.isEmpty()) {
                    int[] skillIds = new int[job.skills.size()];
                    List<String> skillNames = new ArrayList<>();
                    for (int i = 0; i < job.skills.size(); i++) {
                        skillIds[i] = job.skills.get(i).id;
                        skillNames.add(job.skills.get(i).name);
                    }
                    result.skills = skillIds;
                    result.skillStrings = skillNames;
                }
            }

            /**
             * Loading experience.
             */
            // TODO: what happened with experience qualifications ???

            /**
             * Loading experience types!
             */
            if (null != job.experienceTypes) {
                if (!job.experienceTypes.isEmpty()) {
                    result.experienceTypeObjects = job.experienceTypes;
                    int[] experienceTypeIds = new int[job.experienceTypes.size()];
                    List<String> experienceTypeNames = new ArrayList<>();
                    for (int i = 0; i < job.experienceTypes.size(); i++) {
                        experienceTypeIds[i] = job.experienceTypes.get(i).id;
                        experienceTypeNames.add(job.experienceTypes.get(i).name);
                    }
                    result.experienceTypes = experienceTypeIds;
                    result.experienceTypeStrings = experienceTypeNames;
                }
            }

            /**
             * Loading logo.
             */
            if (null != job.owner) {
                if (null != job.owner.picture) {
                    result.logo = job.owner.picture;
                }
            }

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date = format.parse(job.start);
            calendar.setTime(date);
            String startDate = calendar.get(Calendar.YEAR) + "-" +
                    String.valueOf(calendar.get(Calendar.MONTH) + 1) + "-" +
                    calendar.get(Calendar.DAY_OF_MONTH);
            String startTime = calendar.get(Calendar.HOUR) + ":" +
                    calendar.get(Calendar.MINUTE) + ":" + "00";
            result.date = startDate;
            result.time = startTime;

            /**
             *
             */

            //
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void fetchJobs() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchJobs()
                .enqueue(new Callback<EmployerJobResponse>() {
                    @Override
                    public void onResponse(Call<EmployerJobResponse> call,
                                           Response<EmployerJobResponse> response) {
                        //
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);

                            if (response.body().response.isEmpty()) {
                                create();
                            } else {
                                showCreateDialog(response.body().response);
                            }

                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<EmployerJobResponse> call, Throwable t) {
                        //
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void create() {
        Intent intent = new Intent(getActivity(), CreateJobActivity.class);
        getActivity().startActivity(intent);
    }
}