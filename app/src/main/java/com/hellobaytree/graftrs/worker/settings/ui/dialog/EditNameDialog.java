/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package com.hellobaytree.graftrs.worker.settings.ui.dialog;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.utils.TextTools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditNameDialog extends DialogFragment {
    private static final String TAG = "EditNameDialog";

    @BindView(R.id.nameLayout)
    TextInputLayout nameLayout;
    @BindView(R.id.surnameLayout)
    TextInputLayout surnameLayout;

    private NameChangedListener listener;

    public interface NameChangedListener {
        void onNameChanged(String name, String surname);
    }

    public static EditNameDialog newInstance(NameChangedListener listener) {
        EditNameDialog dialog = new EditNameDialog();
        dialog.listener = listener;
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_edit_name, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @OnClick({R.id.done, R.id.cancel})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.done:
                if (validateData()) {
                    this.dismiss();
                    if (listener != null)
                        listener.onNameChanged(nameLayout.getEditText().getText().toString(), surnameLayout.getEditText().getText().toString());
                }
                break;
            case R.id.cancel:
                this.dismiss();
                break;
        }
    }

    private boolean validateData() {
        boolean result = true;
        if ((TextUtils.isEmpty(nameLayout.getEditText().getText().toString()))) {
            nameLayout.setError(getString(R.string.validate_first));
            result = false;
        } else if ((TextUtils.isEmpty(surnameLayout.getEditText().getText().toString()))) {
            surnameLayout.setError(getString(R.string.validate_last));
            result = false;
        }
        if (!result) resetInputErrors.start();
        return result;
    }

    private CountDownTimer resetInputErrors = new CountDownTimer(2000, 2000) {
        @Override
        public void onTick(long l) {
        }

        @Override
        public void onFinish() {
            try {
                TextTools.resetInputLayout(nameLayout);
                TextTools.resetInputLayout(surnameLayout);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        ViewGroup.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes((WindowManager.LayoutParams) params);
        super.onResume();
    }
}
