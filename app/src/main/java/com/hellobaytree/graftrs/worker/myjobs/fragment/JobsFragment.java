package com.hellobaytree.graftrs.worker.myjobs.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.worker.myjobs.adapter.JobsPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Evgheni on 11/3/2016.
 */

public class JobsFragment extends Fragment {

    public static final String TAG = "JobsFragment";
    private int lastPosition;
    @BindView(R.id.worker_jobs_tablayout) TabLayout tabLayout;
    @BindView(R.id.worker_jobs_pager) ViewPager viewPager;
    @BindView(R.id.a) HorizontalScrollView tabScroll;

    public static JobsFragment newInstance() {
        return new JobsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_worker_jobs, container, false);
        ButterKnife.bind(this, view); return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.setAdapter(new JobsPagerAdapter(getActivity(), getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(5);
        viewPager.addOnPageChangeListener(listener);
    }

    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if ((position - lastPosition) > 0) {
                tabScroll.smoothScrollBy(tabLayout.getWidth() / 3, 0);
            } else {
                tabScroll.smoothScrollBy(-tabLayout.getWidth() / 3, 0);
            }
            lastPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}