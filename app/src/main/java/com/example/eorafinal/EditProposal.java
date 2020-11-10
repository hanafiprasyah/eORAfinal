package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class EditProposal extends AppCompatActivity {

    ConnectivityManager conMgr;
    Toolbar toolbar;
    TextView back,idProposal;
    MaterialEditText etNIM;
    NestedScrollView nestedScrollView;
    ImageView InfoJumlahSaudara,InfoAnakKe;
    Spinner spinFakultas,spinJurusan;
    private ProgressDialog loading;
    MaterialEditText etNIMedit,etNamaAyah,etNamaIbu,etPekerjaanAyah,etPekerjaanIbu,etJumsau,etAnakke,etGajiOrtu,etUKT,etNamaBank,etNorek,etAlamatDomisili,etAlamatKTP;

    //ArrayListFAKULTASdanJURUSAN
    ArrayList<String> arrayList_Fakultas;
    ArrayList<String> arrayList_FIP,arrayList_FBS,arrayList_FMIPA,arrayList_FIS,arrayList_FT,arrayList_FIK,arrayList_FE,arrayList_FPP;

    //ArrayAdapter
    ArrayAdapter<String> arrayAdapter_Fakultas;
    ArrayAdapter<String> arrayAdapter_Jurusan;

    Button saveEditProposal;

    //prevent double click
    private long mLastClickTime = 0;

    String proposal_id,NIMbaru,fakultasBaru,jurusanBaru,statusBaru="menunggu",namaAyahBaru,namaIbuBaru,pekerjaanAyahBaru,pekerjaanIbuBaru,jumsauBaru,anakKeBaru,gajiOrtuBaru,uktBaru,namaBankBaru,norekBaru,adomBaru,aKTPBaru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_edit_proposal);

        idProposal  = findViewById(R.id.tv_proposalID);
        etNIMedit   = findViewById(R.id.et_NIMedit);
        etNamaAyah  = findViewById(R.id.et_namaAyahEdit);
        etNamaIbu   = findViewById(R.id.et_namaIbuEdit);
        etPekerjaanAyah = findViewById(R.id.et_pekerjaanAyahEdit);
        etPekerjaanIbu  = findViewById(R.id.et_pekerjaanIbuEdit);
        etJumsau   = findViewById(R.id.et_jumlahSaudaraEdit);
        etAnakke   = findViewById(R.id.et_anakKeEdit);
        etGajiOrtu = findViewById(R.id.et_penghasilanEdit);
        etUKT      = findViewById(R.id.et_uktEdit);
        etNamaBank = findViewById(R.id.et_namaBankEdit);
        etNorek = findViewById(R.id.et_norekEdit);
        etAlamatDomisili = findViewById(R.id.et_alamatDomisiliEdit);
        etAlamatKTP = findViewById(R.id.et_alamatKTPEdit);
        etAlamatKTP.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboard(v);
                }
            }
        });

        Bundle b = getIntent().getExtras();
        String revNIMedit = Objects.requireNonNull(b).getString("NIM");
        etNIM = findViewById(R.id.et_NIMedit);
        etNIM.setText(revNIMedit);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getData();
            } else {
                Intent i = new Intent(EditProposal.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        saveEditProposal = findViewById(R.id.save_toServerEditProposal);
        saveEditProposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                hideKeyboardAfter(EditProposal.this);

                proposal_id = idProposal.getText().toString();
                NIMbaru = etNIMedit.getText().toString();
                fakultasBaru = spinFakultas.getSelectedItem().toString();
                jurusanBaru = spinJurusan.getSelectedItem().toString();
                namaAyahBaru = etNamaAyah.getText().toString();
                namaIbuBaru = etNamaIbu.getText().toString();
                pekerjaanAyahBaru = etPekerjaanAyah.getText().toString();
                pekerjaanIbuBaru = etPekerjaanIbu.getText().toString();
                jumsauBaru = etJumsau.getText().toString();
                anakKeBaru = etAnakke.getText().toString();
                gajiOrtuBaru = etGajiOrtu.getText().toString();
                uktBaru = etUKT.getText().toString();
                namaBankBaru = etNamaBank.getText().toString();
                norekBaru = etNorek.getText().toString();
                adomBaru = etAlamatDomisili.getText().toString();
                aKTPBaru = etAlamatKTP.getText().toString();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        validasiData();
                    }
                },1000);
            }
        });

        toolbar = findViewById(R.id.toolbar_editAjuProMhs);
        TextView toolbarText = findViewById(R.id.toolbar_EditProMhs);
        if (toolbarText != null && toolbar != null){
            setSupportActionBar(toolbar);
        }

        //Popup Window-----------------------------------------------------------
        InfoJumlahSaudara = findViewById(R.id.info_jumlahsaudara);
        final Dialog jumsau = new Dialog(this, R.style.DialogAnimationFadeIn);
        InfoJumlahSaudara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                jumsau.setContentView(R.layout.popup_infojumsau);
                Objects.requireNonNull(jumsau.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                jumsau.show();
                //overridePendingTransition(R.anim.slide_out,R.anim.stay);
            }
        });

        InfoAnakKe = findViewById(R.id.info_anakke);
        final Dialog anakke = new Dialog(this, R.style.DialogAnimationFadeIn);
        InfoAnakKe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                anakke.setContentView(R.layout.popup_infoanakke);
                Objects.requireNonNull(anakke.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                anakke.show();
                //overridePendingTransition(R.anim.slide_out,R.anim.stay);
            }
        });
        //--------------------------------------------------------------------------

        nestedScrollView = findViewById(R.id.nestedViewEditProposal);
        if (nestedScrollView != null){
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    View view = getCurrentFocus();
                    if (scrollY > oldScrollY) {
                        //ScrollDown
                        if (view == null){
                            view = new View(getApplicationContext());
                        }
                        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                    }
                    if (scrollY < oldScrollY) {
                        //ScrollUp
                    }
                    if (scrollY == 0) {
                        //TopOfView
                    }
                    //BottomOfView
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        if (view == null){
                            view = new View(getApplicationContext());
                        }
                        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                    }
                }
            });
        }

        //------------------------------ Proses Pemilihan Jurusan ------------------------------------------

        spinFakultas = findViewById(R.id.fakultas_spinnerEdit);
        spinJurusan = findViewById(R.id.jurusan_spinnerEdit);

        arrayList_Fakultas = new ArrayList<>();
        arrayList_Fakultas.add("FIP");
        arrayList_Fakultas.add("FBS");
        arrayList_Fakultas.add("FMIPA");
        arrayList_Fakultas.add("FIS");
        arrayList_Fakultas.add("FT");
        arrayList_Fakultas.add("FIK");
        arrayList_Fakultas.add("FE");
        arrayList_Fakultas.add("FPP");

        arrayAdapter_Fakultas = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayList_Fakultas);
        spinFakultas.setAdapter(arrayAdapter_Fakultas);

        arrayFIP();
        arrayFBS();
        arrayFMIPA();
        arrayFIS();
        arrayFT();
        arrayFIK();
        arrayFE();
        arrayFPP();

        spinFakultas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    arrayAdapter_Jurusan = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayList_FIP);
                }
                if (position==1){
                    arrayAdapter_Jurusan = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayList_FBS);
                }
                if (position==2){
                    arrayAdapter_Jurusan = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayList_FMIPA);
                }
                if (position==3){
                    arrayAdapter_Jurusan = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayList_FIS);
                }
                if (position==4){
                    arrayAdapter_Jurusan = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayList_FT);
                }
                if (position==5){
                    arrayAdapter_Jurusan = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayList_FIK);
                }
                if (position==6){
                    arrayAdapter_Jurusan = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayList_FE);
                }
                if (position==7){
                    arrayAdapter_Jurusan = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayList_FPP);
                }
                spinJurusan.setAdapter(arrayAdapter_Jurusan);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //------------------------------ FINISH ------------------------------------------

        back = findViewById(R.id.btn_BackToHomeMhs);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                finishAndRemoveTask();
            }
        });
    }

    private void validasiData() {
        if(NIMbaru.equals("") || namaAyahBaru.equals("") || namaIbuBaru.equals("") || pekerjaanAyahBaru.equals("") ||
                pekerjaanIbuBaru.equals("") || jumsauBaru.equals("") || anakKeBaru.equals("") || gajiOrtuBaru.equals("") ||
                uktBaru.equals("") || namaBankBaru.equals("") || norekBaru.equals("") || adomBaru.equals("") || aKTPBaru.equals("")){
            loading.dismiss();
            Toast.makeText(EditProposal.this, "Periksa kembali data yang anda masukkan!", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditProposal.this,R.style.ProgressBarMahasiswa);
            builder.setMessage("Anda yakin sudah memilih Fakultas dan Jurusan yang sesuai? ")
                    .setCancelable(true)
                    .setPositiveButton("YAKIN", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateProposal();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void updateProposal() {
        loading = new ProgressDialog(EditProposal.this,R.style.ProgressBarMahasiswa);
        loading.setMessage("Mengganti Data...");
        loading.setCancelable(false);
        loading.show();

        AndroidNetworking.post("https://prasyah.000webhostapp.com/editProposal.php")
                .addBodyParameter("proposal_id",""+proposal_id)
                .addBodyParameter("NIM",""+NIMbaru)
                .addBodyParameter("fakultas",""+fakultasBaru)
                .addBodyParameter("jurusan",""+jurusanBaru)
                .addBodyParameter("status",""+statusBaru)
                .addBodyParameter("nama_ayah",""+namaAyahBaru)
                .addBodyParameter("nama_ibu",""+namaIbuBaru)
                .addBodyParameter("pekerjaan_ayah",""+pekerjaanAyahBaru)
                .addBodyParameter("pekerjaan_ibu",""+pekerjaanIbuBaru)
                .addBodyParameter("jumlah_saudara",""+jumsauBaru)
                .addBodyParameter("anak_ke",""+anakKeBaru)
                .addBodyParameter("gaji_ortu",""+gajiOrtuBaru)
                .addBodyParameter("ukt_mhs",""+uktBaru)
                .addBodyParameter("nama_bank",""+namaBankBaru)
                .addBodyParameter("norek",""+norekBaru)
                .addBodyParameter("alamat_domisili",""+adomBaru)
                .addBodyParameter("alamat_ktp",""+aKTPBaru)
                .setTag("Update Data")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Log.d("responEdit",""+response);
                        try{
                            Boolean status = response.getBoolean("status");
                            if(status){
                                new AlertDialog.Builder(EditProposal.this)
                                        .setMessage("Selanjutnya, mohon lengkapi ulang berkas anda!")
                                        .setTitle("Berhasil")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = getIntent();
                                                setResult(RESULT_OK,i);
                                                EditProposal.this.finish();
                                            }
                                        })
                                        .show();
                            }else{
                                new AlertDialog.Builder(EditProposal.this)
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


    private void getData() {
        String NIM = etNIM.getText().toString();

        if (NIM.equals("")) {
            Toast.makeText(this, "Server bermasalah", Toast.LENGTH_SHORT).show();
            return;
        }

        loading = new ProgressDialog(this,R.style.ProgressBarMahasiswa);
        loading.setCancelable(false);
        loading.setMessage("Sedang mengambil proposal..");
        loading.setTitle("Mohon Tunggu");
        loading.show();

        final String getDataProposal = ConfigProfileMahasiswa.DATA_EDITPROPOSAL+etNIM.getText().toString();

        StringRequest stringRequest = new StringRequest(getDataProposal, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditProposal.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response) {
        String IDproposal="";
        String namaAyah="";
        String namaIbu="";
        String pekerjaanAyah="";
        String pekerjaanIbu="";
        String jumsau="";
        String anakke="";
        String penghasilanortu="";
        String ukt="";
        String namaBank="";
        String norek="";
        String alamatDomisili="";
        String alamatKTP="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            IDproposal = collegeData.getString(ConfigProfileMahasiswa.KEY_IDPROPOSAL);
            namaAyah = collegeData.getString(ConfigProfileMahasiswa.KEY_NAMAAYAH);
            namaIbu = collegeData.getString(ConfigProfileMahasiswa.KEY_NAMAIBU);
            pekerjaanAyah = collegeData.getString(ConfigProfileMahasiswa.KEY_PEKERJAANAYAH);
            pekerjaanIbu = collegeData.getString(ConfigProfileMahasiswa.KEY_PEKERJAANIBU);
            jumsau = collegeData.getString(ConfigProfileMahasiswa.KEY_JUMSAU);
            anakke = collegeData.getString(ConfigProfileMahasiswa.KEY_ANAKKE);
            penghasilanortu = collegeData.getString(ConfigProfileMahasiswa.KEY_GAJIORTU);
            ukt = collegeData.getString(ConfigProfileMahasiswa.KEY_UKT);
            namaBank = collegeData.getString(ConfigProfileMahasiswa.KEY_NAMABANK);
            norek = collegeData.getString(ConfigProfileMahasiswa.KEY_NOREK);
            alamatDomisili = collegeData.getString(ConfigProfileMahasiswa.KEY_ALAMATDOMISILI);
            alamatKTP = collegeData.getString(ConfigProfileMahasiswa.KEY_ALAMATKTP);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        idProposal.setText(""+IDproposal);
        etNamaAyah.setText(""+namaAyah);
        etNamaIbu.setText(""+namaIbu);
        etPekerjaanAyah.setText(""+pekerjaanAyah);
        etPekerjaanIbu.setText(""+pekerjaanIbu);
        etJumsau.setText(""+jumsau);
        etAnakke.setText(""+anakke);
        etGajiOrtu.setText(""+penghasilanortu);
        etUKT.setText(""+ukt);
        etNamaBank.setText(""+namaBank);
        etNorek.setText(""+norek);
        etAlamatDomisili.setText(""+alamatDomisili);
        etAlamatKTP.setText(""+alamatKTP);
    }

    public void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    private void arrayFIP(){
        arrayList_FIP = new ArrayList<>();
        arrayList_FIP.add("S1 Administrasi Pendidikan");
        arrayList_FIP.add("S1 Bimbingan Konseling");
        arrayList_FIP.add("S1 PGPAUD");
        arrayList_FIP.add("S1 PGSD");
        arrayList_FIP.add("S1 Pendidikan Luar Biasa");
        arrayList_FIP.add("S1 Psikologi");
        arrayList_FIP.add("S1 Teknologi Pendidikan");
    }
    private void arrayFBS(){
        arrayList_FBS = new ArrayList<>();
        arrayList_FBS.add("D3 Ilmu Informasi Perpustakaan dan Kearsipan");
        arrayList_FBS.add("S1 Desain Komunikasi Visual");
        arrayList_FBS.add("S1 Perpustakaan dan Ilmu Informasi");
        arrayList_FBS.add("S1 Pendidikan Bahasa dan Sasta Indonesia");
        arrayList_FBS.add("S1 Pendidikan Bahasa Jepang");
        arrayList_FBS.add("S1 Pendidikan Bahasa Inggris");
        arrayList_FBS.add("S1 Pendidikan Seni Drama Tari dan Musik");
        arrayList_FBS.add("S1 Pendidikan Seni Rupa");
        arrayList_FBS.add("S1 Pendidikan Musik");
        arrayList_FBS.add("S1 Sastra Indonesia");
        arrayList_FBS.add("S1 Sastra Inggris");
        arrayList_FBS.add("S1 Pendidikan Tari");
    }
    private void arrayFMIPA(){
        arrayList_FMIPA = new ArrayList<>();
        arrayList_FMIPA.add("D3 Statistika");
        arrayList_FMIPA.add("S1 Biologi");
        arrayList_FMIPA.add("S1 Fisika");
        arrayList_FMIPA.add("S1 Kimia");
        arrayList_FMIPA.add("S1 Matematika");
        arrayList_FMIPA.add("S1 Pendidikan Biologi");
        arrayList_FMIPA.add("S1 Pendidikan Fisika");
        arrayList_FMIPA.add("S1 Pendidikan Kimia");
        arrayList_FMIPA.add("S1 Pendidikan Matematika");
        arrayList_FMIPA.add("S1 Pendidikan IPA");
        arrayList_FMIPA.add("S1 Statistika");
    }
    private void arrayFIS(){
        arrayList_FIS = new ArrayList<>();
        arrayList_FIS.add("D3 Teknologi Penginderaan Jauh");
        arrayList_FIS.add("S1 Geografi");
        arrayList_FIS.add("S1 Ilmu Administrasi Negara");
        arrayList_FIS.add("S1 Pendidikan Geografi");
        arrayList_FIS.add("S1 Pendidikan Pancasila dan Kewarganegaraan");
        arrayList_FIS.add("S1 Pendidikan Sejarah");
        arrayList_FIS.add("S1 Pendidikan Sosiologi");
        arrayList_FIS.add("S1 Pendidikan Keagamaan Islam");
    }
    private void arrayFT(){
        arrayList_FT = new ArrayList<>();
        arrayList_FT.add("D3 Teknik Otomotif");
        arrayList_FT.add("D3 Teknik Elektronika");
        arrayList_FT.add("D3 Teknik Mesin");
        arrayList_FT.add("D3 Teknik Pertambangan");
        arrayList_FT.add("D3 Teknik Sipil");
        arrayList_FT.add("D4 Teknik Elektro Industri");
        arrayList_FT.add("S1 Pendidikan Teknik Bangunan");
        arrayList_FT.add("S1 Pendidikan Teknik Elektro");
        arrayList_FT.add("S1 Pendidikan Teknik Elektronika");
        arrayList_FT.add("S1 Pendidikan Teknik Informatika");
        arrayList_FT.add("S1 Pendidikan Teknik Mesin");
        arrayList_FT.add("S1 Pendidikan Teknik Otomotif");
        arrayList_FT.add("S1 Teknik Pertambangan");
        arrayList_FT.add("S1 Teknik Sipil");
        arrayList_FT.add("S1 Teknik Mesin");
    }
    private void arrayFIK(){
        arrayList_FIK = new ArrayList<>();
        arrayList_FIK.add("D3 Keperawatan");
        arrayList_FIK.add("S1 Pendidikan Jasmani Kesehatan dan Rekreasi");
        arrayList_FIK.add("S1 Pendidikan Kepelatihan Olahraga");
    }
    private void arrayFE(){
        arrayList_FE = new ArrayList<>();
        arrayList_FE.add("D3 Akuntansi");
        arrayList_FE.add("D3 Manajemen Perdagangan");
        arrayList_FE.add("D3 Manajemen Pajak");
        arrayList_FE.add("S1 Akuntansi");
        arrayList_FE.add("S1 Ekonomi Pembangunan");
        arrayList_FE.add("S1 Manajemen");
        arrayList_FE.add("S1 Pendidikan Ekonomi");
    }
    private void arrayFPP(){
        arrayList_FPP = new ArrayList<>();
        arrayList_FPP.add("D3 Tata Boga");
        arrayList_FPP.add("D3 Tata Busana");
        arrayList_FPP.add("D4 Manajemen Perhotelan");
        arrayList_FPP.add("D4 Pendidikan Tata Rias dan Kecantikan");
        arrayList_FPP.add("S1 Pendidikan Kesejahteraan Keluarga");
    }

    public static void hideKeyboardAfter(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null){
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        overridePendingTransition(0,R.anim.slide_outta_right);
    }
}