package construction.thesquare.employer.createjob;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.employer.createjob.fragment.PreviewJobFragment;

/**
 * Created by gherg on 12/9/2016.
 */

public class PreviewJobActivity extends AppCompatActivity {

    public static final String TAG = "PreviewJobActivity";
    private CreateRequest request;

    @BindView(R.id.toolbar_preview_job) Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_job);
        ButterKnife.bind(this);
        setToolbar(false);

        request = (CreateRequest) getIntent().getSerializableExtra("request");

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, PreviewJobFragment.newInstance(request))
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "preview job activity resumed");
    }

    private void setToolbar(boolean back) {
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(getMenuIcon());
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setTitle(getString(R.string.create_job_preview));
            ab.setElevation(0);
        }
    }
    private Drawable getMenuIcon() {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_clear_black_24dp);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.redSquareColor));
        return drawable;
    }


    @Override
    public void onBackPressed() {
//        if (getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
//                .getBoolean(Constants.KEY_UNFINISHED, false)) {
//            //
//        } else {
//            super.onBackPressed();
//        }
    }
}