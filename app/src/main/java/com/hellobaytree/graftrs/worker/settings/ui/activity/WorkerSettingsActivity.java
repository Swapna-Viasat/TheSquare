package com.hellobaytree.graftrs.worker.settings.ui.activity;

import android.os.Bundle;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.settings.SettingsActivity;
import com.hellobaytree.graftrs.worker.settings.ui.fragments.WorkerSettingsFragment;

/**
 * Created by maizaga on 2/1/17.
 *
 */

public class WorkerSettingsActivity extends SettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame, WorkerSettingsFragment.newInstance())
                .commit();
    }
}
