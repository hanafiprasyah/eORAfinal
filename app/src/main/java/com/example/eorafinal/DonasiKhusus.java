package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class DonasiKhusus extends AppCompatActivity {

    TextView tvIDregDonasiKhusus,tvNamaDonaturKhusus,tvJenisDonasi,tvIDregMahasiswaKhusus,tvStatus;
    ImageView fotoDonaturKhusus,fotoMahasiswaDonasiKhusus,btnSaveDonasi;
    ConnectivityManager conMgr;
    ProgressDialog loadings;
    String idRegGet,namaDonaturGet,jenisDonasiGet,statusDonasiGet,NIMmhsGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_donasi_khusus);

        UI();

        Bundle idRegToDonasiKhusus = getIntent().getExtras();
        Bundle NIMtoDonasiKhusus = getIntent().getExtras();
        String revIDRegDonasiKhusus = Objects.requireNonNull(idRegToDonasiKhusus).getString("id_reg_donatur");
        String revNIMDonasiKhusus = Objects.requireNonNull(NIMtoDonasiKhusus).getString("NIM");

        tvIDregDonasiKhusus = findViewById(R.id.tv_idRegDonasiKhusus);
        tvIDregDonasiKhusus.setText(revIDRegDonasiKhusus);

        tvIDregMahasiswaKhusus = findViewById(R.id.tv_idRegMahasiswaDonasiKhusus);
        tvIDregMahasiswaKhusus.setText(revNIMDonasiKhusus);

        tvJenisDonasi.setText("khusus");
        tvStatus.setText("disetujui");

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getDataDonatur();
                getDataMahasiswa();
            } else {
                Intent i = new Intent(DonasiKhusus.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        btnSaveDonasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idRegGet = tvIDregDonasiKhusus.getText().toString();
                namaDonaturGet = tvNamaDonaturKhusus.getText().toString();
                jenisDonasiGet = tvJenisDonasi.getText().toString();
                NIMmhsGet = tvIDregMahasiswaKhusus.getText().toString();
                statusDonasiGet = tvStatus.getText().toString();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        verifikasi();
                    }
                },100);
            }
        });
    }

    private void verifikasi(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DonasiKhusus.this,R.style.ProgressBarDonatur);
        builder.setMessage("Melanjutkan beri donasi khusus?")
                .setCancelable(true)
                .setNegativeButton("CEK ULANG", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadings.dismiss();
                        finishAfterTransition();
                        overridePendingTransition(0,R.anim.slide_outta_right);
                    }
                })
                .setPositiveButton("LANJUTKAN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadings = new ProgressDialog(DonasiKhusus.this,R.style.ProgressBarDonatur);
                        loadings.setCancelable(false);
                        loadings.setMessage("Mohon tunggu ..");
                        loadings.show();

                        kirimDataDonasiKhusus();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void kirimDataDonasiKhusus() {
        AndroidNetworking.post("https://prasyah.000webhostapp.com/DonaturHome/kirimDonasiKhusus.php")
                .addBodyParameter("id_reg_donatur",""+idRegGet)
                .addBodyParameter("nama_donatur",""+namaDonaturGet)
                .addBodyParameter("jenis_donasi",""+jenisDonasiGet)
                .addBodyParameter("NIM",""+NIMmhsGet)
                .addBodyParameter("status_donasi",""+statusDonasiGet)
                .setTag("Insert Data")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responEdit",""+response);
                        try{
                            boolean status = response.getBoolean("status");
                            if(status){
                                new AlertDialog.Builder(DonasiKhusus.this)
                                        .setMessage("Data anda sudah dimasukkan ke basis data sebagai bukti laporan donasi khusus. " +
                                                "Silakan hubungi mahasiswa terkait.")
                                        .setCancelable(false)
                                        .setPositiveButton("HUBUNGI", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String passingNIMtoGreeting = tvIDregMahasiswaKhusus.getText().toString();
                                                Intent toGreetingDonasiKhusus = new Intent(DonasiKhusus.this,GreetingDonasiKhusus.class);
                                                Bundle NIMtoGreeting = new Bundle();
                                                NIMtoGreeting.putString("NIM",passingNIMtoGreeting);
                                                toGreetingDonasiKhusus.putExtras(NIMtoGreeting);
                                                startActivity(toGreetingDonasiKhusus);
                                                loadings.dismiss();
                                                overridePendingTransition(R.anim.slide_infrom_right,R.anim.stay);
                                                finishAfterTransition();
                                            }
                                        })
                                        .show();
                            }else{
                                new AlertDialog.Builder(DonasiKhusus.this)
                                        .setMessage("Gagal Kirim Data")
                                        .setCancelable(false)
                                        .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = getIntent();
                                                setResult(RESULT_CANCELED,i);
                                                loadings.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    void getDataMahasiswa() {
        String id = tvIDregMahasiswaKhusus.getText().toString();

        if (id.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        final String urlFotoMahasiswa                = ConfigProfileMahasiswa.URL_GETFOTOMAHASISWA+id;

        StringRequest loadFoto = new StringRequest(urlFotoMahasiswa, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadings.dismiss();
                showFotoJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DonasiKhusus.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadFoto);
    }

    void getDataDonatur() {
        String idDonatur = tvIDregDonasiKhusus.getText().toString();

        if (idDonatur.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        loadings = new ProgressDialog(this,R.style.ProgressBarDonatur);
        loadings.setCancelable(false);
        loadings.setMessage("Mohon tunggu ..");
        loadings.show();

        final String urlDataDonatur                = ConfigProfileDonatur.URL_DATA_DONATURKHUSUS+idDonatur;

        StringRequest loadData = new StringRequest(urlDataDonatur, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadings.dismiss();
                showJSON_dataDonatur(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DonasiKhusus.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadData);
    }

    private void showJSON_dataDonatur(String response) {
        String foto_donatur="";
        String nama_donatur="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            nama_donatur = collegeData.getString(ConfigProfileDonatur.KEY_NAMA);
            foto_donatur = collegeData.getString(ConfigProfileDonatur.KEY_FOTO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Glide.with(DonasiKhusus.this).load(foto_donatur).apply(RequestOptions.circleCropTransform()).into(fotoDonaturKhusus);
        YoYo.with(Techniques.FadeInDown).duration(750).playOn(fotoDonaturKhusus);
        tvNamaDonaturKhusus.setText(""+nama_donatur);
        YoYo.with(Techniques.FadeInDown).duration(1500).playOn(tvNamaDonaturKhusus);
    }

    private void showFotoJSON(String response) {
        String foto="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            foto = collegeData.getString(ConfigProfileMahasiswa.KEY_FOTO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Glide.with(DonasiKhusus.this).load(foto).apply(RequestOptions.circleCropTransform()).into(fotoMahasiswaDonasiKhusus);
        YoYo.with(Techniques.FadeInUp)
                .duration(750)
                .playOn(fotoMahasiswaDonasiKhusus);
    }

    private void UI() {
        tvNamaDonaturKhusus = findViewById(R.id.tv_namaDonaturKhusus);
        fotoDonaturKhusus = findViewById(R.id.iv_fotoDonaturKhusus);
        fotoMahasiswaDonasiKhusus = findViewById(R.id.iv_fotoMahasiswaOnDonasiKhusus);
        tvJenisDonasi = findViewById(R.id.tv_jenisDonasiOnDonasiKhusus);
        btnSaveDonasi = findViewById(R.id.btn_saveDonasi);
        tvStatus = findViewById(R.id.tv_statusDonasiKhusus);
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        overridePendingTransition(0,R.anim.slide_outta_right);
    }
}