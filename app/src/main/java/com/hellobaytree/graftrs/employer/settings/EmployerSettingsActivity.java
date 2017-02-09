package com.hellobaytree.graftrs.employer.settings;

import android.os.Bundle;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.settings.fragment.EmployerSettingsFragment;
import com.hellobaytree.graftrs.shared.settings.SettingsActivity;

/**
 * Created by maizaga on 2/1/17.
 *
 */

public class EmployerSettingsActivity extends SettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame, EmployerSettingsFragment.newInstance())
                .commit();
    }
}
