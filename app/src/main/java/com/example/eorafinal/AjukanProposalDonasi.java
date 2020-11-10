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
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
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
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class AjukanProposalDonasi extends AppCompatActivity {

    Toolbar toolbar;
    TextView back;
    ConnectivityManager conMgr;
    NestedScrollView nestedScrollView;

    //ForSkorKriteria
    String getKriteria_jumlahAnak,getKriteria_penghasilanOrtu,getKriteria_UKT,getKriteria_IPK,getKriteria_semester;
    TextView TVvalueJumlahAnak,TVvaluePenghasilanOrtu,TVvalueUKT,TVvalueIPK,TVvalueSemester;
    Double setValueJumlahAnak,setValuePenghasilanOrtu,setValueUKT,setValueIPK,setValueSemester;
    //_______________

    private static final String TAG = "AjukanProposalDonasi"; //untuk melihat log
    //aksi Input-----------------
    Button btnSaveProposal;
    MaterialEditText edtNimForeign,edtNamaAyah,edtNamaIbu,edtKerjaAyah,edtKerjaIbu,edtJumSau,edtAnakke,edtGajiOrtu,edtUKT,edtNamaBank,edtNorek,edtAlamatDomisili,edtAlamatKTP,edtPrestasi1,edtPrestasi2,edtPrestasi3,edtPrestasi4,edtPrestasi5;
    TextView resultIPK;
    Spinner spinSemester,spinKategori1,spinKategori2,spinKategori3,spinKategori4,spinKategori5,spinTingkat1,spinTingkat2,spinTingkat3,spinTingkat4,spinTingkat5,spinFakultas,spinJurusan;
    //------------------

    //prevent double click
    private long mLastClickTime = 0;

    //SharedPreferences
    String revNIM;

    ImageView InfoJumlahSaudara;
    ImageView InfoAnakKe;

    private ProgressDialog progressDialog;

    String NIMforeign_send,fakultas_send,jurusan_send,status_send="menunggu",namaAyah_send,namaIbu_send,kerjaAyah_send,kerjaIbu_send,jumSau_send,anakKe_send,gajiOrtu_send,ukt_send,namaBank_send,norek_send,alamatDomisili_send,alamatKTP_send,IPK_send,semester_send,prestasi1_send,kategori1_send,tingkat1_send,prestasi2_send,kategori2_send,tingkat2_send,prestasi3_send,kategori3_send,tingkat3_send,prestasi4_send,kategori4_send,tingkat4_send,prestasi5_send,kategori5_send,tingkat5_send;
    String sendValueNIM,sendValuePenghasilanOrtu,sendValueUKT,sendValueJumlahAnak,sendValueSemester,sendValueIPK;
    String valueNIMforRank;

    //ArrayListFAKULTASdanJURUSAN
    ArrayList<String> arrayList_Fakultas;
    ArrayList<String> arrayList_FIP,arrayList_FBS,arrayList_FMIPA,arrayList_FIS,arrayList_FT,arrayList_FIK,arrayList_FE,arrayList_FPP;

    //ArrayAdapter
    ArrayAdapter<String> arrayAdapter_Fakultas;
    ArrayAdapter<String> arrayAdapter_Jurusan;

    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_ajukan_proposaldonasi);

        init();
        AndroidNetworking.initialize(getApplicationContext());
        simpanProposal();

        Bundle obj = getIntent().getExtras();
        revNIM = Objects.requireNonNull(obj).getString("NIM");
        edtNimForeign.setText(revNIM);
        edtNimForeign.setEnabled(false);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                edtNimForeign.setEnabled(false);
            } else {
                Intent i = new Intent(AjukanProposalDonasi.this,NetworkErrorActivity.class);
                startActivity(i);
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }
        toolbar = findViewById(R.id.toolbarAjuProMhs);

        TextView toolbarText = findViewById(R.id.toolbar_textMhs);
        if (toolbarText != null && toolbar != null){
            toolbarText.setText("PROPOSAL DONASI MAHASISWA");
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

        //edtGajiOrtu.addTextChangedListener(new NumberTextWatcher(edtGajiOrtu));
        //edtUKT.addTextChangedListener(new NumberTextWatcher(edtUKT));

        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(400);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                resultIPK.setText(Float.toString((float) (progress)/100));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        nestedScrollView = findViewById(R.id.nestedView);
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

        //-------LISTENER UNTUK MENGGANTI VALUE TIAP KRITERIA-------
        edtJumSau.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s!=null && !s.toString().equalsIgnoreCase("")){
                    if (edtJumSau.getText().hashCode() == s.hashCode()){
                        UpdateUI_jumlahAnak();
                    }
                }
            }
        });
        edtGajiOrtu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s!=null && !s.toString().equalsIgnoreCase("")){
                    if (edtGajiOrtu.getText().hashCode() == s.hashCode()){
                        UpdateUI_penghasilanOrtu();
                    }
                }
            }
        });
        edtUKT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s!=null && !s.toString().equalsIgnoreCase("")){
                    if (edtUKT.getText().hashCode() == s.hashCode()){
                        UpdateUI_UKT();
                    }
                }
            }
        });
        resultIPK.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s!=null && !s.toString().equalsIgnoreCase("")){
                    if (resultIPK.getText().hashCode() == s.hashCode()){
                        UpdateUI_IPK();
                    }
                }
            }
        });

        spinSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int semesterChoose = position;
                switch (semesterChoose){
                    case 0 : setValueSemester = 0.0;TVvalueSemester.setText(String.valueOf(setValueSemester));break;
                    case 1 : setValueSemester = 1.0;TVvalueSemester.setText(String.valueOf(setValueSemester));break;
                    case 2 : setValueSemester = 1.0;TVvalueSemester.setText(String.valueOf(setValueSemester));break;
                    case 3 : setValueSemester = 0.8;TVvalueSemester.setText(String.valueOf(setValueSemester));break;
                    case 4 : setValueSemester = 0.6;TVvalueSemester.setText(String.valueOf(setValueSemester));break;
                    case 5 : setValueSemester = 0.4;TVvalueSemester.setText(String.valueOf(setValueSemester));break;
                    case 6 : setValueSemester = 0.2;TVvalueSemester.setText(String.valueOf(setValueSemester));break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //__________________________________________________________

        back = findViewById(R.id.btnBacktoMhs);
        back.setOnClickListener(v -> {
            //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            finishAfterTransition();
            overridePendingTransition(0,R.anim.slide_outta_right);
        });
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

    private void simpanProposal() {
        btnSaveProposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                progressDialog = new ProgressDialog(AjukanProposalDonasi.this,R.style.ProgressBarMahasiswa);
                progressDialog.setMessage("Mengajukan proposal anda..");
                progressDialog.setCancelable(false);
                progressDialog.show();
                hideKeyboard(AjukanProposalDonasi.this);

                NIMforeign_send         = edtNimForeign.getText().toString();
                //status = ""
                fakultas_send           = spinFakultas.getSelectedItem().toString();
                jurusan_send            = spinJurusan.getSelectedItem().toString();
                namaAyah_send           = edtNamaAyah.getText().toString();
                namaIbu_send            = edtNamaIbu.getText().toString();
                kerjaAyah_send          = edtKerjaAyah.getText().toString();
                kerjaIbu_send           = edtKerjaIbu.getText().toString();
                jumSau_send             = edtJumSau.getText().toString();
                anakKe_send             = edtAnakke.getText().toString();
                gajiOrtu_send           = edtGajiOrtu.getText().toString();
                ukt_send                = edtUKT.getText().toString();
                namaBank_send           = edtNamaBank.getText().toString();
                norek_send              = edtNorek.getText().toString();
                alamatDomisili_send     = edtAlamatDomisili.getText().toString();
                alamatKTP_send          = edtAlamatKTP.getText().toString();
                IPK_send                = resultIPK.getText().toString();
                prestasi1_send          = edtPrestasi1.getText().toString();
                prestasi2_send          = edtPrestasi2.getText().toString();
                prestasi3_send          = edtPrestasi3.getText().toString();
                prestasi4_send          = edtPrestasi4.getText().toString();
                prestasi5_send          = edtPrestasi5.getText().toString();
                semester_send           = spinSemester.getSelectedItem().toString();
                kategori1_send          = spinKategori1.getSelectedItem().toString();
                kategori2_send          = spinKategori2.getSelectedItem().toString();
                kategori3_send          = spinKategori3.getSelectedItem().toString();
                kategori4_send          = spinKategori4.getSelectedItem().toString();
                kategori5_send          = spinKategori5.getSelectedItem().toString();
                tingkat1_send           = spinTingkat1.getSelectedItem().toString();
                tingkat2_send           = spinTingkat2.getSelectedItem().toString();
                tingkat3_send           = spinTingkat3.getSelectedItem().toString();
                tingkat4_send           = spinTingkat4.getSelectedItem().toString();
                tingkat5_send           = spinTingkat5.getSelectedItem().toString();

                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(namaAyah_send.equals("") || namaIbu_send.equals("") || kerjaAyah_send.equals("") || kerjaIbu_send.equals("") ||
                                    jumSau_send.equals("") || anakKe_send.equals("") || gajiOrtu_send.equals("") ||
                                    ukt_send.equals("") || namaBank_send.equals("") || norek_send.equals("") || alamatDomisili_send.equals("") ||
                                    alamatKTP_send.equals("") || IPK_send.equals("IPK") || semester_send.equals("")){
                                progressDialog.dismiss();
                                FancyToast.makeText(AjukanProposalDonasi.this,"Semua data harus diisi!",FancyToast.LENGTH_SHORT, FancyToast.ERROR, R.drawable.ic_errorwhite24, false).show();
                            } else {
                                if (jumSau_send.isEmpty() || gajiOrtu_send.isEmpty() || ukt_send.isEmpty()){
                                    progressDialog.dismiss();
                                    FancyToast.makeText(AjukanProposalDonasi.this,"Data Jumlah Saudara, Penghasilan Orang Tua dan UKT harus diisi!",FancyToast.LENGTH_SHORT, FancyToast.ERROR, R.drawable.ic_errorwhite24, false).show();
                                } else {
                                    UploadProposal(NIMforeign_send,fakultas_send,jurusan_send,status_send,namaAyah_send,namaIbu_send,kerjaAyah_send,kerjaIbu_send,jumSau_send,anakKe_send,gajiOrtu_send,ukt_send,namaBank_send,norek_send,alamatDomisili_send,alamatKTP_send,IPK_send,semester_send,prestasi1_send,kategori1_send,tingkat1_send,prestasi2_send,kategori2_send,tingkat2_send,prestasi3_send,kategori3_send,tingkat3_send,prestasi4_send,kategori4_send,tingkat4_send,prestasi5_send,kategori5_send,tingkat5_send);
                                }
                            }
                        }
                    },1000);
                } else {
                    Intent i = new Intent(AjukanProposalDonasi.this,NetworkErrorActivity.class);
                    startActivity(i);
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void UploadProposal(String NIMforeign_send, String fakultas_send, String jurusan_send, String status_send, String namaAyah_send, String namaIbu_send, String kerjaAyah_send, String kerjaIbu_send, String jumSau_send, String anakKe_send, String gajiOrtu_send, String ukt_send, String namaBank_send, String norek_send, String alamatDomisili_send, String alamatKTP_send, String IPK_send, String semester_send, String prestasi1_send, String kategori1_send, String tingkat1_send, String prestasi2_send, String kategori2_send, String tingkat2_send, String prestasi3_send, String kategori3_send, String tingkat3_send, String prestasi4_send, String kategori4_send, String tingkat4_send, String prestasi5_send, String kategori5_send, String tingkat5_send) {
        AndroidNetworking.post("https://prasyah.000webhostapp.com/inputProposal.php")
                .addBodyParameter("proposal_id","")
                .addBodyParameter("NIM", NIMforeign_send)
                .addBodyParameter("fakultas", fakultas_send)
                .addBodyParameter("jurusan", jurusan_send)
                .addBodyParameter("status",status_send)
                .addBodyParameter("nama_ayah", namaAyah_send)
                .addBodyParameter("nama_ibu", namaIbu_send)
                .addBodyParameter("pekerjaan_ayah", kerjaAyah_send)
                .addBodyParameter("pekerjaan_ibu", kerjaIbu_send)
                .addBodyParameter("jumlah_saudara", jumSau_send)
                .addBodyParameter("anak_ke", anakKe_send)
                .addBodyParameter("gaji_ortu", gajiOrtu_send)
                .addBodyParameter("ukt_mhs",ukt_send)
                .addBodyParameter("nama_bank", namaBank_send)
                .addBodyParameter("norek", norek_send)
                .addBodyParameter("alamat_domisili", alamatDomisili_send)
                .addBodyParameter("alamat_ktp", alamatKTP_send)
                .addBodyParameter("ipk_mhs", IPK_send)
                .addBodyParameter("semester", semester_send)
                .addBodyParameter("prestasi1", prestasi1_send)
                .addBodyParameter("kategori1", kategori1_send)
                .addBodyParameter("tingkat1", tingkat1_send)
                .addBodyParameter("prestasi2", prestasi2_send)
                .addBodyParameter("kategori2", kategori2_send)
                .addBodyParameter("tingkat2", tingkat2_send)
                .addBodyParameter("prestasi3", prestasi3_send)
                .addBodyParameter("kategori3", kategori3_send)
                .addBodyParameter("tingkat3", tingkat3_send)
                .addBodyParameter("prestasi4", prestasi4_send)
                .addBodyParameter("kategori4", kategori4_send)
                .addBodyParameter("tingkat4", tingkat4_send)
                .addBodyParameter("prestasi5", prestasi5_send)
                .addBodyParameter("kategori5", kategori5_send)
                .addBodyParameter("tingkat5", tingkat5_send)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,""+response);
                        new AlertDialog.Builder(AjukanProposalDonasi.this)
                                .setMessage("Sistem ingin menghitung skor anda")
                                .setCancelable(false)
                                .setPositiveButton("LANJUTKAN", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        simpanSkor();
                                    }
                                })
                                .show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG,"errorFailed" +anError);
                        FancyToast.makeText(AjukanProposalDonasi.this,"Data gagal ditambahkan",FancyToast.LENGTH_SHORT, FancyToast.ERROR, R.drawable.ic_errorwhite24, false).show();
                    }
                });
    }

    private void simpanSkor() {
        progressDialog = new ProgressDialog(AjukanProposalDonasi.this,R.style.ProgressBarMahasiswa);
        progressDialog.setMessage("Menghitung skor ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        sendValueNIM                        = edtNimForeign.getText().toString();
        sendValuePenghasilanOrtu            = TVvaluePenghasilanOrtu.getText().toString();
        sendValueIPK                        = TVvalueIPK.getText().toString();
        sendValueSemester                   = TVvalueSemester.getText().toString();
        sendValueUKT                        = TVvalueUKT.getText().toString();
        sendValueJumlahAnak                 = TVvalueJumlahAnak.getText().toString();

        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UploadSkor(sendValueNIM,sendValuePenghasilanOrtu,sendValueIPK,sendValueJumlahAnak,sendValueUKT,sendValueSemester);
                }
            },5000);
        } else {
            Intent i = new Intent(AjukanProposalDonasi.this,NetworkErrorActivity.class);
            startActivity(i);
            progressDialog.dismiss();
        }
    }

    private void UploadSkor(String sendValueNIM, String sendValuePenghasilanOrtu, String sendValueIPK, String sendValueJumlahAnak, String sendValueUKT, String sendValueSemester) {
        AndroidNetworking.post("https://prasyah.000webhostapp.com/sendSkor.php")
                .addBodyParameter("id_skor","")
                .addBodyParameter("NIM", sendValueNIM)
                .addBodyParameter("kriteria_gaji", sendValuePenghasilanOrtu)
                .addBodyParameter("kriteria_ipk", sendValueIPK)
                .addBodyParameter("kriteria_jumlahAnak",sendValueJumlahAnak)
                .addBodyParameter("kriteria_ukt",sendValueUKT)
                .addBodyParameter("kriteria_semester",sendValueSemester)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,""+response);
                        new AlertDialog.Builder(AjukanProposalDonasi.this)
                                .setMessage("Skor anda sudah terdaftar!")
                                .setCancelable(false)
                                .setPositiveButton("HITUNG RANKING", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        hitungRanking();
//                                        Intent i = getIntent();
//                                        setResult(RESULT_OK,i);
//                                        TesMetode.this.finishAfterTransition();
//                                        overridePendingTransition(R.anim.fade_in,0);
                                    }
                                })
                                .show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG,"errorFailed" +anError);
                        FancyToast.makeText(AjukanProposalDonasi.this,"Skor gagal ditambahkan",FancyToast.LENGTH_SHORT, FancyToast.ERROR, R.drawable.ic_errorwhite24, false).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void hitungRanking(){
        progressDialog = new ProgressDialog(AjukanProposalDonasi.this,R.style.ProgressBarMahasiswa);
        progressDialog.setMessage("Menghitung ranking anda ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        valueNIMforRank = edtNimForeign.getText().toString();

        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UploadRanking(valueNIMforRank);
                }
            },6000);
        } else {
            Intent i = new Intent(AjukanProposalDonasi.this,NetworkErrorActivity.class);
            startActivity(i);
            progressDialog.dismiss();
        }
    }

    private void UploadRanking(String valueNIMforRank){
        AndroidNetworking.post("https://prasyah.000webhostapp.com/hitungSkor.php")
                .addBodyParameter("id_rank","")
                .addBodyParameter("NIM", valueNIMforRank)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,""+response);
                        new AlertDialog.Builder(AjukanProposalDonasi.this)
                                .setTitle("Proposal anda sudah terdaftar!")
                                .setMessage("Silakan upload berkas untuk melanjutkan verifikasi data proposal anda. Geser ke bawah untuk refresh data.")
                                .setCancelable(false)
                                .setPositiveButton("KEMBALI", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = getIntent();
                                        setResult(RESULT_OK,i);
                                        AjukanProposalDonasi.this.finishAfterTransition();
                                        overridePendingTransition(R.anim.fade_in,0);
                                    }
                                })
                                .show();
                    }
                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG,"errorFailed" +anError);
                        FancyToast.makeText(AjukanProposalDonasi.this,"Ranking gagal ditambahkan",FancyToast.LENGTH_SHORT, FancyToast.ERROR, R.drawable.ic_errorwhite24, false).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void init() {
        //EditText
        edtNimForeign = findViewById(R.id.et_NIMforeign);
        edtNamaAyah = findViewById(R.id.et_namaAyah);
        edtNamaIbu = findViewById(R.id.et_namaIbu);
        edtKerjaAyah = findViewById(R.id.et_pekerjaanAyah);
        edtKerjaIbu = findViewById(R.id.et_pekerjaanIbu);
        edtJumSau = findViewById(R.id.et_jumlahSaudara);
        edtAnakke = findViewById(R.id.et_anakKe);
        edtGajiOrtu = findViewById(R.id.et_penghasilan);
        edtUKT = findViewById(R.id.et_ukt);
        edtNamaBank = findViewById(R.id.et_namaBank);
        edtNorek = findViewById(R.id.et_norek);
        edtAlamatDomisili = findViewById(R.id.et_alamatDomisili);
        edtAlamatKTP = findViewById(R.id.et_alamatKTP);
        edtPrestasi1 = findViewById(R.id.et_prestasiSatu);
        edtPrestasi2 = findViewById(R.id.et_prestasiDua);
        edtPrestasi3 = findViewById(R.id.et_prestasiTiga);
        edtPrestasi4 = findViewById(R.id.et_prestasiEmpat);
        edtPrestasi5 = findViewById(R.id.et_prestasiLima);

        //TextView
        resultIPK = findViewById(R.id.tv_seekBarResult);

        //Untuk TV KRITERIA
        TVvalueJumlahAnak = findViewById(R.id.tv_valueforJumlahAnak);
        TVvaluePenghasilanOrtu = findViewById(R.id.tv_valueforPenghasilanOrtu);
        TVvalueUKT = findViewById(R.id.tv_valueforUKT);
        TVvalueIPK = findViewById(R.id.tv_valueforIPK);
        TVvalueSemester = findViewById(R.id.tv_valueforSemester);

        //Spinner
        spinSemester = findViewById(R.id.semester_spinner);

        spinKategori1 = findViewById(R.id.kategori1_spinner);
        spinTingkat1 = findViewById(R.id.tingkat1_spinner);

        spinKategori2 = findViewById(R.id.kategori2_spinner);
        spinTingkat2 = findViewById(R.id.tingkat2_spinner);

        spinKategori3 = findViewById(R.id.kategori3_spinner);
        spinTingkat3 = findViewById(R.id.tingkat3_spinner);

        spinKategori4 = findViewById(R.id.kategori4_spinner);
        spinTingkat4 = findViewById(R.id.tingkat4_spinner);

        spinKategori5 = findViewById(R.id.kategori5_spinner);
        spinTingkat5 = findViewById(R.id.tingkat5_spinner);

        spinFakultas = findViewById(R.id.fakultas_spinner);
        spinJurusan = findViewById(R.id.jurusan_spinner);

        //Button
        btnSaveProposal = findViewById(R.id.save_toServer);
    }

    public void UpdateUI_jumlahAnak(){
        getKriteria_jumlahAnak = edtJumSau.getText().toString();
        int jumlahAnak = Integer.parseInt(getKriteria_jumlahAnak)+1;
        try {
            if (jumlahAnak>5){
                setValueJumlahAnak = 1.0;
                TVvalueJumlahAnak.setText(String.valueOf(setValueJumlahAnak));
            } else if (jumlahAnak==5){
                setValueJumlahAnak = 0.8;
                TVvalueJumlahAnak.setText(String.valueOf(setValueJumlahAnak));
            } else if (jumlahAnak==4) {
                setValueJumlahAnak = 0.6;
                TVvalueJumlahAnak.setText(String.valueOf(setValueJumlahAnak));
            } else if (jumlahAnak==3) {
                setValueJumlahAnak = 0.4;
                TVvalueJumlahAnak.setText(String.valueOf(setValueJumlahAnak));
            } else if (jumlahAnak<=2) {
                setValueJumlahAnak = 0.2;
                TVvalueJumlahAnak.setText(String.valueOf(setValueJumlahAnak));
            }
        } catch (NumberFormatException ex){
            ex.printStackTrace();
        }
    }

    public void UpdateUI_penghasilanOrtu(){
        getKriteria_penghasilanOrtu = edtGajiOrtu.getText().toString();
        int penghasilanOrtu = Integer.parseInt(getKriteria_penghasilanOrtu);
        try {
            if (penghasilanOrtu < 1500000){
                setValuePenghasilanOrtu = 1.0;
                TVvaluePenghasilanOrtu.setText(String.valueOf(setValuePenghasilanOrtu));
            } else if (penghasilanOrtu >= 1500000 && penghasilanOrtu < 2000000){
                setValuePenghasilanOrtu = 0.8;
                TVvaluePenghasilanOrtu.setText(String.valueOf(setValuePenghasilanOrtu));
            } else if (penghasilanOrtu >= 2000000 && penghasilanOrtu < 2500000){
                setValuePenghasilanOrtu = 0.6;
                TVvaluePenghasilanOrtu.setText(String.valueOf(setValuePenghasilanOrtu));
            } else if (penghasilanOrtu >= 2500000 && penghasilanOrtu < 3000000){
                setValuePenghasilanOrtu = 0.4;
                TVvaluePenghasilanOrtu.setText(String.valueOf(setValuePenghasilanOrtu));
            } else if (penghasilanOrtu >= 3000000){
                setValuePenghasilanOrtu = 0.2;
                TVvaluePenghasilanOrtu.setText(String.valueOf(setValuePenghasilanOrtu));
            }
        } catch (NumberFormatException ex){
            ex.printStackTrace();
        }
    }

    public void UpdateUI_UKT(){
        getKriteria_UKT = edtUKT.getText().toString();
        int UKT = Integer.parseInt(getKriteria_UKT);
        try {
            if (UKT>=4000000){
                setValueUKT = 1.0;
                TVvalueUKT.setText(String.valueOf(setValueUKT));
            } else if (UKT>=3000000 && UKT<4000000){
                setValueUKT = 0.8;
                TVvalueUKT.setText(String.valueOf(setValueUKT));
            } else if (UKT>=1000000 && UKT<3000000) {
                setValueUKT = 0.6;
                TVvalueUKT.setText(String.valueOf(setValueUKT));
            } else if (UKT>=500000 && UKT<1000000) {
                setValueUKT = 0.4;
                TVvalueUKT.setText(String.valueOf(setValueUKT));
            } else if (UKT<500000) {
                setValueUKT = 0.2;
                TVvalueUKT.setText(String.valueOf(setValueUKT));
            }
        } catch (NumberFormatException ex){
            ex.printStackTrace();
        }
    }

    public void UpdateUI_IPK(){
        getKriteria_IPK = resultIPK.getText().toString();
        double IPK = Double.parseDouble(getKriteria_IPK);
        try {
            if (IPK>3.75){
                setValueIPK = 1.0;
                TVvalueIPK.setText(String.valueOf(setValueIPK));
            } else if (IPK>=3.5 && IPK<3.75){
                setValueIPK = 0.8;
                TVvalueIPK.setText(String.valueOf(setValueIPK));
            } else if (IPK>=3.25 && IPK<3.5) {
                setValueIPK = 0.6;
                TVvalueIPK.setText(String.valueOf(setValueIPK));
            } else if (IPK>=3.0 && IPK<3.25) {
                setValueIPK = 0.4;
                TVvalueIPK.setText(String.valueOf(setValueIPK));
            } else if (IPK<3.0) {
                setValueIPK = 0.2;
                TVvalueIPK.setText(String.valueOf(setValueIPK));
            }
        } catch (NumberFormatException ex){
            ex.printStackTrace();
        }
    }

    public static void hideKeyboard(Activity activity){
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
