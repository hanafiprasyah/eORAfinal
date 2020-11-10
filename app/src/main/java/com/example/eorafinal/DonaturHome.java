package com.example.eorafinal;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class DonaturHome extends AppCompatActivity {

    SharedPreferences sharedPreferencesDonatur;
    String username;
    int regID;
    ProgressDialog loadings;
    public static final String TAG_USERNAME = "username_login";
    public static final String TAG_IDREG    = "id_reg_donatur";
    Toolbar toolbar;

    ImageView fotoProfil;

    ConnectivityManager conMgr;

    TextView usernameDonatur,idregDonatur,namaDonatur,pekerjaanDonatur,tempatLahirDonatur,tanggalLahirDonatur,emailDonatur,notelDonatur;

    private WaveSwipeRefreshLayout swipeToRefresh;

    FloatingActionButton FABeditprofilDonatur, FABkeluar, FABriwayatDonasi;
    FloatingActionMenu menu;
    Button beriDonasi;

    private ClipboardManager mClipManager;
    private ClipData myClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_donatur_home);

        fotoProfil = findViewById(R.id.iv_foto_profil);
        namaDonatur = findViewById(R.id.tv_NamaDonatur);
        pekerjaanDonatur = findViewById(R.id.tv_PekerjaanDonatur);
        tempatLahirDonatur = findViewById(R.id.tv_TempatLahirDonatur);
        tanggalLahirDonatur = findViewById(R.id.tv_TanggalLahirDonatur);
        emailDonatur = findViewById(R.id.tv_emailDonatur);
        notelDonatur = findViewById(R.id.tv_notelDonatur);
        beriDonasi = findViewById(R.id.btnBeriDonasi);

        mClipManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        notelDonatur.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String text;
                text = notelDonatur.getText().toString();
                myClip = ClipData.newPlainText("text",text);
                mClipManager.setPrimaryClip(myClip);

                Toast.makeText(getApplicationContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        swipeToRefresh = findViewById(R.id.refreshLayoutDonatur);
        swipeToRefresh.setWaveColor(Color.parseColor("#FFFFFF"));
        swipeToRefresh.setColorSchemeColors(R.color.color3,R.color.ghostwhite);
        //swipeToRefresh.setColorSchemeResources(R.color.color3, R.color.ghostwhite);
        //swipeToRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.seagreen));
        swipeToRefresh.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                swipeToRefresh.setRefreshing(true);
                getDataWithRefresh();
            }
        });

        idregDonatur = findViewById(R.id.tv_regID);
        regID = Integer.parseInt(getIntent().getStringExtra(TAG_IDREG));
        idregDonatur.setText(""+regID);
        idregDonatur.setVisibility(View.GONE);

        usernameDonatur = findViewById(R.id.tv_username);
        username = getIntent().getStringExtra(TAG_USERNAME);
        usernameDonatur.setText(""+username);
        usernameDonatur.setVisibility(View.GONE);

        FABeditprofilDonatur        = findViewById(R.id.editProfilDonatur);
        FABkeluar                   = findViewById(R.id.logoutDonatur);
        FABriwayatDonasi            = findViewById(R.id.riwayatDonasi);

        menu        = findViewById(R.id.menuDonatur);
        menu.setClosedOnTouchOutside(true);

        toolbar = findViewById(R.id.toolbarDonaturHome);
        TextView toolbarText = findViewById(R.id.toolbar_textDonatur);
        if (toolbarText != null && toolbar != null){
            toolbarText.setText("E-ORA | DONATUR");
            toolbarText.setTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
        }

        sharedPreferencesDonatur = getSharedPreferences(DonaturLogin.donatur_shared_preferences, Context.MODE_PRIVATE);

        beriDonasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passingIDregistrasi = idregDonatur.getText().toString();
                Intent toPilihDonasi = new Intent(DonaturHome.this,PilihDonasi.class);
                Bundle idRegToPilihDonasi = new Bundle();
                idRegToPilihDonasi.putString("id_reg_donatur",passingIDregistrasi);
                toPilihDonasi.putExtras(idRegToPilihDonasi);
                startActivity(toPilihDonasi);
                overridePendingTransition(R.anim.falldown,R.anim.stay);
                //Intent rekmahasiswa = new Intent(DonaturHome.this,LihatMahasiswa.class);
                //startActivity(rekmahasiswa);
                //overridePendingTransition(R.anim.slide_infrom_right,R.anim.stay);
            }
        });

        FABeditprofilDonatur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passingIDregistrasi = idregDonatur.getText().toString();
                Intent a = new Intent(DonaturHome.this,EditProfilDonatur.class);
                Bundle b = new Bundle();
                b.putString("id_reg_donatur",passingIDregistrasi);
                a.putExtras(b);
                startActivity(a);
                overridePendingTransition(R.anim.slide_infrom_right,R.anim.stay);
            }
        });

        FABriwayatDonasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passingIDforpilihRiwayat = idregDonatur.getText().toString();
                Intent gotoRiwayatDonasi = new Intent(DonaturHome.this,PilihRiwayatDonasi.class);
                Bundle riwayatDonasi = new Bundle();
                riwayatDonasi.putString("id_reg_donatur",passingIDforpilihRiwayat);
                gotoRiwayatDonasi.putExtras(riwayatDonasi);
                startActivity(gotoRiwayatDonasi);
                overridePendingTransition(R.anim.slide_infrom_right,R.anim.stay);
            }
        });

        FABkeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DonaturHome.this,R.style.ProgressBarDonatur);
                builder.setMessage("Anda yakin ingin keluar dari sesi?")
                        .setCancelable(false)
                        .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // update login session ke FALSE dan mengosongkan nilai username
                                SharedPreferences.Editor editor = sharedPreferencesDonatur.edit();
                                editor.putBoolean(DonaturLogin.session_status, false);
                                editor.putString(TAG_USERNAME,null);
                                editor.apply();

                                Intent intent = new Intent(DonaturHome.this, DonaturLogin.class);
                                startActivity(intent);
                                finishAfterTransition();
                                overridePendingTransition(0,R.anim.splash_fade_out);
                            }
                        })
                        .setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getData();
                Animation animMenuDonatur = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.show_from_bottom);
                menu.setAnimation(animMenuDonatur);
                menu.setVisibility(View.VISIBLE);
            } else {
                Intent i = new Intent(DonaturHome.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getDataWithRefresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                menu.close(true);
                conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        swipeToRefresh.setRefreshing(false);
                        getData();
                    } else {
                        Intent i = new Intent(DonaturHome.this,NetworkErrorActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }
        },1200);
    }

    private void getData() {
        String id = idregDonatur.getText().toString();

        if (id.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        loadings = new ProgressDialog(this,R.style.ProgressBarDonatur);
        loadings.setCancelable(false);
        loadings.setMessage("Sedang mengambil data..");
        loadings.setTitle("Mohon Tunggu");
        loadings.show();

        menu.close(true);

        final String url                = ConfigProfileDonatur.URL_GETFOTODONATUR+id;
        final String urlNaKer           = ConfigProfileDonatur.URL_GETNAKER+id;
        final String urlTTLemailNotel   = ConfigProfileDonatur.URL_GET_TTL_EMAIL_NOTEL+id;

        StringRequest loadFoto = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadings.dismiss();
                showFotoJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DonaturHome.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        StringRequest loadNaKer = new StringRequest(urlNaKer, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadings.dismiss();
                showNaKerJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DonaturHome.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        StringRequest loadTTLeMAILnotel = new StringRequest(urlTTLemailNotel, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadings.dismiss();
                showTTLemailNotelJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DonaturHome.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadFoto);
        requestQueue.add(loadNaKer);
        requestQueue.add(loadTTLeMAILnotel);
    }

    private void showTTLemailNotelJSON(String response) {
        String tempatLahir="";
        String tanggalLahir="";
        String email="";
        String notel="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileDonatur.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            tempatLahir = collegeData.getString(ConfigProfileDonatur.KEY_TEMPAT_LAHIR);
            tanggalLahir = collegeData.getString(ConfigProfileDonatur.KEY_TANGGAL_LAHIR);
            email = collegeData.getString(ConfigProfileDonatur.KEY_EMAIL);
            notel = collegeData.getString(ConfigProfileDonatur.KEY_NOTEL);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        tempatLahirDonatur.setAnimation(animFadeIn);
        tanggalLahirDonatur.setAnimation(animFadeIn);
        emailDonatur.setAnimation(animFadeIn);
        notelDonatur.setAnimation(animFadeIn);

        tempatLahirDonatur.setText(""+tempatLahir);
        tanggalLahirDonatur.setText(""+tanggalLahir);
        emailDonatur.setText(""+email);
        notelDonatur.setText(""+notel);
    }

    private void showNaKerJSON(String response) {
        String nama="";
        String pekerjaan="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileDonatur.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            nama = collegeData.getString(ConfigProfileDonatur.KEY_NAMA);
            pekerjaan = collegeData.getString(ConfigProfileDonatur.KEY_PEKERJAAN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        namaDonatur.setAnimation(animFadeIn);
        pekerjaanDonatur.setAnimation(animFadeIn);
        namaDonatur.setText(""+nama);
        pekerjaanDonatur.setText(""+pekerjaan);

    }

    private void showFotoJSON(String response) {
        String foto="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileDonatur.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            foto = collegeData.getString(ConfigProfileDonatur.KEY_FOTO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Glide.with(DonaturHome.this).load(foto).apply(RequestOptions.circleCropTransform()).into(fotoProfil);
    }

    @Override
    public void onBackPressed() {
    }
}