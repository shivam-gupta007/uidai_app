package com.yoadhar.residentApp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


public class qr_activity extends AppCompatActivity {

    ImageView qrImageView;
    private Bitmap bitmap;
    String userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        qrImageView = findViewById(R.id.qrImg);
        Bundle bundle = getIntent().getExtras();
        Toast.makeText(this, "my name is: " + bundle.getParcelable("name") , Toast.LENGTH_SHORT).show();
        userInfo = bundle.getParcelable("name") + " " + bundle.getParcelable("dob") + " " + bundle.getParcelable("addr");
        Toast.makeText(this, userInfo, Toast.LENGTH_SHORT).show();
    }



}