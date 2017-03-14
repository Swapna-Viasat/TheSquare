package construction.thesquare.employer.myjobs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.employer.myjobs.fragment.WorkerProfileFragment;
import construction.thesquare.shared.utils.Constants;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2016 The Square Tech. All rights reserved.
 */

public class ViewWorkerProfileActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static final String WORKER_ID = "WORKER_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        ButterKnife.bind(this);

        setToolbar();

        Intent intent = getIntent();
        int workerId = intent.getIntExtra(WORKER_ID, 0);
        int jobId = intent.getIntExtra(Constants.KEY_JOB_ID, 0);

//        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
//            if (null != intent.getData()) {
//                Uri uri = intent.getData();
//                String one = uri.getQueryParameter("key");
//                Toast.makeText(this, String.valueOf(one), Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, "intent's data is null", Toast.LENGTH_LONG).show();
//            }
//        } else {
//            Toast.makeText(this, "intent action isn't view", Toast.LENGTH_LONG).show();
//        }

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container,
                    WorkerProfileFragment.newInstance(workerId, jobId));
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