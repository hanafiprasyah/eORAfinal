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
import android.util.Log;
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
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.JsonObject;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class MahasiswaDetail extends AppCompatActivity {

    TextView namaMahasiswa,NIMmahasiswa,Fakultas,Jurusan,back,tvIPK,tvSemester,tvUKT,tvJumSau,tvGajiOrtu,idRegDonaturDetailMahasiswa,
    tvURLLHS,tvURLKRS,tvURLUKT,tvURLPrestasi;
    ImageView fotoMahasiswa,ivLHS,ivKRS,ivUKT,ivPrestasi;
    ConnectivityManager conMgr;
    ProgressDialog loadings;
    String NIM,nama;
    Button btnBeriDonasiKhusus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_mahasiswa_detail);

        Bundle idRegToDetailMahasiswa = getIntent().getExtras();
        String revIDRegDetailMahasiswa = Objects.requireNonNull(idRegToDetailMahasiswa).getString("id_reg_donatur");
        idRegDonaturDetailMahasiswa = findViewById(R.id.tv_idRegDonaturDetailMahasiswa);
        idRegDonaturDetailMahasiswa.setText(revIDRegDetailMahasiswa);

        fotoMahasiswa       = findViewById(R.id.iv_fotoMahasiswa);
        namaMahasiswa       = findViewById(R.id.tv_NamaMahasiswa);
        NIMmahasiswa        = findViewById(R.id.tv_NIMMahasiswa);
        Jurusan             = findViewById(R.id.tv_JurusanMahasiswa);
        Fakultas            = findViewById(R.id.tv_FakultasMahasiswa);
        tvIPK               = findViewById(R.id.tv_dataIPKforDetailMahasiswa);
        tvSemester          = findViewById(R.id.tv_dataSemesterforDetailMahasiswa);
        tvUKT               = findViewById(R.id.tv_dataUKTforDetailMahasiswa);
        tvJumSau            = findViewById(R.id.tv_dataJumlahAnakforDetailMahasiswa);
        tvGajiOrtu          = findViewById(R.id.tv_dataGajiOrtuforDetailMahasiswa);
        back                = findViewById(R.id.btnBackdonatur);
        btnBeriDonasiKhusus = findViewById(R.id.btn_beriDonasiKhusus);

        tvURLLHS            = findViewById(R.id.url_FileLHS);
        tvURLKRS            = findViewById(R.id.url_FileKRS);
        tvURLUKT            = findViewById(R.id.url_FileUKT);
        tvURLPrestasi       = findViewById(R.id.url_FilePrestasi);

        ivLHS               = findViewById(R.id.iv_fileLembarHasilStudi);
        ivKRS               = findViewById(R.id.iv_fileKRS);
        ivUKT               = findViewById(R.id.iv_fileUKT);
        ivPrestasi          = findViewById(R.id.iv_filePrestasi);

        ivLHS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String passingURL_LHS = tvURLLHS.getText().toString();
                    Intent toShowLHSActivity = new Intent(MahasiswaDetail.this,Lembarhasilstudi.class);
                    Bundle urlLHSToLHSactivity = new Bundle();
                    urlLHSToLHSactivity.putString("url_lhs",passingURL_LHS);
                    toShowLHSActivity.putExtras(urlLHSToLHSactivity);
                    startActivity(toShowLHSActivity);
                    overridePendingTransition(R.anim.slide_infrom_right,R.anim.stay);
            }
        });

        ivKRS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passingURL_KRS = tvURLKRS.getText().toString();
                Intent toShowKRSActivity = new Intent(MahasiswaDetail.this,Karturencanastudi.class);
                Bundle urlKRSToKRSactivity = new Bundle();
                urlKRSToKRSactivity.putString("url_krs",passingURL_KRS);
                toShowKRSActivity.putExtras(urlKRSToKRSactivity);
                startActivity(toShowKRSActivity);
                overridePendingTransition(R.anim.slide_infrom_right,R.anim.stay);
            }
        });

        ivUKT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passingURL_UKT = tvURLUKT.getText().toString();
                Intent toShowUKTActivity = new Intent(MahasiswaDetail.this,Buktiuangkuliah.class);
                Bundle urlUKTToUKTactivity = new Bundle();
                urlUKTToUKTactivity.putString("url_ukt",passingURL_UKT);
                toShowUKTActivity.putExtras(urlUKTToUKTactivity);
                startActivity(toShowUKTActivity);
                overridePendingTransition(R.anim.slide_infrom_right,R.anim.stay);
            }
        });

        ivPrestasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passingURL_prestasi = tvURLPrestasi.getText().toString();
                Intent toShowPrestasiActivity = new Intent(MahasiswaDetail.this,Prestasimahasiswa.class);
                Bundle urlPrestasiToPrestasiactivity = new Bundle();
                urlPrestasiToPrestasiactivity.putString("url_fileprestasi",passingURL_prestasi);
                toShowPrestasiActivity.putExtras(urlPrestasiToPrestasiactivity);
                startActivity(toShowPrestasiActivity);
                overridePendingTransition(R.anim.slide_infrom_right,R.anim.stay);
            }
        });

        Animation animBackForMhsDetail = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in);
        back.setAnimation(animBackForMhsDetail);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
                overridePendingTransition(R.anim.slide_infrom_right,R.anim.slide_outta_right);
            }
        });

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getDataIntent();
                getFotoMahasiswa();
                getFaJurMahasiswa();
                getDataPendukung();

                getFileLHS();
                getFileKRS();
                getFileUKT();
                getFilePrestasi();
            } else {
                Intent i = new Intent(MahasiswaDetail.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        btnBeriDonasiKhusus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MahasiswaDetail.this)
                        .setTitle("Anda yakin ingin memberi donasi?")
                        .setMessage("Seluruh aktifitas penyaluran donasi terhadap mahasiswa bersangkutan akan berada di luar tanggung jawab admin karena anda memilih donasi khusus (baca persyaratan). Lanjutkan?")
                        .setCancelable(true)
                        .setPositiveButton("LANJUTKAN", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String passingIDregistrasiToDonasiKhusus = idRegDonaturDetailMahasiswa.getText().toString();
                                String passingNIMmahasiswa = NIMmahasiswa.getText().toString();
                                Intent toDonasiKhusus = new Intent(MahasiswaDetail.this,DonasiKhusus.class);
                                Bundle idRegToDonasiKhusus = new Bundle();
                                Bundle NIMtoDonasiKhusus = new Bundle();
                                idRegToDonasiKhusus.putString("id_reg_donatur",passingIDregistrasiToDonasiKhusus);
                                NIMtoDonasiKhusus.putString("NIM",passingNIMmahasiswa);
                                toDonasiKhusus.putExtras(idRegToDonasiKhusus);
                                toDonasiKhusus.putExtras(NIMtoDonasiKhusus);
                                startActivity(toDonasiKhusus);
                                overridePendingTransition(R.anim.slide_infrom_right,R.anim.stay);
                            }
                        })
                        .show();
            }
        });
    }

    void getDataIntent() {
        NIM = getIntent().getStringExtra("NIM");
        nama = getIntent().getStringExtra("nama");

        YoYo.with(Techniques.FadeIn).duration(1000).playOn(NIMmahasiswa);
        YoYo.with(Techniques.FadeIn).duration(1000).playOn(namaMahasiswa);
        NIMmahasiswa.setText(""+NIM);
        namaMahasiswa.setText(""+nama);

        //Bundle bundle = getIntent().getExtras();
        //if(bundle!=null){
            //Glide.with(this).load(bundle.getString("image_path")).into(fotoMahasiswa);
            //namaMahasiswa.setText(bundle.getString("nama"));
            //NIMmahasiswa.setText(bundle.getString("NIM"));
        //}else{
            //namaMahasiswa.setText("");
            //NIMmahasiswa.setText("");
        //}
    }

    void getFaJurMahasiswa() {
        String id = NIMmahasiswa.getText().toString();

        if (id.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        final String urlFajur                = ConfigProfileMahasiswa.URL_GETFAJUR+id;

        StringRequest loadFajur = new StringRequest(urlFajur, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showFajurJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MahasiswaDetail.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadFajur);
    }

    void getFotoMahasiswa(){
        String id = NIMmahasiswa.getText().toString();

        if (id.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        loadings = new ProgressDialog(this,R.style.ProgressBarDonatur);
        loadings.setCancelable(false);
        loadings.setMessage("Mengambil foto..");
        loadings.show();

        final String urlFoto                = ConfigProfileMahasiswa.URL_GETFOTOMAHASISWA+id;

        StringRequest loadFoto = new StringRequest(urlFoto, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadings.dismiss();
                showFotoJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MahasiswaDetail.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadFoto);
    }

    void getDataPendukung(){
        String id = NIMmahasiswa.getText().toString();

        if (id.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        final String urlDataPendukung      = ConfigProfileMahasiswa.URL_GETDATAPENDUKUNG+id;

        StringRequest loadDataPendukung = new StringRequest(urlDataPendukung, new Response.Listener<String>() {
            @Override
            public void onResponse(String response2) {
                showDataPendukung(response2);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MahasiswaDetail.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                        tvIPK.setVisibility(View.GONE);
                        tvSemester.setVisibility(View.GONE);
                        tvUKT.setVisibility(View.GONE);
                        tvJumSau.setVisibility(View.GONE);
                        tvGajiOrtu.setVisibility(View.GONE);
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadDataPendukung);
    }

    //GET LHS
    void getFileLHS(){
        String idLHS = NIMmahasiswa.getText().toString();

        if (idLHS.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        final String urlLHS      = ConfigProfileMahasiswa.URL_GETLHS+idLHS;

        StringRequest loadLHS = new StringRequest(urlLHS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showFileLHS(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MahasiswaDetail.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                        tvIPK.setVisibility(View.GONE);
                        tvSemester.setVisibility(View.GONE);
                        tvUKT.setVisibility(View.GONE);
                        tvJumSau.setVisibility(View.GONE);
                        tvGajiOrtu.setVisibility(View.GONE);
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadLHS);
    }
    private void showFileLHS(String responseLHS){
        String urlLHSfix="";

        try {
            JSONObject jsonObject = new JSONObject(responseLHS);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            urlLHSfix = collegeData.getString(ConfigProfileMahasiswa.KEY_URL_LHS);
        } catch (JSONException e){
            e.printStackTrace();
        }
        tvURLLHS.setText(""+urlLHSfix);
    }

    //GET KRS
    void getFileKRS(){
        String idKRS = NIMmahasiswa.getText().toString();

        if (idKRS.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        final String urlKRS      = ConfigProfileMahasiswa.URL_GETKRS+idKRS;

        StringRequest loadKRS = new StringRequest(urlKRS, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseKRS) {
                showFileKRS(responseKRS);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MahasiswaDetail.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                        tvIPK.setVisibility(View.GONE);
                        tvSemester.setVisibility(View.GONE);
                        tvUKT.setVisibility(View.GONE);
                        tvJumSau.setVisibility(View.GONE);
                        tvGajiOrtu.setVisibility(View.GONE);
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadKRS);
    }
    private void showFileKRS(String responseKRS){
        String urlKRSfix="";

        try {
            JSONObject jsonObject = new JSONObject(responseKRS);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            urlKRSfix = collegeData.getString(ConfigProfileMahasiswa.KEY_URL_KRS);
        } catch (JSONException e){
            e.printStackTrace();
        }
        tvURLKRS.setText(""+urlKRSfix);
    }

    //GET UKT
    void getFileUKT(){
        String idUKT = NIMmahasiswa.getText().toString();

        if (idUKT.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        final String urlUKT      = ConfigProfileMahasiswa.URL_GETUKT+idUKT;

        StringRequest loadUKT = new StringRequest(urlUKT, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseUKT) {
                showFileUKT(responseUKT);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MahasiswaDetail.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                        tvIPK.setVisibility(View.GONE);
                        tvSemester.setVisibility(View.GONE);
                        tvUKT.setVisibility(View.GONE);
                        tvJumSau.setVisibility(View.GONE);
                        tvGajiOrtu.setVisibility(View.GONE);
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadUKT);
    }
    private void showFileUKT(String responseUKT){
        String urlUKTfix="";

        try {
            JSONObject jsonObject = new JSONObject(responseUKT);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            urlUKTfix = collegeData.getString(ConfigProfileMahasiswa.KEY_URL_UKT);
        } catch (JSONException e){
            e.printStackTrace();
        }
        tvURLUKT.setText(""+urlUKTfix);
    }

    //GET PRESTASI
    void getFilePrestasi(){
        String idPrestasi = NIMmahasiswa.getText().toString();

        if (idPrestasi.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        final String urlPrestasi      = ConfigProfileMahasiswa.URL_GETPRESTASI+idPrestasi;

        StringRequest loadPrestasi = new StringRequest(urlPrestasi, new Response.Listener<String>() {
            @Override
            public void onResponse(String responsePrestasi) {
                showFilePrestasi(responsePrestasi);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MahasiswaDetail.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                        tvIPK.setVisibility(View.GONE);
                        tvSemester.setVisibility(View.GONE);
                        tvUKT.setVisibility(View.GONE);
                        tvJumSau.setVisibility(View.GONE);
                        tvGajiOrtu.setVisibility(View.GONE);
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadPrestasi);
    }
    private void showFilePrestasi(String responsePrestasi){
        String urlPrestasiFix="";

        try {
            JSONObject jsonObject = new JSONObject(responsePrestasi);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            urlPrestasiFix = collegeData.getString(ConfigProfileMahasiswa.KEY_URL_Prestasi);
        } catch (JSONException e){
            e.printStackTrace();
        }
        tvURLPrestasi.setText(""+urlPrestasiFix);
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
        Glide.with(MahasiswaDetail.this).load(foto).apply(RequestOptions.circleCropTransform()).into(fotoMahasiswa);
        YoYo.with(Techniques.FadeInDown)
                .duration(750)
                .playOn(fotoMahasiswa);
    }

    private void showFajurJSON(String response) {
        String jurusan="";
        String fakultas="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            jurusan = collegeData.getString(ConfigProfileMahasiswa.KEY_JURUSAN);
            fakultas = collegeData.getString(ConfigProfileMahasiswa.KEY_FAKULTAS);
        } catch (JSONException e){
            e.printStackTrace();
        }
        Jurusan.setText(""+jurusan);
        YoYo.with(Techniques.Landing).duration(1500).playOn(Jurusan);
        Fakultas.setText(""+fakultas);
        YoYo.with(Techniques.Landing).duration(1500).playOn(Fakultas);
    }

    private void showDataPendukung(String response2) {
        String ipk_mhs="";
        String semester="";
        String ukt_mhs="";
        String jumlah_saudara="";
        String gaji_ortu="";

        try {
            JSONObject jsonObject = new JSONObject(response2);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            ipk_mhs            = collegeData.getString(ConfigProfileMahasiswa.KEY_IPK);
            semester           = collegeData.getString(ConfigProfileMahasiswa.KEY_SEMESTER);
            ukt_mhs            = collegeData.getString(ConfigProfileMahasiswa.KEY_UKT);
            jumlah_saudara     = collegeData.getString(ConfigProfileMahasiswa.KEY_JUMSAU);
            gaji_ortu          = collegeData.getString(ConfigProfileMahasiswa.KEY_GAJIORTU);
        } catch (JSONException e){
            e.printStackTrace();
        }
        tvIPK.setText("IPK : "+ipk_mhs);
        YoYo.with(Techniques.Landing).duration(1500).playOn(tvIPK);
        tvSemester.setText("Semester : "+semester);
        YoYo.with(Techniques.Landing).duration(1500).playOn(tvSemester);
        tvUKT.setText("UKT : "+ukt_mhs);
        YoYo.with(Techniques.Landing).duration(1500).playOn(tvUKT);
        tvJumSau.setText("Jumlah Saudara : "+jumlah_saudara);
        YoYo.with(Techniques.Landing).duration(1500).playOn(tvJumSau);
        tvGajiOrtu.setText("Penghasilan Keluarga / bulan : "+gaji_ortu);
        YoYo.with(Techniques.Landing).duration(1500).playOn(tvGajiOrtu);
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        overridePendingTransition(R.anim.slide_infrom_right,R.anim.slide_outta_right);
    }
}