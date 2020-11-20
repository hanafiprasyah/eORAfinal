package com.example.eorafinal;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class DonasiUmum extends AppCompatActivity {

    TextView btnBack,idRegDonaturUmum,namaDonaturUmum,jenisDonasi,statusDonasiUmum,totalDonasi,currentDate;
    MaterialEditText ETuangDonasi;
    ConnectivityManager conMgr;
    private ProgressDialog loading;
    Button kirimUang;
    String idRegGet,namaDonaturGet,jenisDonasiGet,jumlahUangGet,statusDonasiGet,tanggaldonasiget;

    private static final String URL_PRODUCTS = "https://prasyah.000webhostapp.com/DonaturHome/getTotalDonasi.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_donasi_umum);

        Bundle idRegToDonasiUmum = getIntent().getExtras();
        String revIDRegDonasiUmum = Objects.requireNonNull(idRegToDonasiUmum).getString("id_reg_donatur");
        idRegDonaturUmum = findViewById(R.id.tv_idRegDonaturUmum);
        idRegDonaturUmum.setText(revIDRegDonasiUmum);

        ETuangDonasi = findViewById(R.id.et_uangDonasiUmum);
        totalDonasi = findViewById(R.id.tv_totalDonasi);
        loadProducts();

        currentDate = findViewById(R.id.tvCurrentDate);
        //getDateAndTime
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        String date = dateFormat.format(calendar.getTime());
        currentDate.setText(date);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getDataDonatur();

            } else {
                Intent i = new Intent(DonasiUmum.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        jenisDonasi = findViewById(R.id.tv_jenisDonasiDonatur);
        jenisDonasi.setText("umum");

        statusDonasiUmum = findViewById(R.id.tv_statusDonasiDonatur);
        statusDonasiUmum.setText("menunggu");

        namaDonaturUmum = findViewById(R.id.tv_namaDonaturUmum);

        kirimUang = findViewById(R.id.btn_KirimUang);
        kirimUang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idRegGet = idRegDonaturUmum.getText().toString();
                namaDonaturGet = namaDonaturUmum.getText().toString();
                jenisDonasiGet = jenisDonasi.getText().toString();
                jumlahUangGet = ETuangDonasi.getText().toString();
                statusDonasiGet = statusDonasiUmum.getText().toString();
                tanggaldonasiget = currentDate.getText().toString();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        verifikasiJumlah();
                    }
                },500);
            }
        });

        btnBack = findViewById(R.id.btnBacktoPilihDonasi);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
                overridePendingTransition(0,R.anim.slide_outta_right);
            }
        });
    }

    private void verifikasiJumlah() {
        if(jumlahUangGet.equals("")) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.registerverified_toast, (ViewGroup)findViewById(R.id.customRegisterVerifiedToast));
            TextView title = layout.findViewById(R.id.tv_textVerified);
            title.setText("Anda belum mengisi jumlah uang");
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.BOTTOM,0,100);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } else {
            konfirmasiTransaksi();
        }
    }

    private void konfirmasiTransaksi() {
        loading = new ProgressDialog(DonasiUmum.this,R.style.ProgressBarDonatur);
        loading.setMessage("Mohon tunggu..");
        loading.setCancelable(false);
        loading.show();
        hideKeyboard(DonasiUmum.this);

        AlertDialog.Builder builder = new AlertDialog.Builder(DonasiUmum.this,R.style.ProgressBarDonatur);
        builder.setMessage("Yakin dengan jumlah uang yang dikirim?")
                .setCancelable(true)
                .setNegativeButton("CEK ULANG", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loading.dismiss();
                    }
                })
                .setPositiveButton("YAKIN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        kirimData();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void kirimData() {
        AndroidNetworking.post("https://prasyah.000webhostapp.com/DonaturHome/kirimDonasiUmum.php")
                .addBodyParameter("id_reg_donatur",""+idRegGet)
                .addBodyParameter("nama_donatur",""+namaDonaturGet)
                .addBodyParameter("jenis_donasi",""+jenisDonasiGet)
                .addBodyParameter("jumlah_donasi",""+jumlahUangGet)
                .addBodyParameter("status_donasi",""+statusDonasiGet)
                .addBodyParameter("tanggal_donasi",""+tanggaldonasiget)
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
                                new AlertDialog.Builder(DonasiUmum.this)
                                        .setTitle("Berhasil")
                                        .setMessage("Data anda akan dimasukkan ke basis data. " +
                                                "Jika jumlah uang pada input tidak sesuai dengan jumlah uang transfer," +
                                                " maka proses penyaluran anda tidak di izinkan (mengulang) dan data donasi umum anda akan dihapus.")
                                        .setCancelable(false)
                                        .setPositiveButton("LANJUT", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent greetThanks = new Intent(DonasiUmum.this, GreetingDonasiUmum.class);
                                                startActivity(greetThanks);
                                                finishAfterTransition();
                                                overridePendingTransition(R.anim.slide_infrom_right,R.anim.stay);
                                                loading.dismiss();
                                            }
                                        })
                                        .show();
                            }else{
                                new AlertDialog.Builder(DonasiUmum.this)
                                        .setMessage("Gagal Kirim Data")
                                        .setCancelable(false)
                                        .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = getIntent();
                                                setResult(RESULT_CANCELED,i);
                                                loading.dismiss();
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

    private void getDataDonatur() {
        String idReg = idRegDonaturUmum.getText().toString();

        if (idReg.equals("")) {
            Toast.makeText(this, "Server bermasalah", Toast.LENGTH_SHORT).show();
            return;
        }

        loading = new ProgressDialog(this,R.style.ProgressBarDonatur);
        loading.setCancelable(false);
        loading.setMessage("Mohon tunggu ...");
        loading.show();

        final String dataDonatur = ConfigProfileDonatur.DATA_DONASI_UMUM+idRegDonaturUmum.getText().toString();

        StringRequest stringRequest = new StringRequest(dataDonatur, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DonasiUmum.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response) {
        String namaDonatur="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileDonatur.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            namaDonatur = collegeData.getString(ConfigProfileDonatur.KEY_NAMA);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        namaDonaturUmum.setText(""+namaDonatur);
    }

    public void hideKeyboardOnLayoutDonasiUmum(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        view = this.getCurrentFocus();
        if (view == null){
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public static void hideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null){
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }


    private void loadProducts() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                totalDonasi.setText(product.getString("jumlah_donasi"));
                                if (totalDonasi.getText()=="null"){
                                    totalDonasi.setText("Belum ada donasi");
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        overridePendingTransition(0,R.anim.slide_outta_right);
    }
}