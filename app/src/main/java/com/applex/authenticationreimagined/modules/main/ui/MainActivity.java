package com.applex.authenticationreimagined.modules.main.ui;

import static java.lang.Boolean.TRUE;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.applex.authenticationreimagined.QRGenerator;
import com.applex.authenticationreimagined.R;
import com.applex.authenticationreimagined.utilities.preferenceManager.PreferenceManager;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainActivity extends AppCompatActivity {

    PreferenceManager preferenceManager;
    private String data="";
    private Bitmap bitmap;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(MainActivity.this);

        String refid= preferenceManager.getCurrentUser().getReferenceId();
        String name= preferenceManager.getCurrentUser().getName();
        String country= preferenceManager.getCurrentUser().getCountry();

        data = refid + "\n" + name + "\n" + country;

        Button qr = findViewById(R.id.qr);

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
    }

    private void GenerateQR(String code) {

        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_qr_code);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView qrImagevew = dialog.findViewById(R.id.qrImageview);
        ImageView qrshare = dialog.findViewById(R.id.shareQr);

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
            qrImagevew.setImageBitmap(bitmap);

        }
        catch (WriterException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
        }
        dialog.show();
        if(bitmap != null) {
            Bitmap finalBitmap = bitmap;
            qrshare.setOnClickListener(v -> {

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