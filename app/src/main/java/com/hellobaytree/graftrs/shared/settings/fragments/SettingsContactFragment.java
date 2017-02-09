package com.hellobaytree.graftrs.shared.settings.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.utils.TextTools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Evgheni on 11/17/2016.
 */

public class SettingsContactFragment extends Fragment {

    public static final String TAG = "SettingsContact";

    @BindView(R.id.image1) ImageView image1;
    @BindView(R.id.image2) ImageView image2;
    @BindView(R.id.image3) ImageView image3;
    public static final int PIC_ONE = 234;
    public static final int PIC_TWO = 435;
    public static final int PIC_THREE = 923;

    public static SettingsContactFragment newInstance() {
        return new SettingsContactFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_contact, container, false);
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
        getActivity().setTitle(getString(R.string.employer_settings_contact));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setTitle(getString(R.string.settings));
    }

    @OnClick({R.id.img1, R.id.img2, R.id.img3, R.id.send})
    public void toggles(View view) {
        switch (view.getId()) {
            case R.id.img1:
                //Toast.makeText(getContext(), "im1", Toast.LENGTH_SHORT).show();
                takePicture(PIC_ONE);
                break;
            case R.id.img2:
                //Toast.makeText(getContext(), "im2", Toast.LENGTH_SHORT).show();
                takePicture(PIC_TWO);
                break;
            case R.id.img3:
                //Toast.makeText(getContext(), "im3", Toast.LENGTH_SHORT).show();
                takePicture(PIC_THREE);
                break;
            case R.id.send:
                Toast.makeText(getContext(), "send", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void takePicture(int n) {
        TextTools.log(TAG, "taking picture");
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            pictureIntent(n);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, n);
        }
    }
    private void pictureIntent(int n) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, n);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PIC_ONE:
                    pictureIntent(PIC_ONE);
                    break;
                case PIC_TWO:
                    pictureIntent(PIC_TWO);
                    break;
                case PIC_THREE:
                    pictureIntent(PIC_THREE);
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            switch (requestCode) {
                case PIC_ONE:
                    image1.setImageBitmap(bitmap);
                    break;
                case PIC_TWO:
                    image2.setImageBitmap(bitmap);
                    break;
                case PIC_THREE:
                    image3.setImageBitmap(bitmap);
                    break;
            }
        }
    }
}
