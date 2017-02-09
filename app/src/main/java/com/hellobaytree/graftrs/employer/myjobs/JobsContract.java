package com.hellobaytree.graftrs.employer.myjobs;

import com.hellobaytree.graftrs.shared.models.Job;

import java.util.List;

/**
 * Created by gherg on 12/30/2016.
 */

public interface JobsContract {

    interface View {
        void showProgress(boolean show);
        void displayJobs(List<Job> data);
    }

    interface UserActionsListener {
        void fetchJobs();
    }
}