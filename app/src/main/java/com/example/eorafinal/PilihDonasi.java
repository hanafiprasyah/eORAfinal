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
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Objects;

public class PilihDonasi extends AppCompatActivity {

    RelativeLayout rlPilihDonasi;
    Button donasiUmum,donasiKhusus;
    TextView backToHomeDonatur,tvIdRegDonatur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_pilih_donasi);

        Bundle idRegToPilihDonasi = getIntent().getExtras();
        String revIDRegPilihDonasi = Objects.requireNonNull(idRegToPilihDonasi).getString("id_reg_donatur");
        tvIdRegDonatur = findViewById(R.id.idRegDonatur);
        tvIdRegDonatur.setText(revIDRegPilihDonasi);

        rlPilihDonasi = findViewById(R.id.RL_pilihDonasi);
        donasiUmum = findViewById(R.id.btn_donasiUmum);
        donasiKhusus = findViewById(R.id.btn_donasiKhusus);
        backToHomeDonatur = findViewById(R.id.TV_backToHomeDonatur);

        YoYo.with(Techniques.FadeInUp).playOn(rlPilihDonasi);
        YoYo.with(Techniques.Shake).playOn(donasiUmum);
        YoYo.with(Techniques.Shake).playOn(donasiKhusus);

        donasiKhusus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passingIDregistrasiToLihatMahasiswa = tvIdRegDonatur.getText().toString();
                Intent toLihatMahasiswa = new Intent(PilihDonasi.this,LihatMahasiswa.class);
                Bundle idRegToLihatMahasiswa = new Bundle();
                idRegToLihatMahasiswa.putString("id_reg_donatur",passingIDregistrasiToLihatMahasiswa);
                toLihatMahasiswa.putExtras(idRegToLihatMahasiswa);
                startActivity(toLihatMahasiswa);
                overridePendingTransition(R.anim.slide_infrom_right,R.anim.stay);
            }
        });

        donasiUmum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passingIDregistrasiToDonasiUmum = tvIdRegDonatur.getText().toString();
                Intent toPilihDonasiUmum = new Intent(PilihDonasi.this,DonasiUmum.class);
                Bundle idRegToDonasiUmum = new Bundle();
                idRegToDonasiUmum.putString("id_reg_donatur",passingIDregistrasiToDonasiUmum);
                toPilihDonasiUmum.putExtras(idRegToDonasiUmum);
                startActivity(toPilihDonasiUmum);
                overridePendingTransition(R.anim.slide_infrom_right,R.anim.stay);
            }
        });
        backToHomeDonatur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
                overridePendingTransition(0,R.anim.slide_outta_right);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        overridePendingTransition(0,R.anim.slide_outta_right);
    }
}