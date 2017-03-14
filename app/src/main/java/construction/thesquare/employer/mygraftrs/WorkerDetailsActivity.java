package construction.thesquare.employer.mygraftrs;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansTextView;

public class WorkerDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.worker_details_private_text) JosefinSansTextView privateText;
    private GoogleMap map;
    @BindView(R.id.worker_details_bullet_list_experience) JosefinSansTextView experienceText;
    @BindView(R.id.worker_details_bullet_list_skills) JosefinSansTextView skillsText;
    @BindView(R.id.worker_details_bullet_list_companies) JosefinSansTextView companiesText;
    @BindView(R.id.worker_details_preferred_location) JosefinSansTextView locationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_details);
        ButterKnife.bind(this);
        setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEmployer);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            final Drawable menu = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
            ab.setHomeAsUpIndicator(menu);
            ab.setDisplayHomeAsUpEnabled(true);
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

    @Override
    public void onResume() {
        super.onResume();
        tempStub();
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void tempStub() {
        List<String> list = new ArrayList<>();
        list.add("6 years of experience");list.add("Preferred rate $600");
        list.add("Up to date CSCS Card");list.add("Up to date Driving License");
        list.add("English language proficiency: Native");
        experienceText.setText(TextTools.toBulletList(list, true));

        List<String> list2 = new ArrayList<>();
        list2.add("Plumbing");list2.add("Roofing");list2.add("Flooring");list2.add("Construction");
        skillsText.setText(TextTools.toBulletList(list2, false));

        List<String> list3 = new ArrayList<>();
        list3.add("Balfour Beatty");list3.add("Morgan Sindall");
        companiesText.setText(TextTools.toBulletList(list3, true));

        locationText.setText("Chelsea, London");
    }

    @OnClick({R.id.employer_worker_award_button, R.id.employer_worker_decline_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.employer_worker_award_button:
                Toast.makeText(getApplicationContext(), "Under development: Award", Toast.LENGTH_LONG).show();
                break;
            case R.id.employer_worker_decline_button:
                Toast.makeText(getApplicationContext(), "Under development: Decline", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // stubbing temp marker
        LatLng chelsea = new LatLng(51.4851, 0.1749);
        map.addMarker(new MarkerOptions().position(chelsea).title("Marker in Chelsea"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(chelsea, 12));
    }
}
