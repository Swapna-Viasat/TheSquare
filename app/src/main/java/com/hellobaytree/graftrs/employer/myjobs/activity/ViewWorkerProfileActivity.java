package com.hellobaytree.graftrs.employer.myjobs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.myjobs.fragment.WorkerProfileFragment;
import com.hellobaytree.graftrs.shared.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2016 The Square Tech. All rights reserved.
 */

public class ViewWorkerProfileActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    public static final String WORKER_ID = "WORKER_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        ButterKnife.bind(this);

        setToolbar();

        Intent intent = getIntent();
        int workerId = intent.getIntExtra(WORKER_ID, 0);
        int applicationId = intent.getIntExtra(Constants.KEY_APPLICATION_ID, 0);
        boolean hasApplied = intent.getBooleanExtra(Constants.KEY_HAS_APPLIED, false);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container,
                    WorkerProfileFragment.newInstance(workerId, applicationId, hasApplied));
            fragmentTransaction.commit();
        }
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.view_profile);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
