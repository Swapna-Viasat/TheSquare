package com.hellobaytree.graftrs.worker.onboarding.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.createjob.persistence.GsonConfig;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.ZipCodeVerifier;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.data.model.ZipResponse;
import com.hellobaytree.graftrs.shared.data.persistence.SharedPreferencesManager;
import com.hellobaytree.graftrs.shared.models.Worker;
import com.hellobaytree.graftrs.shared.utils.Constants;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.utils.KeyboardUtils;
import com.hellobaytree.graftrs.shared.utils.MediaTools;
import com.hellobaytree.graftrs.shared.utils.TextTools;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by gherg on 12/18/2016.
 */

public class SelectWorkerInfoFragment extends Fragment {

    public static final String TAG = "WorkerInfoFragment";

    private Worker currentWorker;
    private int workerId;

    @BindView(R.id.first_name_input)
    TextInputLayout firstNameInput;
    @BindView(R.id.last_name_input)
    TextInputLayout lastNameInput;
    @BindView(R.id.zip_layout)
    TextInputLayout zipLayout;
    @BindView(R.id.email_layout)
    TextInputLayout emailLayout;
    @BindView(R.id.password_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.password2_layout)
    TextInputLayout password2Layout;
    @BindView(R.id.avatar)
    CircleImageView avatar;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_SELECTION = 2;
    static final int REQUEST_PERMISSIONS = 3;
    static final int REQUEST_PERMISSION_READ_STORAGE = 4;

