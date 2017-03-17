/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package construction.thesquare.shared.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.SocketTimeoutException;

import construction.thesquare.shared.data.model.ResponseError;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.start.activity.StartActivity;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by Juan on 11/12/2015.
 */
public class HandleErrors {

    private static final String TAG = "HandleErrors";

    public static void parseError(Context context, Dialog dialog, Response<?> response) {
        if (dialog != null)
            DialogBuilder.cancelDialog(dialog);
        Converter<ResponseBody, ResponseError> converter =
                construction.thesquare.shared.data.HttpRestServiceConsumer.getRetrofitInstance()
                        .responseBodyConverter(ResponseError.class, new Annotation[0]);
        ResponseError responseError = null;
        try {
            responseError = converter.convert(response.errorBody());
            TextTools.log(TAG, "Response error: " + responseError.getError().getMessage());

            if (responseError.getError().getMessage().contains("Invalid token")) {
                DialogBuilder.showStandardDialog(context, "Error", responseError.getError().getMessage(),
                        onOkClickCallback);
            } else {
                DialogBuilder.showStandardDialog(context, "Error", responseError.getError().getMessage());
            }

        } catch (IOException exception) {
            TextTools.log(TAG, "Error sin parsear catch: " + response.errorBody().toString());
            DialogBuilder.showStandardDialog(context, "Error", exception.getMessage());
        }
    }

    public static void parseError(Context context, Dialog dialog,
                                  Response<?> response,
                                  final DialogInterface.OnClickListener gotoPaymentListener,
                                  final DialogInterface.OnClickListener listener) {
        if (dialog != null) {
            Log.d(TAG, String.valueOf(dialog.hashCode()));
            DialogBuilder.cancelDialog(dialog);
        }

        Converter<ResponseBody, ResponseError> converter =
                construction.thesquare.shared.data
                        .HttpRestServiceConsumer.getRetrofitInstance()
                        .responseBodyConverter(ResponseError.class, new Annotation[0]);
        ResponseError responseError;

        try {
            responseError = converter.convert(response.errorBody());
            TextTools.log(TAG, "Error sin parsear try after assing: "
                    + responseError.getError().getMessage());

            if (responseError.getError().getMessage().contains("Invalid token")) {
                DialogBuilder.showStandardDialog(context, "Error",
                        responseError.getError().getMessage(),
                        onOkClickCallback);
            } else if (responseError.getError().code == 102) {
                //
                DialogBuilder.showStandardDialog(context, "Error",
                        responseError.getError().getMessage(), listener);
            } else if (responseError.getError().code == 101) {
                // no active subscription
                DialogBuilder.showStandardDialog(context, "Error",
                        responseError.getError().getMessage(), gotoPaymentListener);
                //
            } else if (responseError.getError().getMessage().contains("We already have email address")) {
                DialogBuilder.showStandardDialog(context, "Error",
                        responseError.getError().getMessage(), listener);
            } else {
                DialogBuilder.showStandardDialog(context, "Error", responseError.getError().getMessage());
            }

        } catch (IOException exception) {
            TextTools.log(TAG, "Response error: " + response.errorBody().toString());
            DialogBuilder.showStandardDialog(context, "Error", exception.getMessage());
        }
    }

    public static void parseFailureError(Context context, Dialog dialog, Throwable throwable) {
        String error = "Unexpected Error";
        if (dialog != null)
            DialogBuilder.cancelDialog(dialog);
        if (throwable instanceof IOException)
            error = "Oops!\nThe network connection\nwas lost.";

        if (throwable instanceof SocketTimeoutException)
            error = "Connection Time Out";

        DialogBuilder.showStandardDialog(context, "", error);
    }

    private static DialogBuilder.OnClickStandardDialog onOkClickCallback = new DialogBuilder.OnClickStandardDialog() {
        @Override
        public void onOKClickStandardDialog(Context context) {
            SharedPreferencesManager.getInstance(context).deleteToken();
            if (SharedPreferencesManager.getInstance(context).loadSessionInfoWorker().getUserId() > 0)
                SharedPreferencesManager.getInstance(context).deleteSessionInfoWorker();
            else if (SharedPreferencesManager.getInstance(context).loadSessionInfoEmployer().getUserId() > 0)
                SharedPreferencesManager.getInstance(context).deleteSessionInfoEmployer();

            Intent intent = new Intent(context, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    };

}
