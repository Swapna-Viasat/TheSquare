package com.hellobaytree.graftrs.worker.jobmatches;

import android.content.Context;
import android.content.Intent;

import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.data.persistence.SharedPreferencesManager;
import com.hellobaytree.graftrs.shared.models.Worker;
import com.hellobaytree.graftrs.worker.jobdetails.JobDetailActivity;
import com.hellobaytree.graftrs.worker.jobdetails.LikeJobConnector;
import com.hellobaytree.graftrs.worker.jobmatches.model.Job;
import com.hellobaytree.graftrs.worker.jobmatches.model.MatchesResponse;
import com.hellobaytree.graftrs.worker.jobmatches.model.Ordering;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Evgheni on 11/1/2016.
 */

public class MatchesPresenter implements MatchesContract.UserActionListener, LikeJobConnector.Callback {

    private final MatchesContract.View mMatchesView;
    private LikeJobConnector likeJobConnector;
    private Ordering currentOrdering;
    private Integer currentCommuteTime;

    public MatchesPresenter(MatchesContract.View view) {
        this.mMatchesView = view;
        likeJobConnector = new LikeJobConnector(this);
    }

    @Override
    public void onShowDetails(Context context, Job job) {
        if (context == null || job == null) return;
        Intent intent = new Intent(context, JobDetailActivity.class);
        intent.putExtra(JobDetailActivity.JOB_ARG, job.id);
        context.startActivity(intent);
    }

    @Override
    public void onLikeJobClick(Context context, Job job) {
        if (job == null) return;

        if (job.liked) likeJobConnector.unlikeJob(context, job.id);
        else likeJobConnector.likeJob(context, job.id);
    }

    @Override
    public void fetchJobMatches() {
        mMatchesView.displayProgress(true);
        Call<MatchesResponse> call = HttpRestServiceConsumer.getBaseApiClient()
                .getJobMatches(currentOrdering, currentCommuteTime);
        call.enqueue(new Callback<MatchesResponse>() {
            @Override
            public void onResponse(Call<MatchesResponse> call, Response<MatchesResponse> response) {
                mMatchesView.displayProgress(false);
                if (null != response) {
                    if (null != response.body()) {
                        if (null != response.body().response) {
                            mMatchesView.displayMatches(response.body().response);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MatchesResponse> call, Throwable t) {
                mMatchesView.displayProgress(false);
                t.printStackTrace();
                mMatchesView.displayError((null != t.getMessage()) ? t.getMessage() : "Something went wrong");
            }
        });
    }

    @Override
    public void setMatchesFilters(Ordering ordering, int commuteTime) {
        currentOrdering = ordering;
        currentCommuteTime = commuteTime;
    }

    @Override
    public Ordering getOrdering() {
        return currentOrdering;
    }

    @Override
    public void fetchMe(Context context) {
        if (context == null) return;
        List<String> requiredFields = Arrays.asList("onboarding_skipped");
        HttpRestServiceConsumer.getBaseApiClient()
                .getFilteredWorker(SharedPreferencesManager.getInstance(context).getWorkerId(), requiredFields)
                .enqueue(new Callback<ResponseObject<Worker>>() {
            @Override
            public void onResponse(Call<ResponseObject<Worker>> call,
                                   Response<ResponseObject<Worker>> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body().getResponse() != null)
                            mMatchesView.displayHint(response.body().getResponse().onboardingSkipped);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                mMatchesView.displayError((null != t.getMessage()) ? t.getMessage() : "Something went wrong");
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onConnectorSuccess() {
        fetchJobMatches();
    }
}
