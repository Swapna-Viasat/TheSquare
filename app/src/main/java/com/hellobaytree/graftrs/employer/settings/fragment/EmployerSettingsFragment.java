package com.hellobaytree.graftrs.employer.settings.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.payments.PaymentsActivity;
import com.hellobaytree.graftrs.employer.subscription.SubscriptionActivity;
import com.hellobaytree.graftrs.shared.settings.fragments.SettingsFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by maizaga on 2/1/17.
 *
 */

public class EmployerSettingsFragment extends SettingsFragment {

    public static EmployerSettingsFragment newInstance() {
        Bundle args = new Bundle();
        EmployerSettingsFragment fragment = new EmployerSettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view); 
        return view;
    }

    @OnClick({R.id.my_subscriptions, R.id.notify})
    public void onAction(View view) {
        switch (view.getId()) {
            case R.id.my_subscriptions:
                startActivity(new Intent(getActivity(), PaymentsActivity.class));
                break;
            case R.id.notify:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, EmployerSettingsNotifyFragment.newInstance())
                        .addToBackStack("notifications")
                        .commit();
                break;
            default:
                //
                break;

        }
    }
}
