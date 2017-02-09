package com.hellobaytree.graftrs.worker.myjobs;

import android.content.Context;

import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.persistence.SharedPreferencesManager;
import com.hellobaytree.graftrs.worker.jobmatches.model.Job;
import com.hellobaytree.graftrs.worker.myjobs.model.JobsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Evgheni on 11/3/2016.
 */

public class JobsPresenter implements JobsContract.UserActionsListener {

    private JobsContract.View mJobsView;
    private Context context;

    public JobsPresenter(Context context, JobsContract.View mJobsView) {
        this.mJobsView = mJobsView;
        this.context = context;
    }

    @Override
    public void init() {
        mJobsView.displayProgress(true);
        int id = SharedPreferencesManager.getInstance(context).loadSessionInfoWorker().getUserId();
        Call<JobsResponse> call = HttpRestServiceConsumer.getBaseApiClient().getMyJobs(id);
        call.enqueue(new Callback<JobsResponse>() {
            @Override
            public void onResponse(Call<JobsResponse> call, Response<JobsResponse> response) {
                mJobsView.displayProgress(false);
                if (null != response) {
                    if (null != response.body()) {
                        mJobsView.displayJobs(response.body().response);
                    }
                }
            }

            @Override
            public void onFailure(Call<JobsResponse> call, Throwable t) {
                mJobsView.displayProgress(false);
            }
        });
    }

    @Override
    public void onShowDetails(Job job) {
        //
    }
}
