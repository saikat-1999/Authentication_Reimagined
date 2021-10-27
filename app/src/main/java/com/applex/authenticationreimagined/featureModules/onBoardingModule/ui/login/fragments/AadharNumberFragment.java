package com.applex.authenticationreimagined.featureModules.onBoardingModule.ui.login.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.applex.authenticationreimagined.R;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AadharNumberFragment extends Fragment {

    EditText zip_loc,passcode;
    Button choose, unzip;
    String src, filename, filepath;
    String[] storagePermission;
    private static final int STORAGE_REQUEST_CODE = 400;

    public AadharNumberFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aadhar_number, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        zip_loc = getView().findViewById(R.id.aadhar_no);
        choose = getActivity().findViewById(R.id.choose_zip);
        unzip = getActivity().findViewById(R.id.unzip);
        passcode = getActivity().findViewById(R.id.passcode);
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                }
                else {
                    select();
                }
            }
        });

        unzip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String destinationAddress = Environment.getExternalStorageDirectory().getPath();
                    ZipFile zipfile = new ZipFile(src, passcode.getText().toString().toCharArray());
                    zipfile.extractFile(filename.replace("zip","xml"),destinationAddress);
                    //new ZipFile(src, passcode.getText().toString().toCharArray()).extractAll(destinationAddress);
                    //new ZipFile(src, passcode.getText().toString().toCharArray()).extractFile(filename.replace("zip","xml"), destinationAddress, "unzipped.xml");
                    filepath = new StringBuilder().append(Environment.getExternalStorageDirectory().getPath()).append("/").append(filename.replace("zip","xml")).toString();
                    Toast.makeText(getContext(), filepath, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //////////////////////PERMISSIONS//////////////////////////
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(getActivity(), storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission(){
        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE )== (PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){

            case STORAGE_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted) {
//                        pickGallery();
                        select();
                    }
                    else {
                        Toast.makeText(getActivity(),"permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    //////////////////////PERMISSIONS//////////////////////////

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void select() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, 200);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        src = getRealPathFromURI(uri);
        String[] params = src.split("/");
        filename = params[params.length-1];
        zip_loc.setText(src);
        //Toast.makeText(getContext(), src, Toast.LENGTH_SHORT).show();
    }

}