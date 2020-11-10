package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class EditProfilDonatur extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvIdReg,back,saveEditDonatur;
    ConnectivityManager conMgr;
    EditText etUsernameDonatur,etNamaDonatur,etEmailDonatur,etNotelDonatur,etPekerjaanDonatur;
    private ProgressDialog loading;
    String idRegistrasi,usernameDonatur,namaDonatur,emailDonatur,notelDonatur,pekerjaanDonatur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_edit_profil_donatur);

        Bundle b = getIntent().getExtras();
        String revIDReg = Objects.requireNonNull(b).getString("id_reg_donatur");
        tvIdReg = findViewById(R.id.et_idregdonatur);
        tvIdReg.setText(revIDReg);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getDataDonatur();
            } else {
                Intent i = new Intent(EditProfilDonatur.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        etUsernameDonatur = findViewById(R.id.et_usernameDonatur);
        etNamaDonatur = findViewById(R.id.et_namaDonatur);
        etPekerjaanDonatur = findViewById(R.id.et_pekerjaanDonatur);
        etEmailDonatur = findViewById(R.id.et_emailDonatur);
        etNotelDonatur = findViewById(R.id.et_notelDonatur);
        etNotelDonatur.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboard(v);
                }
            }
        });

        toolbar = findViewById(R.id.toolbarEditProfilMhs);

        TextView toolbarText = findViewById(R.id.toolbar_textDonatur);
        if (toolbarText != null && toolbar != null){
            setSupportActionBar(toolbar);
        }

        saveEditDonatur = findViewById(R.id.save_EditDonatur);
        saveEditDonatur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading = new ProgressDialog(EditProfilDonatur.this,R.style.ProgressBarDonatur);
                loading.setMessage("Mengganti Data...");
                loading.setCancelable(false);
                loading.show();
                hideKeyboardAfter(EditProfilDonatur.this);

                idRegistrasi = tvIdReg.getText().toString();
                usernameDonatur = etUsernameDonatur.getText().toString();
                namaDonatur = etNamaDonatur.getText().toString();
                pekerjaanDonatur = etPekerjaanDonatur.getText().toString();
                emailDonatur = etEmailDonatur.getText().toString();
                notelDonatur = etNotelDonatur.getText().toString();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        validasiData();
                    }
                },1000);
            }
        });

        back = findViewById(R.id.btnBacktoDonatur);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
                overridePendingTransition(0,R.anim.slide_outta_right);
            }
        });
    }

    private void validasiData() {
        if(usernameDonatur.equals("") || namaDonatur.equals("") ||
                pekerjaanDonatur.equals("") || emailDonatur.equals("") || notelDonatur.equals("")){
            loading.dismiss();
            Toast.makeText(EditProfilDonatur.this, "Ada form yang kosong. Periksa kembali data yang anda masukkan!", Toast.LENGTH_SHORT).show();
        }else {
            updateData();
        }
    }

    private void updateData() {
        AndroidNetworking.post("https://prasyah.000webhostapp.com/editProfilDonatur.php")
                .addBodyParameter("id_reg_donatur",""+idRegistrasi)
                .addBodyParameter("username_login",""+usernameDonatur)
                .addBodyParameter("nama_donatur",""+namaDonatur)
                .addBodyParameter("pekerjaan_donatur",""+pekerjaanDonatur)
                .addBodyParameter("email_donatur",""+emailDonatur)
                .addBodyParameter("notel_donatur",""+notelDonatur)
                .setTag("Update Data")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Log.d("responEdit",""+response);
                        try{
                            boolean status = response.getBoolean("status");
                            if(status){
                                new AlertDialog.Builder(EditProfilDonatur.this)
                                        .setMessage("Silakan geser ke atas untuk refresh!")
                                        .setTitle("Berhasil")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = getIntent();
                                                setResult(RESULT_OK,i);
                                                EditProfilDonatur.this.finish();
                                            }
                                        })
                                        .show();
                            }else{
                                new AlertDialog.Builder(EditProfilDonatur.this)
                                        .setMessage("Gagal Mengupdate Data")
                                        .setCancelable(false)
                                        .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = getIntent();
                                                setResult(RESULT_CANCELED,i);
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

    public void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public static void hideKeyboardAfter(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null){
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    private void getDataDonatur() {
        String idReg = tvIdReg.getText().toString();

        if (idReg.equals("")) {
            Toast.makeText(this, "Server bermasalah", Toast.LENGTH_SHORT).show();
            return;
        }

        loading = new ProgressDialog(this,R.style.ProgressBarDonatur);
        loading.setCancelable(false);
        loading.setMessage("Sedang mengambil data..");
        loading.setTitle("Mohon Tunggu");
        loading.show();

        final String urlDonatur = ConfigProfileDonatur.DATA_URL+tvIdReg.getText().toString();

        StringRequest stringRequest = new StringRequest(urlDonatur, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditProfilDonatur.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response) {
        String usernameDonatur="";
        String namaDonatur="";
        String pekerjaanDonatur="";
        String emailDonatur="";
        String notelDonatur="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileDonatur.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            usernameDonatur = collegeData.getString(ConfigProfileDonatur.KEY_USERNAME);
            namaDonatur = collegeData.getString(ConfigProfileDonatur.KEY_NAMA);
            pekerjaanDonatur = collegeData.getString(ConfigProfileDonatur.KEY_PEKERJAAN);
            emailDonatur = collegeData.getString(ConfigProfileDonatur.KEY_EMAIL);
            notelDonatur = collegeData.getString(ConfigProfileDonatur.KEY_NOTEL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        etUsernameDonatur.setText(""+usernameDonatur);
        etNamaDonatur.setText(""+namaDonatur);
        etPekerjaanDonatur.setText(""+pekerjaanDonatur);
        etEmailDonatur.setText(""+emailDonatur);
        etNotelDonatur.setText(""+notelDonatur);
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        overridePendingTransition(0,R.anim.slide_outta_right);
    }
}