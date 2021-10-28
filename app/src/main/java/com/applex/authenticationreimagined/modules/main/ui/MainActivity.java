package com.applex.authenticationreimagined.modules.main.ui;

import static java.lang.Boolean.TRUE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applex.authenticationreimagined.QRGenerator;
import com.applex.authenticationreimagined.R;
import com.applex.authenticationreimagined.utilities.preferenceManager.PreferenceManager;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.zxing.WriterException;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainActivity extends AppCompatActivity {

    PreferenceManager preferenceManager;
    private String data="";
    private Bitmap bitmap;
    private long mLastClickTime = 0;
    private Button qr, btnScan;
    private TextView image_text, title_text;
    private LinearLayout layout;
    private ImageView qrImageview, shareQr;

    public static Uri resultUri;
    private String[] cameraPermission;
    private static final int CAMERA_REQUEST_CODE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        preferenceManager = new PreferenceManager(MainActivity.this);

//        String refid= preferenceManager.getCurrentUser().getReferenceId();
        String name= preferenceManager.getCurrentUser().getName();
        String dob = preferenceManager.getCurrentUser().getDob();
        String gender = preferenceManager.getCurrentUser().getGender();
        String house = preferenceManager.getCurrentUser().getHouse();
        String street = preferenceManager.getCurrentUser().getStreet();
        String po= preferenceManager.getCurrentUser().getPo();
        String dist = preferenceManager.getCurrentUser().getDist();
        String state = preferenceManager.getCurrentUser().getState();
        String pincode = preferenceManager.getCurrentUser().getPc();
        String country= preferenceManager.getCurrentUser().getCountry();

        data = name + "\n" + dob + "\n" + gender + "\n" +
                house + "\n" + street + "\n" + po + "\n" + dist + "\n" + state + "\n" + pincode + "\n" + country;

        qr = findViewById(R.id.qr);
        image_text = findViewById(R.id.image_text);
        title_text = findViewById(R.id.title_text);
        layout = findViewById(R.id.layout);
        qrImageview = findViewById(R.id.qrImageview);
        shareQr = findViewById(R.id.shareQr);


        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, QRGenerator.class));
                Dialog myDialogue = new Dialog(MainActivity.this);
                myDialogue.setContentView(R.layout.dialog_check_sharecode);
                myDialogue.setCanceledOnTouchOutside(TRUE);
                EditText sharecode = myDialogue.findViewById(R.id.sharecode);
                TextView submit = myDialogue.findViewById(R.id.submit);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String code = sharecode.getText().toString();
                        String pref_code = preferenceManager.getSharecode();
//
//                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1500){
//                            return;
//                        }
//                        mLastClickTime = SystemClock.elapsedRealtime();

                        if((code!=null && !code.isEmpty()) && (pref_code!=null && !pref_code.isEmpty())){
                            if(code.matches(pref_code)){
                                myDialogue.dismiss();
                                GenerateQR(data);
                            }
                            else{
                                myDialogue.dismiss();
                                Toast.makeText(MainActivity.this, "Enter Valid Sharecode", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            myDialogue.dismiss();
                            Toast.makeText(MainActivity.this, "Enter Sharecode", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                myDialogue.show();
                Objects.requireNonNull(myDialogue.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });

        btnScan.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            Animation bounce = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
            btnScan.startAnimation(bounce);
            if(checkCameraPermission()){
                startActivity(new Intent(MainActivity.this, ScannerActivity.class));
            }
            else {
                requestCameraPermission();
            }
        });

    }
    private boolean checkCameraPermission() {
        boolean result= ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE )== (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, cameraPermission, CAMERA_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    File file = new File(Environment.getExternalStorageDirectory() + "/Snaplingo/.ocr", "ocr_database.json");
                    if (introPref.isFirstTimeLaunchAfterUpdate() || !file.exists()) {
                        new MoveToFolders().execute();
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    //////////////////PERMISSION REQUESTS/////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = Objects.requireNonNull(result).getUri();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                TextRecognizer recognizer = new TextRecognizer.Builder(MainActivity.this).build();

                if (!recognizer.isOperational()) {
                    Toast.makeText(getApplicationContext(), "Text not recognisable", Toast.LENGTH_SHORT).show();
                }
                else {
                    Frame frame = new Frame.Builder().setBitmap(Objects.requireNonNull(bitmap)).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        if (i != items.size() - 1) {
                            sb.append("\n");
                        }
                    }

                    Intent intent = new Intent(MainActivity.this, OcrResultActivity.class);
                    intent.putExtra("Text",sb.toString().trim());
                    intent.putExtra("selection", "1");
                    startActivity(intent);
                }
            }
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(MainActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
        }
    }

    private void GenerateQR(String code) {

//        Dialog dialog = new Dialog(MainActivity.this);
//        dialog.setContentView(R.layout.dialog_qr_code);
//        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        ImageView qrImagevew = dialog.findViewById(R.id.qrImageview);
//        ImageView qrshare = dialog.findViewById(R.id.shareQr);

        QRGEncoder qrgEncoder;
        WindowManager manager=(WindowManager)getSystemService(WINDOW_SERVICE);
        Display display = null;
        if (manager != null) {
            display = manager.getDefaultDisplay();
        }
        Point point = new Point();
        if (display != null) {
            display.getSize(point);
        }
        int width=point.x;
        int height=point.y;
        int smallerdimension = Math.min(width, height);
        qrgEncoder = new QRGEncoder(code, null, QRGContents.Type.TEXT, smallerdimension);

        try {
            bitmap = qrgEncoder.encodeAsBitmap();
//            image_text.setVisibility(View.GONE);
//            layout.setVisibility(View.VISIBLE);
            qrImageview.setImageBitmap(bitmap);

        }
        catch (WriterException e) {
//            image_text.setVisibility(View.VISIBLE);
//            layout.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
        }
//        dialog.show();
        if(bitmap != null) {
            Bitmap finalBitmap = bitmap;
            shareQr.setOnClickListener(v -> {

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("*/*");

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                        finalBitmap, String.valueOf(System.currentTimeMillis()), null);
                Uri imageUri =  Uri.parse(path);
                share.putExtra(Intent.EXTRA_TEXT, "Generate QRs with SnapLingo! If you haven't downloaded yet, click here: https://play.google.com/store/apps/details?id=com.applex.snaplingo ");
                share.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(Intent.createChooser(share, "Share QR"));

            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
        }
    }
}