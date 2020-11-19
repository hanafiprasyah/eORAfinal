package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class KelolaPengguna extends AppCompatActivity {

    RelativeLayout llKelolaMhs,llKelolaDonatur;
    Button btnKelolaDonatur,btnKelolaMahasiswa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_kelola_pengguna);

        llKelolaMhs = findViewById(R.id.LayoutButtonKelolaMahasiswa);
        llKelolaDonatur = findViewById(R.id.LayoutButtonKelolaDonatur);
        btnKelolaMahasiswa = findViewById(R.id.btnKelolaMahasiswa);
        btnKelolaDonatur = findViewById(R.id.btnKelolaDonatur);

        YoYo.with(Techniques.Shake).playOn(llKelolaDonatur);
        YoYo.with(Techniques.Shake).playOn(llKelolaMhs);

        btnKelolaDonatur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoKelolaDonatur = new Intent(KelolaPengguna.this,KelolaDonatur.class);
                startActivity(gotoKelolaDonatur);
                overridePendingTransition(R.anim.fade_in,R.anim.stay);
            }
        });

        btnKelolaMahasiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        overridePendingTransition(0,R.anim.slide_outta_right);
    }
}