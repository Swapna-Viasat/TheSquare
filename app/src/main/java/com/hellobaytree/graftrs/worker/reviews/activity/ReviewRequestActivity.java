package com.hellobaytree.graftrs.worker.reviews.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansEditText;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Evgheni on 11/14/2016.
 */

public class ReviewRequestActivity extends AppCompatActivity {

    public static final String TAG = "ReviewRequestActivity";
    @BindView(R.id.get_first)
    JosefinSansEditText firstName;
    @BindView(R.id.get_last)
    JosefinSansEditText lastName;
    @BindView(R.id.get_company)
    JosefinSansEditText company;
    @BindView(R.id.get_date)
    JosefinSansEditText date;
    @BindView(R.id.get_email)
    JosefinSansEditText email;
    @BindView(R.id.get_mobile)
    JosefinSansEditText mobile;
    @BindView(R.id.err)
    JosefinSansTextView error;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_review);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.close, R.id.cancel, R.id.request})
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.request:
                if (validate()) {
                    final Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.dialog_reference_request);
                    dialog.setCancelable(false); dialog.show();
                    dialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    break;
                }
                break;
            case R.id.close:
                finish();
                break;
        }
    }

    private boolean validate() {
        if (TextUtils.isEmpty(firstName.getText().toString())) {
            firstName.setError(getString(R.string.worker_request_field));
            return false;
        }
        if (TextUtils.isEmpty(lastName.getText().toString())) {
            lastName.setError(getString(R.string.worker_request_field));
            return false;
        }
        if (TextUtils.isEmpty(date.getText().toString())) {
            date.setError(getString(R.string.worker_request_field));
            return false;
        }
        if (TextUtils.isEmpty(company.getText().toString())) {
            company.setError(getString(R.string.worker_request_field));
            return false;
        }
        if (TextUtils.isEmpty(mobile.getText().toString()) &&
                TextUtils.isEmpty(email.getText().toString())) {
            error.setVisibility(View.VISIBLE);
            return false;
        } else {
            error.setVisibility(View.GONE);
        }
        return true;
    }
}