package com.applex.authenticationreimagined.modules.login.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.applex.authenticationreimagined.R;
import com.applex.authenticationreimagined.models.UserModel;
import com.applex.authenticationreimagined.modules.main.ui.MainActivity;
import com.applex.authenticationreimagined.modules.splash.ui.ActivitySplash;
import com.applex.authenticationreimagined.utilities.preferenceManager.PreferenceManager;

import net.lingala.zip4j.ZipFile;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ActivityZip extends AppCompatActivity {

    EditText zip_loc,passcode;
    Button choose, unzip;
    String src, filename, filepath;
    String[] storagePermission;
    private static final int STORAGE_REQUEST_CODE = 400;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip);

        preferenceManager = new PreferenceManager(ActivityZip.this);

        zip_loc = findViewById(R.id.aadhar_no);
        choose = findViewById(R.id.choose_zip);
        unzip = findViewById(R.id.unzip);
        passcode = findViewById(R.id.passcode);
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
//                    //new ZipFile(src, passcode.getText().toString().toCharArray()).extractAll(destinationAddress);
//                    //new ZipFile(src, passcode.getText().toString().toCharArray()).extractFile(filename.replace("zip","xml"), destinationAddress, "unzipped.xml");
                    filepath = new StringBuilder().append(Environment.getExternalStorageDirectory().getPath()).append("/").append(filename.replace("zip","xml")).toString();
//                    filepath=src;
//                    Toast.makeText(getContext(), filepath, Toast.LENGTH_SHORT).show();
                    xmlparser();
                } catch (Exception e) {
                    Toast.makeText(ActivityZip.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //////////////////////PERMISSIONS//////////////////////////
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(ActivityZip.this, storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission(){
        return ContextCompat.checkSelfPermission(ActivityZip.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE )== (PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
//                        pickGallery();
                        select();
                    } else {
                        Toast.makeText(ActivityZip.this, "permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    //////////////////////PERMISSIONS//////////////////////////

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
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


    private void xmlparser() {
        Log.d("BAM 1", "in xmlparser");
        XmlPullParserFactory xmlFactoryObject;
        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactoryObject.newPullParser();
            File file = new File(filepath);
            InputStream is = new FileInputStream(file);
//            InputStream is = getAssets().open("xyz.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is,null);
            parseXML(parser);
            Log.d("BAM 2", "try");
        }
        catch (XmlPullParserException | IOException e) {
            Log.d("BAM 3", e.getMessage());
            e.printStackTrace();
        }


    }

    private void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException{

        int eventType = parser.getEventType();
        UserModel userModel = new UserModel();
        String text="",tagname;
        String referenceId="";
        String dob="", gender="", name="";
        String careof="",country="",dist="",house="",pc="",po="",state="",street="",subdist="",vtc="";
        String photo="";
        while (eventType != XmlPullParser.END_DOCUMENT){
//            String tagname;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
//                    userModel = new UserModel();
                    break;
                case XmlPullParser.START_TAG:
                    tagname = parser.getName();
                    if (tagname.equals("OfflinePaperlessKyc")){
                        userModel.setReferenceId(parser.getAttributeValue(null,"referenceId"));
                    }
                    else if (tagname.equals("UidData")){
                        text = "UidData";
                    }
                    else if (text!=null && !text.isEmpty() && text.matches("UidData")){
                        if (tagname.equals("Poi")){
                            userModel.setDob(parser.getAttributeValue(null,"dob"));
                            userModel.setGender(parser.getAttributeValue(null,"gender"));
                            userModel.setName(parser.getAttributeValue(null,"name"));
//                            Toast.makeText(requireActivity(), fname, Toast.LENGTH_SHORT).show();
                        }
                        else if (tagname.equals("Poa")){
                            userModel.setCareof(parser.getAttributeValue(null,"careof"));
                            userModel.setCountry(parser.getAttributeValue(null,"country"));
                            userModel.setDist(parser.getAttributeValue(null,"dist"));
                            userModel.setHouse(parser.getAttributeValue(null,"house"));
                            userModel.setPc(parser.getAttributeValue(null,"pc"));
                            userModel.setPo(parser.getAttributeValue(null,"po"));
                            userModel.setState(parser.getAttributeValue(null,"state"));
                            userModel.setStreet(parser.getAttributeValue(null,"street"));
                            userModel.setSubdist(parser.getAttributeValue(null,"subdist"));
                            userModel.setVtc(parser.getAttributeValue(null,"vtc"));
//                            Toast.makeText(requireActivity(), fname, Toast.LENGTH_SHORT).show();
                        }
                        else if (tagname.equals("Pht")){
                            userModel.setPhoto(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
//                    name = parser.getName();
//                    if (name.equalsIgnoreCase("country") && country != null){
//                        countries.add(country);
//                    }
                    break;
            }
            eventType = parser.next();
        }

        if(passcode.getText().toString()!=null && !passcode.getText().toString().isEmpty()){
            preferenceManager.setCurrentUser(userModel);
            preferenceManager.setSharecode(passcode.getText().toString());
            startActivity(new Intent(ActivityZip.this, MainActivity.class));
            finish();
        }
        else{
            Toast.makeText(ActivityZip.this, "Enter Sharecode", Toast.LENGTH_SHORT).show();
        }

//        Toast.makeText(ActivityZip.this,
//                preferenceManager.getCurrentUser().getReferenceId() + "\n" +
//                        preferenceManager.getCurrentUser().getName() + "\n" +
//                        preferenceManager.getCurrentUser().getCountry() + "\n" +
//                        preferenceManager.getSharecode(), Toast.LENGTH_SHORT).show();

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
        zip_loc.setText(filename);
        //Toast.makeText(getContext(), src, Toast.LENGTH_SHORT).show();
    }
}