package com.hellobaytree.graftrs.shared.settings.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hellobaytree.graftrs.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Evgheni on 11/17/2016.
 */

public class SettingsDocsFragment extends Fragment {

    public static SettingsDocsFragment newInstance() {
        SettingsDocsFragment settingsDocsFragment = new SettingsDocsFragment();
        return settingsDocsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_settings_docs, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.employer_settings_terms));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setTitle(getString(R.string.settings));
    }

    @OnClick({R.id.tc, R.id.pp})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.tc:
                Toast.makeText(getContext(), "tc", Toast.LENGTH_SHORT).show();
                break;
            case R.id.pp:
                Toast.makeText(getContext(), "pp", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
