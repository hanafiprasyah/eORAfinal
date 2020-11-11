package com.example.eorafinal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
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
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class MahasiswaHome extends AppCompatActivity {

    ImageButton btnUploadBerkas;
    SharedPreferences sharedPreferences;
    FloatingActionButton FABajukanproposal, FABeditprofil, FABkeluar, FABlihatDonatur, FABlaporanDonasi;
    FloatingActionMenu menu;
    Toolbar toolbar;
    String nama,NIM,id;
    MaterialEditText norekMhs,namaBank;
    TextView namaMhs,NIMmhs,statusProposalMhs,tvStatusShow,tvUploadBerkas,kataPenghubung,idProposal;

    ImageView fotoProfilMhs;

    LottieAnimationView lottieAnimationView;

    //prevent double click
    private long mLastClickTime = 0;

    Button editProposal,ajukanUlangProposal;

    private ProgressDialog loading;
    private WaveSwipeRefreshLayout swipeToRefreshForMahasiswa;

    public static final String TAG_NIM = "NIM";
    public static final String TAG_NAMA = "nama";

    LinearLayout uploadBerkasLayout,infoStatusMenunggu,infoLaporanDonasi,ajukanUlangLayout;

    ConnectivityManager conMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_mahasiswa_home);

        swipeToRefreshForMahasiswa = findViewById(R.id.refreshLayout);
        swipeToRefreshForMahasiswa.setWaveColor(Color.parseColor("#FFFFFF"));
        swipeToRefreshForMahasiswa.setColorSchemeColors(R.color.color3,R.color.ghostwhite);
        //swipeToRefresh.setColorSchemeResources(R.color.seagreen, R.color.ghostwhite);
        //swipeToRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.color3));
        swipeToRefreshForMahasiswa.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                swipeToRefreshForMahasiswa.setRefreshing(true);
                scrollRefresh();
            }
        });

        tvStatusShow = findViewById(R.id.tvStatusShow);

        namaMhs = findViewById(R.id.tvNamaMhs);
        nama = getIntent().getStringExtra(TAG_NAMA);
        Animation animFadeInNama = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        namaMhs.setAnimation(animFadeInNama);
        namaMhs.setText(""+nama);

        NIMmhs = findViewById(R.id.tv_NIM);
        NIM = getIntent().getStringExtra(TAG_NIM);
        NIMmhs.setText(""+NIM);
        NIMmhs.setVisibility(View.GONE);

        idProposal = findViewById(R.id.tv_idProposal);
        fotoProfilMhs = findViewById(R.id.iv_foto_profilMhs);

        FABajukanproposal = findViewById(R.id.ajukanProposalMhs);
        FABeditprofil = findViewById(R.id.editProfilMhs);
        FABkeluar   = findViewById(R.id.logoutMhs);
        FABlihatDonatur = findViewById(R.id.lihatDonaturMhs);
        FABlaporanDonasi = findViewById(R.id.laporanDonasi);

        menu        = findViewById(R.id.menu);
        menu.setClosedOnTouchOutside(true);

        toolbar = findViewById(R.id.toolbar);
        kataPenghubung = findViewById(R.id.tv_penghubungEdit);

        //Upload Berkas
        uploadBerkasLayout = findViewById(R.id.infoBerkas_layout);
        infoStatusMenunggu = findViewById(R.id.info_statusMenunggu);
        infoLaporanDonasi = findViewById(R.id.info_laporanDonasi);

        btnUploadBerkas = findViewById(R.id.IB_upload);
        YoYo.with(Techniques.Shake)
                .duration(600)
                .playOn(btnUploadBerkas);

        tvUploadBerkas = findViewById(R.id.tv_upload);
        YoYo.with(Techniques.Shake)
                .duration(600)
                .playOn(tvUploadBerkas);

        FABlaporanDonasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                String passingNIMforLaporan = NIMmhs.getText().toString();
                Intent goLaporan = new Intent(MahasiswaHome.this,RiwayatDonasiMahasiswa.class);
                Bundle riwayatDonasiMahasiswa = new Bundle();
                riwayatDonasiMahasiswa.putString("NIM",passingNIMforLaporan);
                goLaporan.putExtras(riwayatDonasiMahasiswa);
                startActivity(goLaporan);
                overridePendingTransition(R.anim.falldown,R.anim.stay);
            }
        });

        btnUploadBerkas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                String passingNIMforberkas = NIMmhs.getText().toString();
                Intent go = new Intent(MahasiswaHome.this,BerkasProposal.class);
                Bundle nim = new Bundle();
                nim.putString("NIM",passingNIMforberkas);
                go.putExtras(nim);
                startActivity(go);
                overridePendingTransition(R.anim.falldown,R.anim.stay);
            }
        });
        //--------------------------------------------------

        norekMhs    = findViewById(R.id.tvNorekMhs);
        namaBank    = findViewById(R.id.tvNamaBank);

        TextView toolbarText = findViewById(R.id.toolbar_text);
        if (toolbarText != null && toolbar != null){
            toolbarText.setText("E-ORA | Mahasiswa");
            setSupportActionBar(toolbar);
        }

        statusProposalMhs = findViewById(R.id.tv_statusProposalMhs);
        sharedPreferences = getSharedPreferences(MahasiswaLogin.my_shared_preferences, Context.MODE_PRIVATE);

        FABajukanproposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                String passingNIMForeign = NIMmhs.getText().toString();
                Intent a = new Intent(MahasiswaHome.this,AjukanProposalDonasi.class);
                Bundle obj = new Bundle();
                obj.putString("NIM",passingNIMForeign);
                a.putExtras(obj);
                startActivity(a);
                overridePendingTransition(R.anim.slide_infrom_right,R.anim.stay);
            }
        });

        FABeditprofil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                String passingNIM = NIMmhs.getText().toString();
                Intent a = new Intent(MahasiswaHome.this,EditProfilMhs.class);
                Bundle b = new Bundle();
                b.putString("NIM",passingNIM);
                a.putExtras(b);
                startActivity(a);
                overridePendingTransition(R.anim.slide_infrom_right,R.anim.stay);
            }
        });

        FABlihatDonatur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                Intent s = new Intent(MahasiswaHome.this,LihatDonatur.class);
                startActivity(s);
                overridePendingTransition(R.anim.slide_infrom_right,R.anim.stay);
            }
        });

        FABkeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                AlertDialog.Builder builder = new AlertDialog.Builder(MahasiswaHome.this,R.style.ProgressBarMahasiswa);
                builder.setMessage("Anda yakin ingin keluar dari sesi?")
                        .setCancelable(false)
                        .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // update login session ke FALSE dan mengosongkan nilai nama dan NIM
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(MahasiswaLogin.session_status, false);
                                editor.putString(TAG_NIM,null);
                                editor.apply();

                                Intent intent = new Intent(MahasiswaHome.this, MahasiswaLogin.class);
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
                Animation animMenuMahasiswa = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.show_from_bottom);
                menu.setAnimation(animMenuMahasiswa);
                menu.setVisibility(View.VISIBLE);
            } else {
                Intent i = new Intent(MahasiswaHome.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        //UlangPengajuanProposal
        ajukanUlangLayout = findViewById(R.id.ajukanUlang_layout);
        ajukanUlangProposal = findViewById(R.id.bt_AjukanUlangProposal);
        ajukanUlangProposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                AlertDialog.Builder builder = new AlertDialog.Builder(MahasiswaHome.this,R.style.ProgressBarMahasiswa);
                builder.setMessage("Data proposal anda yang lama akan dihapus oleh sistem. Anda dapat melakukan pengajuan ulang proposal donasi. Apakah anda setuju?")
                        .setTitle("Perhatian!")
                        .setCancelable(true)
                        .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();

                                id = idProposal.getText().toString();

                                loading.setMessage("Menghapus proposal ..");
                                loading.setCancelable(false);
                                loading.show();
                                HapusProposalMhs();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        editProposal = findViewById(R.id.bt_PerbaikanProposal);
        editProposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                AlertDialog.Builder builder = new AlertDialog.Builder(MahasiswaHome.this,R.style.ProgressBarMahasiswa);
                builder.setMessage("Jika ada form yang dikosongkan oleh admin, silakan anda lengkapi dengan data yang benar! ")
                        .setTitle("Perhatian!")
                        .setCancelable(true)
                        .setPositiveButton("EDIT PROPOSAL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PeringatanIsiJurusanFakultas();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void HapusProposalMhs() {
        AndroidNetworking.post("https://prasyah.000webhostapp.com/MahasiswaHome/hapusProposalMhs.php")
                .addBodyParameter("proposal_id",""+id)
                .setTag("Hapus Data")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responEdit",""+response);
                        try{
                            Boolean status = response.getBoolean("status");
                            if(status){
                                loading.dismiss();
                                scrollRefresh();
                                new android.app.AlertDialog.Builder(MahasiswaHome.this)
                                        .setMessage("Proposal yang lama berhasil dihapus. Anda ingin mengajukan proposal baru?")
                                        .setCancelable(false)
                                        .setPositiveButton("AJUKAN", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                                                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                                                    return;
                                                }
                                                mLastClickTime = SystemClock.elapsedRealtime();

                                                String passingNIMForeign = NIMmhs.getText().toString();
                                                Intent a = new Intent(MahasiswaHome.this,AjukanProposalDonasi.class);
                                                Bundle obj = new Bundle();
                                                obj.putString("NIM",passingNIMForeign);
                                                a.putExtras(obj);
                                                startActivity(a);
                                                overridePendingTransition(R.anim.slide_infrom_right,R.anim.stay);
                                            }
                                        })
                                        .show();
                            }else{
                                new android.app.AlertDialog.Builder(MahasiswaHome.this)
                                        .setMessage("Gagal Hapus Data")
                                        .setCancelable(false)
                                        .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                loading.dismiss();
                                                scrollRefresh();
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

    private void PeringatanIsiJurusanFakultas(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MahasiswaHome.this,R.style.ProgressBarMahasiswa);
        builder.setMessage("Jangan lupa mengisi data bagian pemilihan Fakultas dan Jurusan anda.")
                .setCancelable(true)
                .setPositiveButton("LANJUTKAN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String passingNIMForeignEdit = NIMmhs.getText().toString();
                        Intent a = new Intent(MahasiswaHome.this,EditProposal.class);
                        Bundle obj = new Bundle();
                        obj.putString("NIM",passingNIMForeignEdit);
                        a.putExtras(obj);
                        startActivity(a);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void scrollRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                tvStatusShow.setVisibility(View.VISIBLE);
                namaBank.setVisibility(View.VISIBLE);
                conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {

                        String NIM = NIMmhs.getText().toString();

                        swipeToRefreshForMahasiswa.setRefreshing(false);

                        if (NIM.equals("")) {
                            Toast.makeText(MahasiswaHome.this, "Server bermasalah", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            getData();
                            if (statusProposalMhs.equals("")){
                                FABajukanproposal.setVisibility(View.VISIBLE);
                                return;
                            }
                        }
                    } else {
                        Intent i = new Intent(MahasiswaHome.this,NetworkErrorActivity.class);
                        startActivity(i);
                        finish();
                        //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, 500);
    }

    private void getData() {
        String NIM = NIMmhs.getText().toString();

        loading = new ProgressDialog(this,R.style.ProgressBarMahasiswa);
        loading.setCancelable(false);
        loading.setMessage("Validasi proposal..");
        loading.show();

        if (NIM.equals("")) {
            FancyToast.makeText(getApplicationContext(), "Maaf, server bermasalah.",FancyToast.LENGTH_SHORT, FancyToast.ERROR, R.drawable.ic_cloud_upload_black_24dp, false).show();
            loading.dismiss();
            return;
        }

        final String url            = ConfigProfileMahasiswa.DATA_NOREK+NIM;
        final String namaBank       = ConfigProfileMahasiswa.DATA_NAMABANK+NIM;
        final String namaMahasiswa  = ConfigProfileMahasiswa.URL_GETNAMA+NIM;
        final String status         = ConfigProfileMahasiswa.URL_GETSTATUS+NIM;
        final String IDproposal     = ConfigProfileMahasiswa.URL_GETIDPROPOSALFORHOME+NIM;
        final String fotoProfil     = ConfigProfileMahasiswa.URL_GETFOTOMAHASISWA+NIM;

        //ReqIDproposal
        StringRequest requestIdProposal = new StringRequest(IDproposal, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showIdProposal(response);
            }
        },
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MahasiswaHome.this, "Server Bermasalah", Toast.LENGTH_SHORT).show();
            }
        });

        //ReqNamaRefresh
        StringRequest requestNama = new StringRequest(namaMahasiswa, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showNamaRefresh(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MahasiswaHome.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        //ReqNorek
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MahasiswaHome.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        //ReqNamaBank
        StringRequest requestNamaBank = new StringRequest(namaBank, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showNamaBank(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MahasiswaHome.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        //reqStatus
        StringRequest requestStatus = new StringRequest(status, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showStatus(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MahasiswaHome.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        StringRequest requestFotoProfil = new StringRequest(fotoProfil, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showFoto(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MahasiswaHome.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        requestQueue.add(requestNamaBank);
        requestQueue.add(requestNama);
        requestQueue.add(requestStatus);
        requestQueue.add(requestIdProposal);
        requestQueue.add(requestFotoProfil);
    }

    private void showIdProposal(String response){
        String idProposalBaru="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);
            idProposalBaru = collegeData.getString(ConfigProfileMahasiswa.KEY_IDPROPOSAL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        idProposal.setText(""+idProposalBaru);
    }

    private void showStatus(String response) {
        String statusRefresh="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);
            statusRefresh = collegeData.getString(ConfigProfileMahasiswa.KEY_STATUS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Animation animFadeInTVstatus = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        statusProposalMhs.setText(""+statusRefresh);
        if (statusProposalMhs.getText().equals("diterima")){
            statusProposalMhs.setTextColor(getResources().getColor(R.color.green));
            editProposal.setVisibility(View.GONE);
            uploadBerkasLayout.setVisibility(View.GONE);
            infoLaporanDonasi.setVisibility(View.VISIBLE);
            infoStatusMenunggu.setVisibility(View.GONE);
//            FABlaporanDonasi.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Landing).playOn(statusProposalMhs);
            YoYo.with(Techniques.FadeIn).playOn(infoLaporanDonasi);
        } else if (statusProposalMhs.getText().equals("ditolak")){
            statusProposalMhs.setTextColor(getResources().getColor(R.color.color5));
            FABlihatDonatur.setVisibility(View.GONE);
            FABlaporanDonasi.setVisibility(View.GONE);
            FABajukanproposal.setVisibility(View.GONE);
            editProposal.setVisibility(View.GONE);
            uploadBerkasLayout.setVisibility(View.GONE);
            ajukanUlangLayout.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Landing).playOn(statusProposalMhs);
            YoYo.with(Techniques.FadeIn).playOn(ajukanUlangLayout);
        } else if (statusProposalMhs.getText().equals("menunggu")){
            statusProposalMhs.setTextColor(getResources().getColor(R.color.color3));
            editProposal.setVisibility(View.GONE);
            uploadBerkasLayout.setVisibility(View.GONE);
            FABlaporanDonasi.setVisibility(View.GONE);
            FABajukanproposal.setVisibility(View.GONE);
            infoStatusMenunggu.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Landing).playOn(statusProposalMhs);
            YoYo.with(Techniques.FadeIn).playOn(infoStatusMenunggu);
        } else if (statusProposalMhs.getText().equals("perbaikan")){
            statusProposalMhs.setTextColor(getResources().getColor(R.color.gold));
            editProposal.setVisibility(View.VISIBLE);
            uploadBerkasLayout.setVisibility(View.VISIBLE);
            kataPenghubung.setVisibility(View.VISIBLE);
            FABlaporanDonasi.setVisibility(View.GONE);
            FABajukanproposal.setVisibility(View.GONE);
            YoYo.with(Techniques.Landing).playOn(statusProposalMhs);
            YoYo.with(Techniques.FadeIn).playOn(editProposal);
            YoYo.with(Techniques.FadeIn).playOn(uploadBerkasLayout);
            YoYo.with(Techniques.FadeIn).playOn(kataPenghubung);
        } else if (statusProposalMhs.getText().equals("upload berkas")){
            statusProposalMhs.setTextColor(getResources().getColor(R.color.gold));
            uploadBerkasLayout.setVisibility(View.VISIBLE);
            editProposal.setVisibility(View.GONE);
            FABlaporanDonasi.setVisibility(View.GONE);
            FABajukanproposal.setVisibility(View.GONE);
            YoYo.with(Techniques.Landing).playOn(statusProposalMhs);
            YoYo.with(Techniques.FadeIn).playOn(uploadBerkasLayout);
        }
        else {
            statusProposalMhs.setText("");
            FABlaporanDonasi.setVisibility(View.GONE);
        }
    }

    private void showNamaRefresh(String response) {
        String namabaru="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);
            namabaru = collegeData.getString(ConfigProfileMahasiswa.KEY_NAMA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        namaMhs.setText(""+namabaru);
        YoYo.with(Techniques.Landing).playOn(namaMhs);
    }

    private void showJSON(String response) {
        String norek="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);
            norek = collegeData.getString(ConfigProfileMahasiswa.KEY_NOREK);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        norekMhs.setText("Nomor Rekening : "+norek);
        norekMhs.setEnabled(false);
        YoYo.with(Techniques.Landing).playOn(norekMhs);
        if (norek.length()<=10){
            FABajukanproposal.setEnabled(true);
            YoYo.with(Techniques.FadeIn).playOn(norekMhs);
            norekMhs.setText("Silakan ajukan proposal donasi");
            namaBank.setVisibility(View.GONE);

            tvStatusShow = findViewById(R.id.tvStatusShow);
            tvStatusShow.setVisibility(View.GONE);
        } else {
            FABajukanproposal.setVisibility(View.GONE);
        }
    }

    private void showNamaBank(String response) {
        String nama_bank="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);
            nama_bank = collegeData.getString(ConfigProfileMahasiswa.KEY_BANK);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        namaBank.setText("Bank : "+nama_bank);
        namaBank.setEnabled(false);
        YoYo.with(Techniques.Landing).playOn(namaBank);
    }

    private void showFoto(String response){
        String foto="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileDonatur.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            foto = collegeData.getString(ConfigProfileMahasiswa.KEY_FOTO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Glide.with(MahasiswaHome.this).load(foto).apply(RequestOptions.circleCropTransform()).into(fotoProfilMhs);
    }

    @Override
    public void onBackPressed() {
    }
}
