package com.applex.authenticationreimagined.featureModules.onBoardingModule.ui.login.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
//        enter_aadhar.addTextChangedListener(new TextWatcher() {
//
//            private static final int TOTAL_SYMBOLS = 14; // size of pattern 0000-0000-0000-0000
//            private static final int TOTAL_DIGITS = 12; // max numbers of digits in pattern: 0000 x 4
//            private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
//            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
//            private static final char DIVIDER = ' ';
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // noop
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // noop
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
//                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
//                }
//            }
//
//            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
//                boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
//                for (int i = 0; i < s.length(); i++) { // check that every element is right
//                    if (i > 0 && (i + 1) % dividerModulo == 0) {
//                        isCorrect &= divider == s.charAt(i);
//                    } else {
//                        isCorrect &= Character.isDigit(s.charAt(i));
//                    }
//                }
//                return isCorrect;
//            }
//
//            private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
//                final StringBuilder formatted = new StringBuilder();
//
//                for (int i = 0; i < digits.length; i++) {
//                    if (digits[i] != 0) {
//                        formatted.append(digits[i]);
//                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
//                            formatted.append(divider);
//                        }
//                    }
//                }
//
//                return formatted.toString();
//            }
//
//            private char[] getDigitArray(final Editable s, final int size) {
//                char[] digits = new char[size];
//                int index = 0;
//                for (int i = 0; i < s.length() && index < size; i++) {
//                    char current = s.charAt(i);
//                    if (Character.isDigit(current)) {
//                        digits[index] = current;
//                        index++;
//                    }
//                }
//                return digits;
//            }
//        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, 200);
            }
        });

        unzip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String destinationAddress = Environment.getExternalStorageDirectory().getPath().toString()+"/Aadhar";
                    new ZipFile(src, passcode.getText().toString().toCharArray()).extractAll(destinationAddress);
                    filepath = Environment.getExternalStorageDirectory().getPath().toString()+"/Aadhar/"+filename;
                    Toast.makeText(getContext(), filepath, Toast.LENGTH_SHORT).show();
                } catch (ZipException e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        src = getRealPathFromURI(uri);
        String[] params = src.split("/");
        filename = params[params.length-1].replace("zip","xml");
        zip_loc.setText(filename);
        //Toast.makeText(getContext(), src, Toast.LENGTH_SHORT).show();
    }
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

}