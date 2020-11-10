package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class PilihRiwayatDonasi extends AppCompatActivity {

    Button gotoRiwayatUmum,gotoRiwayatKhusus;
    TextView back,idReg;
    ConnectivityManager conMgr;
    ProgressDialog loadings;
    ImageView fotoDonatur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_pilih_riwayat_donasi);

        Bundle riwayatDonasi = getIntent().getExtras();
        String revIDRegforPilihRiwayat = Objects.requireNonNull(riwayatDonasi).getString("id_reg_donatur");
        idReg = findViewById(R.id.tv_IDpilihRiwayatDonasi);
        idReg.setText(revIDRegforPilihRiwayat);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getDataFotoDonatur();
            } else {
                Intent i = new Intent(PilihRiwayatDonasi.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        fotoDonatur = findViewById(R.id.iv_foto_profilPilihRiwayatDonasi);
        back = findViewById(R.id.btnBacktoDonaturHome2);
        gotoRiwayatUmum = findViewById(R.id.btn_gotoRiwayatDonasiUmum);
        gotoRiwayatKhusus = findViewById(R.id.btn_gotoRiwayatDonasiKhusus);

        gotoRiwayatUmum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passingIDforRiwayatUmum = idReg.getText().toString();
                Intent gotoRiwayatDonasiUmum = new Intent(PilihRiwayatDonasi.this,RiwayatDonasi.class);
                Bundle riwayatDonasiUmum = new Bundle();
                riwayatDonasiUmum.putString("id_reg_donatur",passingIDforRiwayatUmum);
                gotoRiwayatDonasiUmum.putExtras(riwayatDonasiUmum);
                startActivity(gotoRiwayatDonasiUmum);
                overridePendingTransition(R.anim.falldown,R.anim.stay);
            }
        });

        gotoRiwayatKhusus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passingIDforRiwayatKhusus = idReg.getText().toString();
                Intent gotoRiwayatDonasiKhusus = new Intent(PilihRiwayatDonasi.this,RiwayatDonasiKhusus.class);
                Bundle riwayatDonasiKhusus = new Bundle();
                riwayatDonasiKhusus.putString("id_reg_donatur",passingIDforRiwayatKhusus);
                gotoRiwayatDonasiKhusus.putExtras(riwayatDonasiKhusus);
                startActivity(gotoRiwayatDonasiKhusus);
                overridePendingTransition(R.anim.falldown,R.anim.stay);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
                overridePendingTransition(0,R.anim.slide_outta_right);
            }
        });
    }

    private void getDataFotoDonatur() {
        String id = idReg.getText().toString();

        if (id.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        loadings = new ProgressDialog(this,R.style.ProgressBarDonatur);
        loadings.setCancelable(false);
        loadings.setMessage("Memuat foto ..");
        loadings.show();

        final String urlFoto = ConfigProfileDonatur.URL_GETFOTODONATUR+id;

        StringRequest loadFoto = new StringRequest(urlFoto, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadings.dismiss();
                showFotoDonaturJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PilihRiwayatDonasi.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadFoto);
    }

    private void showFotoDonaturJSON(String response) {
        String foto="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileDonatur.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            foto = collegeData.getString(ConfigProfileDonatur.KEY_FOTO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Glide.with(PilihRiwayatDonasi.this).load(foto).apply(RequestOptions.circleCropTransform()).into(fotoDonatur);
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        overridePendingTransition(0,R.anim.slide_outta_right);
    }
}