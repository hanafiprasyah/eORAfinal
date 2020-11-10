package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
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
import com.github.clans.fab.FloatingActionButton;
import com.tuann.floatingactionbuttonexpandable.FloatingActionButtonExpandable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class EditProfilMhs extends AppCompatActivity {

    Toolbar toolbar;
    TextView back;

    ConnectivityManager conMgr;

    //prevent double click
    private long mLastClickTime = 0;

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListName;
    HashMap<String,Contact> listContacts;
    private int expandposition = -1;

    String NIMmhs,namaMhs,emailMhs,notelMhs;

    TextView saveEdit;
    EditText etNama,etEmail,etNotel;
    TextView tvNIM;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_editprofil_mhs);

        Bundle b = getIntent().getExtras();
        String revNIM = Objects.requireNonNull(b).getString("NIM");
        tvNIM = findViewById(R.id.et_nim);
        tvNIM.setText(revNIM);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getData();
            } else {
                Intent i = new Intent(EditProfilMhs.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        //forexpandable
        init();

        //loading = new ProgressDialog(this);
        saveEdit = findViewById(R.id.saveEdit);
        saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                loading = new ProgressDialog(EditProfilMhs.this,R.style.ProgressBarMahasiswa);
                loading.setMessage("Mengganti Data...");
                loading.setCancelable(false);
                loading.show();
                hideKeyboardAfter(EditProfilMhs.this);

                NIMmhs = tvNIM.getText().toString();
                namaMhs = etNama.getText().toString();
                emailMhs = etEmail.getText().toString();
                notelMhs = etNotel.getText().toString();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        validasiData();
                    }
                },1000);
            }
        });

        etNama = findViewById(R.id.et_nama);
        etEmail = findViewById(R.id.et_email);
        etNotel = findViewById(R.id.et_notel);
        etNotel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboard(v);
                }
            }
        });



        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (expandposition != -1 && groupPosition != expandposition){
                    expandableListView.collapseGroup(expandposition);
                }
                expandposition = groupPosition;
            }
        });

        toolbar = findViewById(R.id.toolbarEditProfilMhs);
        TextView toolbarText = findViewById(R.id.toolbar_textMhs);
        if (toolbarText != null && toolbar != null){
            setSupportActionBar(toolbar);
        }

        back = findViewById(R.id.btnBacktoMhs);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                finishAfterTransition();
                overridePendingTransition(0,R.anim.slide_outta_right);
            }
        });
    }

    private void validasiData() {
        if(NIMmhs.equals("") || namaMhs.equals("") || emailMhs.equals("") || notelMhs.equals("")){
            loading.dismiss();
            Toast.makeText(EditProfilMhs.this, "Periksa kembali data yang anda masukkan!", Toast.LENGTH_SHORT).show();
        }else {
            updateData();
        }
    }

    private void updateData() {
        AndroidNetworking.post("https://prasyah.000webhostapp.com/editProfilMhs.php")
                .addBodyParameter("NIM",""+NIMmhs)
                .addBodyParameter("nama",""+namaMhs)
                .addBodyParameter("email",""+emailMhs)
                .addBodyParameter("notel",""+notelMhs)
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
                                new AlertDialog.Builder(EditProfilMhs.this)
                                        .setMessage("Silakan geser ke atas untuk refresh!")
                                        .setTitle("Berhasil")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = getIntent();
                                                setResult(RESULT_OK,i);
                                                EditProfilMhs.this.finishAndRemoveTask();
                                            }
                                        })
                                        .show();
                            }else{
                                new AlertDialog.Builder(EditProfilMhs.this)
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

    private void init() {
        this.expandableListView = findViewById(R.id.expandableListView);
        this.listContacts = getContact();
        this.expandableListName = new ArrayList<>(listContacts.keySet());
        this.expandableListAdapter = new CustomExpandableListAdapter(this,expandableListName,listContacts);
    }

    private HashMap<String, Contact> getContact(){
        HashMap<String,Contact> list = new HashMap<>();
        list.put("Admin EORA", new Contact("KLIK DISINI! Hubungi admin untuk merubah NIM, Tempat Lahir dan Tanggal Lahir.",R.drawable.fotohanafi));
        return list;
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

    private void showJSON(String response) {
        String nama="";
        String email="";
        String notel="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileMahasiswa.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            nama = collegeData.getString(ConfigProfileMahasiswa.KEY_NAMA);
            email = collegeData.getString(ConfigProfileMahasiswa.KEY_EMAIL);
            notel = collegeData.getString(ConfigProfileMahasiswa.KEY_NOTEL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        etNama.setText(""+nama);
        etEmail.setText(""+email);
        etNotel.setText(""+notel);
    }

    private void getData() {
        String NIM = tvNIM.getText().toString();

        if (NIM.equals("")) {
            Toast.makeText(this, "Server bermasalah", Toast.LENGTH_SHORT).show();
            return;
        }

        loading = new ProgressDialog(this,R.style.ProgressBarMahasiswa);
        loading.setCancelable(false);
        loading.setMessage("Sedang mengambil data..");
        loading.setTitle("Mohon Tunggu");
        loading.show();
        //loading = ProgressDialog.show(this,"Mohon Tunggu","Sedang mengambil data..",false,false);

        final String url = ConfigProfileMahasiswa.DATA_URL+tvNIM.getText().toString();

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
                        Toast.makeText(EditProfilMhs.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        overridePendingTransition(0,R.anim.slide_outta_right);
    }
}