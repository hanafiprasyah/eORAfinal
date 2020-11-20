package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class GreetingDonasiKhusus extends AppCompatActivity {

    TextView NIMmhs,tvNotelMahasiswa,back;
    ImageView fotoMahasiswaGreeting;
    LinearLayout greetingLayout;
    LottieAnimationView lottieWAmhs;
    ConnectivityManager conMgr;
    ProgressDialog loadings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_greeting_donasi_khusus);

        UI();

        Bundle NIMtoGreeting = getIntent().getExtras();
        String revNIM_mhs = Objects.requireNonNull(NIMtoGreeting).getString("NIM");
        NIMmhs = findViewById(R.id.tv_NIMfromDonasiKhusus);
        NIMmhs.setText(revNIM_mhs);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getFoto();
                getNotel();
            } else {
                Intent i = new Intent(GreetingDonasiKhusus.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
            }
        }

        YoYo.with(Techniques.SlideInUp).playOn(greetingLayout);

        lottieWAmhs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String notelMhs = tvNotelMahasiswa.getText().toString();
                String url = "https://api.whatsapp.com/send?phone="+"+62"+notelMhs;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        back = findViewById(R.id.btnBacktoDonaturHomefromGreetingKhusus);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FancyToast.makeText(GreetingDonasiKhusus.this,"Terima kasih sudah berpartisipasi", Toast.LENGTH_SHORT, FancyToast.INFO, R.drawable.ic_done, false).show();
                finishAfterTransition();
                overridePendingTransition(0,R.anim.slide_outta_right);
            }
        });
    }

    private void UI() {
        fotoMahasiswaGreeting = findViewById(R.id.iv_fotoMahasiswaGreeting);
        greetingLayout = findViewById(R.id.LL_dataGreetingMahasiswa);
        tvNotelMahasiswa = findViewById(R.id.notelMahasiswaGreeting);
        lottieWAmhs = findViewById(R.id.btn_lottieWAmahasiswaKhusus);
    }

    void getFoto() {
        String id = NIMmhs.getText().toString();

        if (id.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        final String urlGetFoto            = ConfigProfileMahasiswa.URL_GETFOTOMAHASISWA+id;

        StringRequest loadFoto = new StringRequest(urlGetFoto, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadings.dismiss();
                showFotoGreetingJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GreetingDonasiKhusus.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadFoto);
    }

    void getNotel() {
        String NIM = NIMmhs.getText().toString();

        if (NIM.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        loadings = new ProgressDialog(this,R.style.ProgressBarDonatur);
        loadings.setCancelable(false);
        loadings.setMessage("Mohon tunggu ..");
        loadings.show();

        final String urlNotel                = ConfigProfileMahasiswa.URL_GETNOTEL+NIM;

        StringRequest loadData = new StringRequest(urlNotel, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadings.dismiss();
                showJSONnotel(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GreetingDonasiKhusus.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadData);
    }

    private void showFotoGreetingJSON(String response) {
        String foto="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            foto = collegeData.getString(ConfigProfileMahasiswa.KEY_FOTO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Glide.with(GreetingDonasiKhusus.this).load(foto).apply(RequestOptions.circleCropTransform()).into(fotoMahasiswaGreeting);
        YoYo.with(Techniques.SlideInDown).duration(800).playOn(fotoMahasiswaGreeting);
    }

    private void showJSONnotel(String response) {
        String notel="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            notel = collegeData.getString(ConfigProfileMahasiswa.KEY_NOTEL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tvNotelMahasiswa.setText(""+notel);
    }

    @Override
    public void onBackPressed() {
        FancyToast.makeText(GreetingDonasiKhusus.this,"Terima kasih sudah berpartisipasi", Toast.LENGTH_SHORT, FancyToast.INFO, R.drawable.ic_done, false).show();
        finishAfterTransition();
        overridePendingTransition(R.anim.slide_infrom_right,R.anim.slide_outta_right);
    }
}