    private boolean initialized;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            avatar.setImageBitmap(imageBitmap);
            prepPicture(getActivity(), imageBitmap);
        } else if (requestCode == REQUEST_IMAGE_SELECTION && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            Bitmap imageBitmap = BitmapFactory.decodeFile(MediaTools.getPath(getActivity(), imageUri));
            avatar.setImageBitmap(imageBitmap);
            prepPicture(getActivity(), imageBitmap);
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

    public static SelectWorkerInfoFragment newInstance(boolean singleEdition,
                                                       Worker worker) {
        SelectWorkerInfoFragment selectInfoFragment = new SelectWorkerInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdition);
        bundle.putSerializable(Constants.KEY_CURRENT_WORKER, worker);
        selectInfoFragment.setArguments(bundle);
        return selectInfoFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_info_worker, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workerId = SharedPreferencesManager.getInstance(getContext()).getWorkerId();

        if (null != getArguments().getSerializable(Constants.KEY_CURRENT_WORKER)) {
            currentWorker = (Worker) getArguments().getSerializable(Constants.KEY_CURRENT_WORKER);
            populate();
        }
    }

    private void populate() {
        try {

            firstNameInput.getEditText().setText(currentWorker.firstName);
            lastNameInput.getEditText().setText(currentWorker.lastName);
            if (initialized)
                emailLayout.getEditText().setText(currentWorker.email);
            zipLayout.getEditText().setText(currentWorker.zip);

            showProfileImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProfileImage() {
        if (currentWorker != null && currentWorker.picture != null) {
            Picasso.with(getContext())
                    .load(currentWorker.picture)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.bob)
                    .error(R.drawable.bob)
                    .into(avatar);
        }

    }

    @OnClick({R.id.avatar, R.id.next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatar:
                showChooserDialog();
                break;
            case R.id.next:
                if (validate()) {
                    validateZip();
                }
                break;
        }
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
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dispatchOpenGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.onboarding_select_image)),
                REQUEST_IMAGE_SELECTION);
    }

    private void prepPicture(Context context, Bitmap bitmap) {
        try {
            File file = new File(getContext().getCacheDir(),
                    "temp" + String.valueOf(bitmap.hashCode()));
            file.createNewFile();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
            baos.flush();
            baos.close();

            uploadPicture(context, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadPicture(Context context, File file) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        RequestBody request = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), request);
        HttpRestServiceConsumer.getBaseApiClient()
                .uploadProfileImageWorker(
                        SharedPreferencesManager.getInstance(context).loadSessionInfoWorker().getUserId(), body)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {

                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            currentWorker = response.body().getResponse();
                            showProfileImage();
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private boolean validate() {
        boolean result = true;
        if (TextUtils.isEmpty(firstNameInput.getEditText().getText().toString())) {
            firstNameInput.setError(getString(R.string.validate_first));
            result = false;
        } else if (TextUtils.isEmpty(lastNameInput.getEditText().getText().toString())) {
            lastNameInput.setError(getString(R.string.validate_last));
            result = false;
        } else if (TextUtils.isEmpty(emailLayout.getEditText().getText().toString())) {
            emailLayout.setError(getString(R.string.empty_email));
            result = false;
        } else if (!TextTools.isEmailValid(emailLayout.getEditText().getText().toString())) {
            emailLayout.setError(getString(R.string.validate_email));
            result = false;
        } else if ((TextUtils.isEmpty(passwordLayout.getEditText().getText().toString()))) {
            passwordLayout.setError(getString(R.string.validate_password));
            result = false;
        } else if ((TextUtils.isEmpty(password2Layout.getEditText().getText().toString()))) {
            password2Layout.setError(getString(R.string.validate_password_reenter));
            result = false;
        } else if ((!(passwordLayout.getEditText().getText().toString()
                .equals(password2Layout.getEditText().getText().toString())))) {
            password2Layout.setError(getString(R.string.validate_password_match));
            result = false;
        }

        if (!result) resetInputErrors.start();

        return result;
    }

    private void validateZip() {
        if ((TextUtils.isEmpty(zipLayout.getEditText().getText().toString()))) {
            zipLayout.setError(getString(R.string.validate_zip));
        } else {
            // Api call to validate postal code
            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

            ZipCodeVerifier.getInstance()
                    .api()
                    .verify(zipLayout.getEditText().getText().toString(), ZipCodeVerifier.API_KEY)
                    .enqueue(new Callback<ZipResponse>() {
                        @Override
                        public void onResponse(Call<ZipResponse> call, Response<ZipResponse> response) {
                            DialogBuilder.cancelDialog(dialog);

                            if (null != response.body()) {
                                if (null != response.body().message) {
                                    if (response.body().message.equals(ZipCodeVerifier.BAD_REQUEST)) {
                                        new AlertDialog.Builder(getContext())
                                                .setMessage(getString(R.string.validate_zip))
                                                .show();
                                    } else {
                                        new AlertDialog.Builder(getContext())
                                                .setMessage(getString(R.string.validate_zip))
                                                .show();
                                    }
                                } else {
                                    // all good
                                    patchWorker();
                                }
                            } else {
                                // response body null
                                new AlertDialog.Builder(getContext())
                                        .setMessage(getString(R.string.validate_zip))
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ZipResponse> call, Throwable t) {
                            DialogBuilder.cancelDialog(dialog);
                        }
                    });
        }
    }

    private void patchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        HashMap<String, Object> request = new HashMap<>();
        request.put("password", passwordLayout.getEditText().getText().toString());
        request.put("password2", password2Layout.getEditText().getText().toString());
        request.put("email", emailLayout.getEditText().getText().toString());
        request.put("post_code", zipLayout.getEditText().getText().toString());
        request.put("first_name", firstNameInput.getEditText().getText().toString());
        request.put("last_name", lastNameInput.getEditText().getText().toString());
        HttpRestServiceConsumer.getBaseApiClient()
                .patchWorker(workerId, request)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {

                            proceed(response.body().getResponse());

                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void proceed(Worker worker) {
        initialized = true;
        if (getArguments() != null && getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
            return;
        }
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.onboarding_content,
                        SelectLocationFragment.newInstance(false, worker))
                .addToBackStack("")
                .commit();
    }

    private CountDownTimer resetInputErrors = new CountDownTimer(10000, 10000) {
        @Override
        public void onTick(long l) {
            //
        }

        @Override
        public void onFinish() {
            try {
                TextTools.resetInputLayout(password2Layout);
                TextTools.resetInputLayout(passwordLayout);
                TextTools.resetInputLayout(emailLayout);
                TextTools.resetInputLayout(lastNameInput);
                TextTools.resetInputLayout(firstNameInput);
                TextTools.resetInputLayout(zipLayout);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        loadWorker();
        populate();
    }

    @Override
    public void onPause() {
        persistProgress();
        KeyboardUtils.hideKeyboard(getActivity());
        super.onPause();
    }

    private void loadWorker() {
        String workerJson = getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW,
                Context.MODE_PRIVATE).getString(Constants.KEY_PERSISTED_WORKER, "");

        if (!TextUtils.isEmpty(workerJson))
            currentWorker = GsonConfig.buildDefault().fromJson(workerJson, Worker.class);
    }

    private void persistProgress() {
        if (currentWorker != null) {
            currentWorker.firstName = firstNameInput.getEditText().getText().toString();
            currentWorker.lastName = lastNameInput.getEditText().getText().toString();
            currentWorker.email = emailLayout.getEditText().getText().toString();
            currentWorker.zip = zipLayout.getEditText().getText().toString();
        }

        getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.KEY_PERSISTED_WORKER, GsonConfig.buildDefault().toJson(currentWorker))
                .apply();
    }
}