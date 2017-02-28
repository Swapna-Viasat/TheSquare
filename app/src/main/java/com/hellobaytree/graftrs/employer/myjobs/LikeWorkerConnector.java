package com.hellobaytree.graftrs.employer.myjobs;

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
 * Copyright (c) 2017 The Square Tech. All rights reserved.
 */

public class LikeWorkerConnector {
    private Callback callback;

    public interface Callback {
        void onConnectorSuccess();
    }

    public LikeWorkerConnector(Callback callback) {
        this.callback = callback;
    }

    public void likeWorker(final Context context, int workerId) {
        if (context == null) {
            return;
        }
        final Dialog dialog = DialogBuilder.showCustomDialog(context);

        try {
            HttpRestServiceConsumer.getBaseApiClient()
                    .likeWorker(workerId)
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

    public void unlikeWorker(final Context context, int workerId) {
        if (context == null) {
            return;
        }
        final Dialog dialog = DialogBuilder.showCustomDialog(context);

        try {
            HttpRestServiceConsumer.getBaseApiClient()
                    .unlikeWorker(workerId)
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

