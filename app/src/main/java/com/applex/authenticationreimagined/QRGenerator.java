package com.applex.authenticationreimagined;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRGenerator extends AppCompatActivity {

    private Bitmap bitmap;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ////////////////////DARK MODE//////////////////////
//        IntroPref introPref = new IntroPref(QRGenerator.this);
//        if(introPref.getTheme() == 1) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//        }
//        else if(introPref.getTheme() == 2){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }
//        else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        }
        ////////////////////DARK MODE//////////////////////
        setContentView(R.layout.activity_qr_generator);
        Toolbar tb = findViewById(R.id.toolb);
        tb.setTitle("Generate QR");
//        setSupportActionBar(tb);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        Button done = findViewById(R.id.done);
        EditText editText = findViewById(R.id.edittext);

        done.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1500){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if(editText.getText().toString().length()!=0){
                GenerateQR(editText.getText().toString());
            }
            else
                Toast.makeText(QRGenerator.this, "No Text Found", Toast.LENGTH_SHORT).show();
        });
    }

    private void GenerateQR(String string) {
        Dialog dialog = new Dialog(QRGenerator.this);
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
        qrgEncoder = new QRGEncoder(string, null, QRGContents.Type.TEXT, smallerdimension);

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

//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_web, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == android.R.id.home){
//            super.onBackPressed();
//        }
//        return super.onOptionsItemSelected(item);
//    }
}