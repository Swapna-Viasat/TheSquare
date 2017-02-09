package com.hellobaytree.graftrs.worker.myjobs.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.worker.jobmatches.model.Job;
import com.hellobaytree.graftrs.worker.myjobs.fragment.JobsListFragment;

/**
 * Created by Evgheni on 11/3/2016.
 */

public class JobsPagerAdapter extends FragmentPagerAdapter {

    private static final int JOBS_COUNT = 5;
    private Context context;

    public JobsPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.worker_jobs_booked);
            case 1:
                return context.getResources().getString(R.string.worker_jobs_offers);
            case 2:
                return context.getResources().getString(R.string.worker_jobs_applications);
            case 3:
                return context.getResources().getString(R.string.worker_jobs_liked);
            case 4:
                return context.getResources().getString(R.string.worker_jobs_completed);
        }
        return null;
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return JobsListFragment.newInstance(Job.TYPE_BOOKED);
            case 1:
                return JobsListFragment.newInstance(Job.TYPE_OFFER);
            case 2:
                return JobsListFragment.newInstance(Job.TYPE_APP);
            case 3:
                return JobsListFragment.newInstance(Job.TYPE_LIKED);
            case 4:
                return JobsListFragment.newInstance(Job.TYPE_COMPLETED);
        }
        return null;
    }

    @Override
    public int getCount() {
        return JOBS_COUNT;
    }
}