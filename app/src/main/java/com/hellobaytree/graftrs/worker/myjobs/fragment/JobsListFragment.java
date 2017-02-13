package com.hellobaytree.graftrs.worker.myjobs.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.worker.jobdetails.JobDetailActivity;
import com.hellobaytree.graftrs.worker.jobmatches.model.Job;
import com.hellobaytree.graftrs.worker.myjobs.JobsContract;
import com.hellobaytree.graftrs.worker.myjobs.JobsPresenter;
import com.hellobaytree.graftrs.worker.myjobs.adapter.JobsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Evgheni on 11/3/2016.
 */

public class JobsListFragment extends Fragment
        implements JobsContract.View,
        JobsAdapter.JobsActionListener {

    public static final String TAG = "JobListFragment";
    private int jobType;
    private JobsPresenter presenter;
    private ProgressDialog progressDialog;
    private JobsAdapter jobsAdapter;
    private List<Job> jobs = new ArrayList<>();
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.no_matches)
    View noMatches;

    public static JobsListFragment newInstance(int jobType) {
        JobsListFragment jobsListFragment = new JobsListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", jobType);
        jobsListFragment.setArguments(bundle);
        return jobsListFragment;
    }

    @Override
    public void onViewDetails(Job job) {
        if (getActivity() == null || job == null) return;
        Intent intent = new Intent(getActivity(), JobDetailActivity.class);
        intent.putExtra(JobDetailActivity.JOB_ARG, job.id);
        startActivity(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jobType = getArguments().getInt("type");
        presenter = new JobsPresenter(getActivity(), this);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.worker_jobs_wait_msg));
        jobsAdapter = new JobsAdapter(jobs, getContext(), this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_worker_myjobs_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        jobsAdapter.registerAdapterDataObserver(observer);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(jobsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != presenter) presenter.init(jobType);
    }

    public void displayJobs(List<Job> data) {
        if (!jobs.isEmpty()) jobs.clear();
        jobs.addAll(data);
        jobsAdapter.notifyDataSetChanged();
    }

    public void displayError(String message) {
        new AlertDialog.Builder(getContext()).setMessage(message).create().show();
    }

    public void displayProgress(boolean show) {
        if (null != progressDialog) {
            if (show) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        }
    }

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (jobs.isEmpty()) {
                noMatches.setVisibility(View.VISIBLE);
            } else {
                noMatches.setVisibility(View.GONE);
            }
        }
    };
}
