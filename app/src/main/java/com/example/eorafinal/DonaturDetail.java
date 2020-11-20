package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class DonaturDetail extends AppCompatActivity {

    private TextView idRegDonaturDetailDonatur,back,namaDetailDonatur,pekerjaanDetailDonatur,emailDetail,notelDetail;
    ConnectivityManager conMgr;
    ProgressDialog loadings;
    ImageView ivFotoDetailDonatur;
    Button callOrtu;
    LottieAnimationView waOrtuAsuh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_donatur_detail);

        Init();

        Bundle idDonaturtoDonaturDetail = getIntent().getExtras();
        String revIDRegDetailDonatur = Objects.requireNonNull(idDonaturtoDonaturDetail).getString("id_reg_donatur");
        idRegDonaturDetailDonatur.setText(revIDRegDetailDonatur);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getDataDetailOrangTuaAsuh();
                getFotoDetailOrangTuaAsuh();
                getEmailNotelDetaiOrangTuaAsuh();
            } else {
                Intent i = new Intent(DonaturDetail.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
                overridePendingTransition(R.anim.slide_infrom_right,R.anim.slide_outta_right);
            }
        });

        waOrtuAsuh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.whatsapp.com/send?phone=" + ""+notelDetail.getText();
                Intent wa = new Intent(Intent.ACTION_VIEW);
                wa.setData(Uri.parse(url));
                startActivity(wa);
            }
        });

        callOrtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(DonaturDetail.this,R.style.ProgressBarMahasiswa);
                        builder.setMessage("Apa yang ingin anda lakukan?")
                                .setCancelable(true)
                                .setPositiveButton("HUBUNGI", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String phone = ""+notelDetail.getText();
                                        Intent hubungi = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                                        startActivity(hubungi);
                                    }
                                })
                                .setNegativeButton("SIMPAN KONTAK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent saveNumber = new Intent(ContactsContract.Intents.Insert.ACTION);
                                        saveNumber.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                                        saveNumber.putExtra(ContactsContract.Intents.Insert.PHONE, notelDetail.getText());
                                        startActivity(saveNumber);
                                    }
                                });
                        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                },250);
            }
        });
    }

    private void getFotoDetailOrangTuaAsuh() {
        String id = idRegDonaturDetailDonatur.getText().toString();

        if (id.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        loadings = new ProgressDialog(this,R.style.ProgressBarMahasiswa);
        loadings.setCancelable(false);
        loadings.setMessage("Mengambil foto..");
        loadings.show();

        final String urlFotoDonatur                = ConfigProfileDonatur.URL_GETFOTODONATUR+id;

        StringRequest loadFoto = new StringRequest(urlFotoDonatur, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadings.dismiss();
                showFotoDetailOrangTuaAsuhJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DonaturDetail.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadFoto);
    }

    private void getDataDetailOrangTuaAsuh() {
        String id = idRegDonaturDetailDonatur.getText().toString();

        if (id.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        final String urlDataTambahanDetail                = ConfigProfileDonatur.URL_GETNAKER+id;

        StringRequest loadNaker = new StringRequest(urlDataTambahanDetail, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showNakerJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DonaturDetail.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadNaker);
    }

    private void getEmailNotelDetaiOrangTuaAsuh(){
        String id = idRegDonaturDetailDonatur.getText().toString();

        if (id.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        final String urlDataEmail                = ConfigProfileDonatur.URL_GET_EMAIL_NOTEL+id;

        StringRequest loademailNotel = new StringRequest(urlDataEmail, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showEmailNotelJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DonaturDetail.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loademailNotel);
    }

    private void showFotoDetailOrangTuaAsuhJSON(String response) {
        String foto="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileDonatur.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            foto = collegeData.getString(ConfigProfileDonatur.KEY_FOTO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Glide.with(DonaturDetail.this).load(foto).apply(RequestOptions.circleCropTransform()).into(ivFotoDetailDonatur);
        YoYo.with(Techniques.FadeInDown)
                .duration(750)
                .playOn(ivFotoDetailDonatur);
    }

    private void showNakerJSON(String response) {
        String nama="";
        String pekerjaan="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileDonatur.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            nama = collegeData.getString(ConfigProfileDonatur.KEY_NAMA);
            pekerjaan = collegeData.getString(ConfigProfileDonatur.KEY_PEKERJAAN);
        } catch (JSONException e){
            e.printStackTrace();
        }
        namaDetailDonatur.setText(""+nama);
        YoYo.with(Techniques.Landing).duration(1500).playOn(namaDetailDonatur);
        pekerjaanDetailDonatur.setText(""+pekerjaan);
        YoYo.with(Techniques.Landing).duration(1500).playOn(pekerjaanDetailDonatur);
    }

    private void showEmailNotelJSON(String response) {
        String email="";
        String notel="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileDonatur.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            email = collegeData.getString(ConfigProfileDonatur.KEY_EMAIL);
            notel = collegeData.getString(ConfigProfileDonatur.KEY_NOTEL);
        } catch (JSONException e){
            e.printStackTrace();
        }
        emailDetail.setText(""+email);
        YoYo.with(Techniques.Landing).duration(1500).playOn(emailDetail);
        notelDetail.setText(""+notel);
        YoYo.with(Techniques.Landing).duration(1500).playOn(notelDetail);
    }

    private void Init() {
        back = findViewById(R.id.btnBackDetailDonatur);
        idRegDonaturDetailDonatur = findViewById(R.id.idRegDetailDonatur);
        ivFotoDetailDonatur = findViewById(R.id.iv_fotoDetailDonatur);
        namaDetailDonatur = findViewById(R.id.tv_NamaDetailDonatur);
        pekerjaanDetailDonatur = findViewById(R.id.tv_pekerjaanDetailDonatur);
        emailDetail = findViewById(R.id.tv_emailDetailDonatur);
        notelDetail = findViewById(R.id.tv_NotelDetailDonatur);
        callOrtu = findViewById(R.id.hubungiOrtuAsuh);
        waOrtuAsuh = findViewById(R.id.lottie_WAortuAsuh);
    }
}