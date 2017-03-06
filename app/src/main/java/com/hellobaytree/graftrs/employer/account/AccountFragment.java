package com.hellobaytree.graftrs.employer.account;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.createjob.dialog.CRNDialog;
import com.hellobaytree.graftrs.employer.payments.PaymentsActivity;
import com.hellobaytree.graftrs.employer.payments.fragment.PricePlanFragment;
import com.hellobaytree.graftrs.employer.reviews.ReviewsActivity;
import com.hellobaytree.graftrs.employer.settings.EmployerSettingsActivity;
import com.hellobaytree.graftrs.employer.subscription.SubscriptionActivity;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.models.Employer;
import com.hellobaytree.graftrs.shared.utils.Constants;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.utils.MediaTools;
import com.hellobaytree.graftrs.shared.view.widget.RatingView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by maizaga on 27/12/16.
 * 
 */
public class AccountFragment extends Fragment {

    private static final String TAG = "AccountFragment";

    private static final int LOGO_TAKE_PICTURE = 333;
    private static final int LOGO_PICK_GALLERY = 334;
    static final int REQUEST_PERMISSIONS = 335;
    static final int REQUEST_PERMISSION_READ_STORAGE = 336;

    @BindView(R.id.employer_account_logo) ImageView logo;
    @BindView(R.id.employer_account_name) TextView name;
    @BindView(R.id.employer_account_owner) TextView owner;
    @BindView(R.id.employer_account_rating) RatingView rating;
    // @BindView(R.id.employer_company_description) TextView description;
    @BindView(R.id.employer_account_reviews_counter) TextView reviewsCounter;
    @BindView(R.id.employer_account_task_counter) TextView myTasksCounter;

    @BindView(R.id.employer_account_my_tasks_layout) RelativeLayout myTasksLayout;
    @BindView(R.id.employer_account_invoices_layout) RelativeLayout invoicesLayout;
    @BindView(R.id.employer_account_leaderboards_layout) RelativeLayout leaderBoardsLayout;
    @BindView(R.id.employer_account_user_management) RelativeLayout accountUserManagementLayout;

    private Employer meEmployer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_employer_account, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchEmployer();
        configureView();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("My Account");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_employer_account, menu);
        menu.getItem(0).getIcon().mutate().setColorFilter(ContextCompat
                .getColor(getContext(), R.color.redSquareColor), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.employer_settings:
                startActivity(new Intent(getActivity(), EmployerSettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchEmployer() {
        HttpRestServiceConsumer.getBaseApiClient()
                .meEmployer()
                .enqueue(new Callback<ResponseObject<Employer>>() {
            @Override
            public void onResponse(Call<ResponseObject<Employer>> call,
                                   Response<ResponseObject<Employer>> response) {
                if (response.isSuccessful()) {
                    populateView(response.body().getResponse());
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<Employer>> call, Throwable t) {
                Log.e(TAG, "Error updating worker: " + t.getMessage());
            }
        });
    }

    private void updateLogo(String picture, int id) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HashMap<String, String> body = new HashMap<>();
        body.put("logo", picture);
        HttpRestServiceConsumer.getBaseApiClient()
                .updateLogo(id, body)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            fetchEmployer();
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void populateView(Employer employer) {
        try {
            if (null != employer) meEmployer = employer;
            if (null != employer.company) {
                if (null != employer.company.logo) {
                    if (!TextUtils.isEmpty(employer.company.logo)) {
                        Picasso.with(getContext())
                                .load(employer.company.logo)
                                .fit()
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(logo);
                    } else {
                        logo.setImageDrawable(ContextCompat
                                .getDrawable(getContext(), R.drawable.ic_logo_placeholder));
                    }
                }
                if (null != employer.company.name) {
                    name.setText(employer.company.name);
                }
//                if (null != employer.company.description) {
//                    description.setText(employer.company.description);
//                }
            }

            owner.setText(employer.firstName + " " + employer.lastName);
            rating.setRating(employer.reviewInt);

//            if (null != employer.picture) {
//                Picasso.with(getContext())
//                        .load(employer.picture)
//                        .fit()
//                        .memoryPolicy(MemoryPolicy.NO_CACHE)
//                        .into(logo);
//            }

            if (employer.reviewCount > 0) {
                reviewsCounter.setText(String.valueOf(employer.reviewCount));
                reviewsCounter.setVisibility(View.VISIBLE);
            } else {
                reviewsCounter.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureView() {
        reviewsCounter.setVisibility(View.GONE);
        myTasksCounter.setVisibility(View.GONE);

        // TODO Enable for next versions
        myTasksLayout.setVisibility(View.GONE);
        invoicesLayout.setVisibility(View.GONE);
        leaderBoardsLayout.setVisibility(View.GONE);
        accountUserManagementLayout.setVisibility(View.GONE);
    }

    @OnClick({R.id.employer_account_reviews_layout,
            R.id.employer_account_subscription_plan_management})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.employer_account_reviews_layout:
                startActivity(new Intent(getActivity(), ReviewsActivity.class));
                break;
            case R.id.employer_account_subscription_plan_management:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_employer_content, PricePlanFragment.newInstance())
                        .commit();
                break;
        }
    }

    @OnClick(R.id.employer_account_logo)
    public void logo() {
        showChooserDialog();
    }

    private void showChooserDialog() {
        CharSequence[] options = {getString(R.string.onboarding_take_photo),
                getString(R.string.onboarding_choose_from_gallery),
                getString(R.string.onboarding_cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.onboarding_add_photo));

        builder.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        openCamera();
                        break;
                    case 1:
                        openGallery();
                        break;
                    case 2:
                        dialog.cancel();
                        break;
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
        }
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            dispatchOpenGalleryIntent();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ_STORAGE);
        }
    }

    private void dispatchTakePictureIntent() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, LOGO_TAKE_PICTURE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dispatchOpenGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.onboarding_select_image)),
                LOGO_PICK_GALLERY);
    }

    private void prepPicture(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes = baos.toByteArray();
            String file = Base64.encodeToString(bytes, Base64.NO_WRAP);
            updateLogo(file, (null != meEmployer) ? meEmployer.id : 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == LOGO_TAKE_PICTURE) {
                try {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    prepPicture(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == LOGO_PICK_GALLERY) {
                try {
                    Uri uri = data.getData();
                    Bitmap bitmap = BitmapFactory.decodeFile(MediaTools.getPath(getActivity(), uri));
                    prepPicture(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
                break;
            case REQUEST_PERMISSION_READ_STORAGE:
                if (grantResults.length > 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchOpenGalleryIntent();
                }
                break;
        }
    }

    @OnClick(R.id.employer_account_name)
    public void onChangeCRN() {
        CRNDialog crnDialog = CRNDialog.newInstance(new CRNDialog.CRNListener() {
            @Override
            public void onResult(boolean success) {
                if (success) {
                    //
                    Toast.makeText(getContext(), "CRN updated successfully!", Toast.LENGTH_LONG).show();
                    fetchEmployer();
                    //
                } else {
                    new AlertDialog.Builder(getContext())
                            .setMessage("This Company Registration " +
                                    "Number doesn't seem right, please try again...")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }
            }
        });
        crnDialog.show(getChildFragmentManager(), "");
    }
}