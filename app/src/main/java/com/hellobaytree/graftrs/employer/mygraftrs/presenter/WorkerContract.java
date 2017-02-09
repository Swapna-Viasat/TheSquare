package com.hellobaytree.graftrs.employer.mygraftrs.presenter;

import com.hellobaytree.graftrs.employer.mygraftrs.model.Worker;

import java.util.List;

/**
 * Created by Evgheni on 10/21/2016.
 */

public interface WorkerContract {

    interface View {
        void showWorkerList(List<Worker> data);
        void showEmptyList();
        void showProgress(boolean show);
        void showError(String message);
    }

    interface UserActionsListener {
        void fetchWorkers(int id);
        void viewWorkerDetails();
        void quickInvite();
    }
}
