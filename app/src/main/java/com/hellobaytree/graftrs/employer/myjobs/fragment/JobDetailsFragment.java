package com.hellobaytree.graftrs.employer.myjobs.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.MainEmployerActivity;
import com.hellobaytree.graftrs.employer.myjobs.adapter.JobDetailsPagerAdapter;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.models.Job;
import com.hellobaytree.graftrs.shared.utils.Constants;
import com.hellobaytree.graftrs.shared.utils.DateUtils;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/30/2016.
 */

public class JobDetailsFragment extends Fragment {

    public static final String TAG = "JobDetailsFragment";

    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;
    // job details
    @BindView(R.id.item_job_location) JosefinSansTextView location;
    @BindView(R.id.item_job_start_date) JosefinSansTextView startDate;
    @BindView(R.id.item_job_salary_period) JosefinSansTextView payPeriod;
    @BindView(R.id.item_job_salary_number) JosefinSansTextView payNumber;
    @BindView(R.id.item_job_occupation) JosefinSansTextView occupation;
    @BindView(R.id.item_job_experience) JosefinSansTextView experience;
    @BindView(R.id.item_job_logo) ImageView logo;

    private JobDetailsPagerAdapter adapter;

    public static JobDetailsFragment newInstance(int id) {
        JobDetailsFragment fragment = new JobDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_JOB_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new JobDetailsPagerAdapter(getContext(),
                getChildFragmentManager(),
                getArguments().getInt(Constants.KEY_JOB_ID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create_job_post_step, menu);
        int positionOfMenuItem = 0;
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString("Cancel");
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
        item.setTitle(s);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.createJobCancel:
                Intent intent = new Intent(getActivity(), MainEmployerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        fetchInfo(getArguments().getInt(Constants.KEY_JOB_ID));
    }

    private void fetchInfo(int id) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchJob(id)
                .enqueue(new Callback<ResponseObject<Job>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Job>> call,
                                           Response<ResponseObject<Job>> response) {
                        
                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful()) {


                            populate(response.body().getResponse());

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

    private void populate(Job job) {

        if (job.status.id == Job.TAB_LIVE) {
            viewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(1);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setCurrentItem(2);
        }

        if (null != job.role) {
            occupation.setText(job.role.name);
        }

        if (null != job.address) {
            location.setText(job.address);
        }

        payNumber.setText(String.valueOf(job.budget));

        if (null != job.budgetType) {
            payPeriod.setText(job.budgetType.name);
        }

        experience.setText(String
                .format(getString(R.string.employer_jobs_experience),
                        job.experience,
                        getResources()
                                .getQuantityString(R.plurals.year_plural,
                                        job.experience)));

        if (null != job.company) {
            if (null != job.company.logo) {
                logo.setVisibility(View.VISIBLE);
                Picasso.with(getContext())
                        .load(job.company.logo)
                        .into(logo);
            }
        }

        if (null != job.start) {
            try {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                Date date = simpleDateFormat.parse(job.start);
                calendar.setTime(date);
                String startString =
                        String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) +
                        DateUtils.suffix(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                        DateUtils.monthShort(calendar.get(Calendar.MONTH));
                startDate.setText(String.format(getResources()
                        .getString(R.string.employer_jobs_starts), startString));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}