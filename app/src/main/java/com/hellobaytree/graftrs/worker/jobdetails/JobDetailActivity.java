package com.hellobaytree.graftrs.worker.jobdetails;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.hellobaytree.graftrs.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JobDetailActivity extends AppCompatActivity {

    public static final String JOB_ARG = "jobArgument";

    @BindView(R.id.toolbarJobDetail)
    Toolbar toolbar;

    private int jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        ButterKnife.bind(this);
        setToolbar();
        jobId = getIntent().getIntExtra(JOB_ARG, -1);
        if (jobId < 0) {
            finish();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                JobDetailsFragment.newInstance(jobId)).commit();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
            ab.setTitle(R.string.title_activity_job_detail);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
