package com.hellobaytree.graftrs.shared.applications;

import android.app.Dialog;
import android.content.Context;

import com.hellobaytree.graftrs.shared.applications.model.Feedback;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.worker.jobmatches.model.Application;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2016 The Square Tech. All rights reserved.
 */

public class ApplicationsConnector {
    private Callback callback;

    public interface Callback {
        void onApplicationCancelled();
    }

    public ApplicationsConnector(Callback callback) {
        this.callback = callback;
    }

    public void cancelBooking(final Context context, int applicationId, String feedback) {
        if (context == null) {
            return;
        }
        final Dialog dialog = DialogBuilder.showCustomDialog(context);

        try {
            HttpRestServiceConsumer.getBaseApiClient()
                    .cancelBooking(applicationId, new Feedback(feedback))
                    .enqueue(new retrofit2.Callback<ResponseObject<Application>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<Application>> call,
                                               Response<ResponseObject<Application>> response) {

                            DialogBuilder.cancelDialog(dialog);
                            if (response.isSuccessful()) {
                                callback.onApplicationCancelled();
                            } else {
                                HandleErrors.parseError(context, dialog, response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<Application>> call, Throwable t) {
                            HandleErrors.parseFailureError(context, dialog, t);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
        }
    }
}
