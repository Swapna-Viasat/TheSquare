package com.hellobaytree.graftrs.employer;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.account.AccountFragment;
import com.hellobaytree.graftrs.employer.createjob.CreateJobActivity;
import com.hellobaytree.graftrs.employer.mygraftrs.fragment.MyGraftrsEmployerFragment;
import com.hellobaytree.graftrs.employer.myjobs.fragment.JobsFragment;
import com.hellobaytree.graftrs.employer.onboarding.OnboardingEmployerActivity;
import com.hellobaytree.graftrs.shared.data.persistence.SharedPreferencesManager;
import com.hellobaytree.graftrs.shared.main.activity.MainActivity;
import com.hellobaytree.graftrs.shared.utils.Constants;
import com.hellobaytree.graftrs.shared.utils.TextTools;

import butterknife.ButterKnife;

/**
 * Created by juanmaggi on 10/6/16.
 *
 * Refactored by Evgheni Gherghelejiu throughout January 2017
 */
public class MainEmployerActivity extends AppCompatActivity {

    public static final String TAG = "MainEmployer";

    private int lastTab;

    private DrawerLayout drawerEmployerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_employer);
        ButterKnife.bind(this); setToolbar();


        // some UI setup from previous dev
        drawerEmployerLayout = (DrawerLayout) findViewById(R.id.drawer_employer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_employer_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        // checking if the employer wasn't in the process of creating a job
        // when last left the app
        if (getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .getBoolean(Constants.KEY_UNFINISHED, false)) {
            ///
            Log.d(TAG, "resume create job flow");
            startActivity(new Intent(this, CreateJobActivity.class));
            ///
        } else {
            int currentTab =
            getSharedPreferences(Constants.EMPLOYER, MODE_PRIVATE)
                    .getInt(Constants.EMPLOYER_CURRENT_TAB, 0);

            switch (currentTab) {
                case 0:
                    selectItem(getString(R.string.menu_employer_my_jobs_home),
                            navigationView.getMenu().getItem(0));
                    break;
                case 1:
                    selectItem(getString(R.string.menu_employer_my_graftrs),
                            navigationView.getMenu().getItem(1));
                    break;
                case 2:
                    selectItem(getString(R.string.menu_employer_my_account),
                            navigationView.getMenu().getItem(2));
                    break;
            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        getSharedPreferences(Constants.EMPLOYER, MODE_PRIVATE)
                .edit()
                .putInt(Constants.EMPLOYER_CURRENT_TAB, lastTab)
                .commit();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEmployer);
        setSupportActionBar(toolbar);
        // find the title text view
        TextView toolbarTitle;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View child = toolbar.getChildAt(i);
            if (child instanceof TextView) {
                toolbarTitle = (TextView) child;
                // set my custom font
                Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/JosefinSans-Italic.ttf");
                toolbarTitle.setTypeface(typeface);
                break;
            }
        }

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            final Drawable menu = ContextCompat.getDrawable(this, R.drawable.ic_menu_black_24dp);
            menu.setColorFilter(ContextCompat.getColor(this, R.color.redSquareColor), PorterDuff.Mode.SRC_ATOP);
            ab.setHomeAsUpIndicator(menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        String title = menuItem.getTitle().toString();
                        selectItem(title, menuItem);
                        return true;
                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerEmployerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectItem(String title, MenuItem menuItem) {
        Fragment fragment = null;
        menuItem.setChecked(true);
        if (title.equals(getResources().getString(R.string.menu_employer_my_jobs_home))) {
            lastTab = 0;
            fragment = new JobsFragment();
        }
        if (title.equals(getResources().getString(R.string.menu_employer_my_graftrs))) {
            lastTab = 1;
            fragment = new MyGraftrsEmployerFragment();
        }
        if (title.equals(getResources().getString(R.string.menu_employer_my_account))) {
            lastTab = 2;
            fragment = new AccountFragment();
        }

        if (title.equals(getResources().getString(R.string.menu_employer_log_out))) {
            lastTab = 0;
            fragment = null;
            SharedPreferencesManager.getInstance(this).deleteToken();
            SharedPreferencesManager.getInstance(this).deleteSessionInfoEmployer();
            SharedPreferencesManager.getInstance(this).deleteIsInComingSoon();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

//        if (title.equals(getResources().getString(R.string.menu_employer_edit_profile))) {
//            fragment = null;
//            startActivity(new Intent(this, OnboardingEmployerActivity.class));
//        }

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_employer_content, fragment)
                    .commit();

            drawerEmployerLayout.closeDrawers();
            setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 2) {
            int i = 0;
            while (i < getSupportFragmentManager().getBackStackEntryCount()) {
               getSupportFragmentManager().popBackStack();
                i++;
            }
            setTitle(getResources().getString(R.string.menu_employer_my_jobs_home));
        } else {
            super.onBackPressed();
        }
    }
}