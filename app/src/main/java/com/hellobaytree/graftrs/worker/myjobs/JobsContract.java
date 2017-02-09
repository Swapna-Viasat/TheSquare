package com.hellobaytree.graftrs.worker.myjobs;

import com.hellobaytree.graftrs.worker.jobmatches.model.Job;

import java.util.List;

/**
 * Created by Evgheni on 11/3/2016.
 */

public interface JobsContract {

    interface View {
        void displayJobs(List<Job> data);
        void displayError(String message);
        void displayProgress(boolean show);
    }

    interface UserActionsListener {
        void onShowDetails(Job job);
        void init();
    }
}
