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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.util.Objects;

public class DetailProposal extends AppCompatActivity {

    TextView TVidProposal,back,namaDetailProposal,NIMDetailProposal;
    EditText etNamaAyah,etNamaIbu,etPekerjaanAyah,etPekerjaanIbu,etJumsau,etAnakke,etGajiOrtu,etUKT,etNamaBank,etNorek,etalamatDomisili,etalamatKTP,etIPK,etSemester;
    Button btnSetuju,btnTolak,btnPerbaiki,btnUploadUlang;
    ImageView fotoMhsDetailProposal;
    ConnectivityManager conMgr;
    private int pStatus = 0;
    private Handler handler = new Handler();
    private ProgressBar progressBar;
    ProgressDialog loadings;
    public static final String URL_GETFOTONAMANIM = "https://prasyah.000webhostapp.com/getNaFoNIMforDetailProposal.php?proposal_id=";
    public static final String JSON_ARRAY = "result";
    public static final String KEY_NAMA = "nama";
    public static final String KEY_NIM = "NIM";
    public static final String KEY_FOTO = "image_path";

    //dataProposal
    public static final String URL_GETDATA_PROPOSAL = "https://prasyah.000webhostapp.com/getDataProposalForAdmin.php?NIM=";
    public static final String KEY_NAMAAYAH = "nama_ayah";
    public static final String KEY_NAMAIBU = "nama_ibu";
    public static final String KEY_PEKERJAANAYAH = "pekerjaan_ayah";
    public static final String KEY_PEKERJAANIBU = "pekerjaan_ibu";
    public static final String KEY_JUMSAU = "jumlah_saudara";
    public static final String KEY_ANAKKE = "anak_ke";
    public static final String KEY_GAJIORTU = "gaji_ortu";
    public static final String KEY_UKT = "ukt_mhs";
    public static final String KEY_NAMABANK = "nama_bank";
    public static final String KEY_NOREK = "norek";
    public static final String KEY_ALAMATDOMISILI = "alamat_domisili";
    public static final String KEY_ALAMATKTP = "alamat_ktp";
    public static final String KEY_IPK = "ipk_mhs";
    public static final String KEY_SEMESTER = "semester";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_detail_proposal);

        init();

        progressBar.setProgress(0);
        progressBar.setMax(100);

        Bundle proposalIDbundle = getIntent().getExtras();
        String revIDproposal = Objects.requireNonNull(proposalIDbundle).getString("proposal_id");
        TVidProposal.setText(revIDproposal);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getFotoNamaNIM();
            } else {
                Intent i = new Intent(DetailProposal.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        btnSetuju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailProposal.this,R.style.ProgressBarMahasiswa);
                builder.setMessage("Anda yakin akan menyetujui proposal berikut?")
                        .setCancelable(true)
                        .setPositiveButton("Y A K I N", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateStatusProposal();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
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

    private void updateStatusProposal() {
        loadings = new ProgressDialog(DetailProposal.this,R.style.ProgressBarMahasiswa);
        loadings.setMessage("Mengganti status proposal ..");
        loadings.setCancelable(false);
        loadings.show();

        AndroidNetworking.post("https://prasyah.000webhostapp.com/gantiStatusDiterima.php")
                .addBodyParameter("proposal_id",""+TVidProposal)
                .setTag("Update Data")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadings.dismiss();
                        Log.d("responEdit",""+response);
                        try{
                            Boolean status = response.getBoolean("status");
                            if(status){
                                new AlertDialog.Builder(DetailProposal.this)
                                        .setMessage("Anda sudah merubah status proposalnya.")
                                        .setTitle("Berhasil")
                                        .setCancelable(false)
                                        .setPositiveButton("M E N U  U T A M A", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = getIntent();
                                                setResult(RESULT_OK,i);
                                                DetailProposal.this.finish();
                                            }
                                        })
                                        .show();
                            }else{
                                new AlertDialog.Builder(DetailProposal.this)
                                        .setMessage("Gagal Mengupdate Status Proposal")
                                        .setCancelable(false)
                                        .setPositiveButton("K E M B A L I", new DialogInterface.OnClickListener() {
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

    private void init() {
        TVidProposal = findViewById(R.id.tv_IdProposalDetailProposal);
        back = findViewById(R.id.btnBack_detailProposal);
        fotoMhsDetailProposal = findViewById(R.id.fotoMahasiswa_detailProposal);
        namaDetailProposal = findViewById(R.id.namaMahasiswa_detailProposal);
        NIMDetailProposal = findViewById(R.id.NIMMahasiswa_detailProposal);
        progressBar = findViewById(R.id.progressBarDetailProposal);
        etNamaAyah = findViewById(R.id.etNamaAyah_detailProposal);
        etNamaIbu = findViewById(R.id.etNamaIbu_detailProposal);
        etPekerjaanAyah = findViewById(R.id.etPekerjaanAyah_detailProposal);
        etPekerjaanIbu = findViewById(R.id.etPekerjaanIbu_detailProposal);
        etJumsau = findViewById(R.id.etJumlahSaudara_detailProposal);
        etAnakke = findViewById(R.id.etAnakKe_detailProposal);
        etGajiOrtu = findViewById(R.id.etPenghasilan_detailProposal);
        etUKT = findViewById(R.id.etUkt_detailProposal);
        etNamaBank = findViewById(R.id.etNamaBank_detailProposal);
        etNorek = findViewById(R.id.etNorek_detailProposal);
        etalamatDomisili = findViewById(R.id.etAlamatDomisili_detailProposal);
        etalamatKTP = findViewById(R.id.etAlamatKTP_detailProposal);
        etIPK = findViewById(R.id.etIPK_detailProposal);
        etSemester = findViewById(R.id.etSemester_detailProposal);
        btnSetuju = findViewById(R.id.btnTerimaProposal_detailProposal);
        btnTolak = findViewById(R.id.btnTolakProposal_detailProposal);
        btnPerbaiki = findViewById(R.id.btnPerbaikanProposal_detailProposal);
        btnUploadUlang = findViewById(R.id.btnUploadBerkasProposal_detailProposal);
    }

    void getDataProposalmahasiswa() {
        String NIMproposal = NIMDetailProposal.getText().toString();

        if (NIMproposal.equals("")) {
            Toast.makeText(this, "Server bermasalah", Toast.LENGTH_SHORT).show();
            return;
        }

        loadings = new ProgressDialog(this,R.style.ProgressBarMahasiswa);
        loadings.setCancelable(false);
        loadings.setMessage("Sedang mengambil data proposal..");
        loadings.show();

        final String urlDataProposalLengkap = URL_GETDATA_PROPOSAL+NIMproposal;

        StringRequest stringRequestDataProposal = new StringRequest(urlDataProposalLengkap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadings.dismiss();
                showJSONDataProposalLengkap(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailProposal.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequestDataProposal);
    }

    void getFotoNamaNIM(){
        String id = TVidProposal.getText().toString();

        if (id.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        loadings = new ProgressDialog(this,R.style.ProgressBarMahasiswa);
        loadings.setCancelable(false);
        loadings.setMessage("Mengambil foto..");
        loadings.show();

        final String urlDetailProposalMahasiswa                = URL_GETFOTONAMANIM+id;

        StringRequest loadDataFotoNamaNIM = new StringRequest(urlDetailProposalMahasiswa, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadings.dismiss();
                showFotoNamaNIMJSON(response);
                new AlertDialog.Builder(DetailProposal.this)
                        .setTitle("PERINGATAN!")
                        .setMessage("Data proposal mahasiswa termasuk data penting dan sensitif, harap berhati-hati dalam pengelolaan data ini. Lanjutkan?")
                        .setCancelable(true)
                        .setPositiveButton("L A N J U T K A N", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getDataProposalmahasiswa();
                            }
                        })
                        .show();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailProposal.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                        loadings.dismiss();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadDataFotoNamaNIM);
    }

    private void showFotoNamaNIMJSON(String response) {
        String image_path="";
        String nama="";
        String NIM="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            image_path = collegeData.getString(KEY_FOTO);
            nama = collegeData.getString(KEY_NAMA);
            NIM = collegeData.getString(KEY_NIM);

        } catch (JSONException e){
            e.printStackTrace();
        }

        Glide.with(DetailProposal.this).load(image_path).apply(RequestOptions.circleCropTransform()).into(fotoMhsDetailProposal);
        YoYo.with(Techniques.FadeInDown)
                .duration(750)
                .playOn(fotoMhsDetailProposal);
        namaDetailProposal.setText(""+nama);
        YoYo.with(Techniques.Landing).duration(1500).playOn(namaDetailProposal);
        NIMDetailProposal.setText(""+NIM);
        YoYo.with(Techniques.Landing).duration(1500).playOn(NIMDetailProposal);
    }

    private void showJSONDataProposalLengkap(String response){
        String namaAyah="";
        String namaIbu="";
        String pekerjaanAyah="";
        String pekerjaanIbu="";
        String jumsau="";
        String anakke="";
        String penghasilanOrtu="";
        String uktsemester="";
        String namaBank="";
        String norek="";
        String alamatDomisili="";
        String alamatKTP="";
        String IPK="";
        String semesterberjalan="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            namaAyah = collegeData.getString(KEY_NAMAAYAH);
            namaIbu = collegeData.getString(KEY_NAMAIBU);
            pekerjaanAyah = collegeData.getString(KEY_PEKERJAANAYAH);
            pekerjaanIbu = collegeData.getString(KEY_PEKERJAANIBU);
            jumsau = collegeData.getString(KEY_JUMSAU);
            anakke = collegeData.getString(KEY_ANAKKE);
            penghasilanOrtu = collegeData.getString(KEY_GAJIORTU);
            uktsemester = collegeData.getString(KEY_UKT);
            namaBank = collegeData.getString(KEY_NAMABANK);
            norek = collegeData.getString(KEY_NOREK);
            alamatDomisili = collegeData.getString(KEY_ALAMATDOMISILI);
            alamatKTP = collegeData.getString(KEY_ALAMATKTP);
            IPK = collegeData.getString(KEY_IPK);
            semesterberjalan = collegeData.getString(KEY_SEMESTER);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        etNamaAyah.setText(""+namaAyah);
        etNamaIbu.setText(""+namaIbu);
        etPekerjaanAyah.setText(""+pekerjaanAyah);
        etPekerjaanIbu.setText(""+pekerjaanIbu);
        etJumsau.setText(""+jumsau);
        etAnakke.setText(""+anakke);
        etGajiOrtu.setText(""+penghasilanOrtu);
        etUKT.setText(""+uktsemester);
        etNamaBank.setText(""+namaBank);
        etNorek.setText(""+norek);
        etalamatDomisili.setText(""+alamatDomisili);
        etalamatKTP.setText(""+alamatKTP);
        etIPK.setText(""+IPK);
        etSemester.setText(""+semesterberjalan);

        btnSetuju.setEnabled(true);
        btnTolak.setEnabled(true);
        btnPerbaiki.setEnabled(true);
        btnUploadUlang.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        overridePendingTransition(0,R.anim.slide_outta_right);
    }
}