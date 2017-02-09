package com.hellobaytree.graftrs.worker.signup.datasource;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.data.model.SMSSent;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.worker.signup.model.WorkerVerify;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by juanmaggi on 25/7/16.
 */
public class SignUpWorkerServiceConsumerImpl implements SignUpWorkerServiceConsumer {

    private Dialog dialog;
    private Context context;

    public void verifyWorkerNumber(Context aContext, HashMap<String, String> verificationRequest, final OnVerificationWorkerNumberFinishedListener listener) {
        context = aContext;
        dialog = DialogBuilder.showCustomDialog(context);
        Call<ResponseObject<WorkerVerify>> workerVerify = HttpRestServiceConsumer.getBaseApiClient().verifyWorkerNumber(verificationRequest);
        workerVerify.enqueue(new Callback<ResponseObject<WorkerVerify>>() {
            @Override
            public void onResponse(Call<ResponseObject<WorkerVerify>> profile, Response<ResponseObject<WorkerVerify>> aResponse) {
                final Response<ResponseObject<WorkerVerify>> response = aResponse;
                if (response.isSuccessful()) {
                    AlertDialog.Builder builder = DialogBuilder.getStandardDialog(context,
                            context.getResources().getString(R.string.pop_up_title_great), context.getResources().getString(R.string.pop_up_message_successful_registration));
                    builder.setPositiveButton(context.getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface aDialog, int id) {
                            listener.onPostVerificationNumberWorkerSuccess(dialog, response.body().getResponse());
                        }
                    });
                    DialogBuilder.afterShowSetProperties(builder, context);
                } else HandleErrors.parseError(context, dialog, response);
            }

            @Override
            public void onFailure(Call<ResponseObject<WorkerVerify>> profile, Throwable t) {
                HandleErrors.parseFailureError(context, dialog, t);
            }
        });
    }

    public void resendSMSWorker(Context aContext, HashMap<String, String> resendSMSRequest,
                                final OnSMSWorkerResendFinishedListener listener) {
        context = aContext;
        dialog = DialogBuilder.showCustomDialog(context);
        Call<ResponseObject<SMSSent>> smsSent = HttpRestServiceConsumer.getBaseApiClient().resendSMSWorker(resendSMSRequest);
        smsSent.enqueue(new Callback<ResponseObject<SMSSent>>() {
            @Override
            public void onResponse(Call<ResponseObject<SMSSent>> smsSent, Response<ResponseObject<SMSSent>> response) {
                if (response.isSuccessful())
                    listener.onSMSSentWorkerSuccess(dialog, response.body().getResponse());
                else HandleErrors.parseError(context, dialog, response);
            }

            @Override
            public void onFailure(Call<ResponseObject<SMSSent>> logout, Throwable t) {
                HandleErrors.parseFailureError(context, dialog, t);
            }
        });
    }

}