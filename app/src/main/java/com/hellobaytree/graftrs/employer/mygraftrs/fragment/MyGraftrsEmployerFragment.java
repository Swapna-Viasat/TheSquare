package com.hellobaytree.graftrs.employer.mygraftrs.fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.mygraftrs.adapter.MyGraftrsEmployerPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyGraftrsEmployerFragment extends Fragment {

    @BindView(R.id.tabLayoutFragmentMyGraftrs)
    TabLayout tabLayout;
    @BindView(R.id.viewPagerFragmentMyGraftrs)
    ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_employer_my_graftrs, null);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.setAdapter(new MyGraftrsEmployerPagerAdapter(getChildFragmentManager(), getContext()));
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        tabLayout.setMinimumWidth(width);
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView textView = (TextView) ((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(0)).getChildAt(1);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_like_tab, 0);
        int density = (int) getResources().getDisplayMetrics().density;
        textView.setCompoundDrawablePadding(6 * density);
    }
}
