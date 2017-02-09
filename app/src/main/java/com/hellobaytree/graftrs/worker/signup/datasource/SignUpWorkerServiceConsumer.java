package com.hellobaytree.graftrs.worker.signup.datasource;

import android.app.Dialog;
import android.content.Context;

import com.hellobaytree.graftrs.shared.data.model.SMSSent;
import com.hellobaytree.graftrs.worker.signup.model.WorkerVerify;

import java.util.HashMap;

/**
 * Created by juanmaggi on 25/7/16.
 */
public interface SignUpWorkerServiceConsumer {
    interface OnVerificationWorkerNumberFinishedListener {
        void onPostVerificationNumberWorkerSuccess(Dialog dialog, WorkerVerify workerVerify);
    }

    void verifyWorkerNumber(Context aContext, HashMap<String, String> verificationRequest,
                            OnVerificationWorkerNumberFinishedListener listener);

    interface OnSMSWorkerResendFinishedListener {
        void onSMSSentWorkerSuccess(Dialog dialog, SMSSent smsSent);
    }

    void resendSMSWorker(Context aContext, HashMap<String, String> resendSMSRequest, OnSMSWorkerResendFinishedListener listener);
}
