package com.hellobaytree.graftrs.worker.jobdetails;

import android.app.Dialog;
import android.content.Context;

import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.models.StatusMessageResponse;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2016 FusionWorks. All rights reserved.
 */

public class LikeJobConnector {
    private Callback callback;

    public interface Callback {
        void onConnectorSuccess();
    }

    public LikeJobConnector(Callback callback) {
        this.callback = callback;
    }

    public void likeJob(final Context context, int jobId) {
        if (context == null) {
            return;
        }
        final Dialog dialog = DialogBuilder.showCustomDialog(context);

        try {
            HttpRestServiceConsumer.getBaseApiClient()
                    .likeJob(jobId)
                    .enqueue(new retrofit2.Callback<ResponseObject<StatusMessageResponse>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<StatusMessageResponse>> call,
                                               Response<ResponseObject<StatusMessageResponse>> response) {

                            DialogBuilder.cancelDialog(dialog);
                            if (response.isSuccessful()) {
                                callback.onConnectorSuccess();
                            } else {
                                HandleErrors.parseError(context, dialog, response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<StatusMessageResponse>> call, Throwable t) {
                            HandleErrors.parseFailureError(context, dialog, t);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
        }
    }

    public void unlikeJob(final Context context, int jobId) {
        if (context == null) {
            return;
        }
        final Dialog dialog = DialogBuilder.showCustomDialog(context);

        try {
            HttpRestServiceConsumer.getBaseApiClient()
                    .unlikeJob(jobId)
                    .enqueue(new retrofit2.Callback<ResponseObject<StatusMessageResponse>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<StatusMessageResponse>> call,
                                               Response<ResponseObject<StatusMessageResponse>> response) {

                            DialogBuilder.cancelDialog(dialog);
                            if (response.isSuccessful()) {
                                callback.onConnectorSuccess();
                            } else {
                                HandleErrors.parseError(context, dialog, response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<StatusMessageResponse>> call, Throwable t) {
                            HandleErrors.parseFailureError(context, dialog, t);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
        }
    }
}
