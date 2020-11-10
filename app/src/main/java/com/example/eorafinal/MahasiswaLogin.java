package com.example.eorafinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.textfield.TextInputLayout;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MahasiswaLogin extends AppCompatActivity {

    TextView register,back;

    ProgressDialog progressDialog;
    Button btnLogin;

    EditText etNim, etPass;

    //prevent double click
    private long mLastClickTime = 0;

    int success;
    ConnectivityManager conMgr;

    private String url = Server.URL + "login.php";
    private static final String TAG = MahasiswaLogin.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public final static String TAG_NIM = "NIM";
    public final static String TAG_NAMA = "nama";

    String tag_json_obj = "json_obj_req";

    SharedPreferences sharedPreferences;
    Boolean session = false;
    String nama,NIM;

    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_mahasiswa_login);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Intent i = new Intent(MahasiswaLogin.this,NetworkErrorActivity.class);
                startActivity(i);
            }
        }

        // Cek session login jika TRUE maka langsung buka HomeMahasiswa
        sharedPreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedPreferences.getBoolean(session_status, false);
        NIM = sharedPreferences.getString(TAG_NIM, null);
        nama = sharedPreferences.getString(TAG_NAMA, null);

        if (session) {
            //Go to Mahasiswa Home
            Intent intent = new Intent(MahasiswaLogin.this, MahasiswaHome.class);
            intent.putExtra(TAG_NIM,NIM);
            intent.putExtra(TAG_NAMA,nama);
            finish();
            startActivity(intent);
        }

        etNim = findViewById(R.id.etNIM);
        etNim.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etPass = findViewById(R.id.etPassword);
        TextView alertlogin = findViewById(R.id.TValert);

        btnLogin = findViewById(R.id.signInMHS);
        btnLogin.setOnClickListener(v -> {
            //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            // TODO Auto-generated method stub
            final String NIM      = etNim.getText().toString();
            final String pass     = etPass.getText().toString();

            //Tutup keyboard saat tombol ditekan
            hideKeyboard(MahasiswaLogin.this);

            // mengecek kolom yang kosong
            if (NIM.trim().length() > 0 && pass.trim().length() > 0) {
                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {
                    checkLogin(NIM,pass);
                    alertlogin.setVisibility(View.GONE);
                } else {
                    Intent i = new Intent(MahasiswaLogin.this,NetworkErrorActivity.class);
                    startActivity(i);
                    //Toast.makeText(getApplicationContext() ,"Tidak ada koneksi Internet :(", Toast.LENGTH_LONG).show();
                }
            } else {
                // Prompt user to enter credentials
                Animation animAlertforMahasiswaLogin = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.item_animation_falldown);
                alertlogin.setAnimation(animAlertforMahasiswaLogin);
                alertlogin.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.Bounce)
                        .duration(500)
                        .repeat(1)
                        .playOn(etNim);
            }

        });

        register = findViewById(R.id.registerMHS);
        register.setOnClickListener(v -> {
            //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent (MahasiswaLogin.this,RegisterMahasiswa.class);
            startActivity(i);
            finishAfterTransition();
            overridePendingTransition(R.anim.slide_in,R.anim.stay);
        });

        back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> {
            //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            finishAfterTransition();
            overridePendingTransition(0,R.anim.slide_out);
        });
    }

    private void checkLogin(final String nim, final String pass) {
        //normalButton
        progressDialog = new ProgressDialog(this,R.style.ProgressBarMahasiswa);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang verifikasi data ..");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        String NIM = jObj.getString(TAG_NIM);
                        String nama = jObj.getString(TAG_NAMA);

                        Log.e("Login Berhasil", jObj.toString());

                        FancyToast.makeText(getApplicationContext(),jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT, FancyToast.SUCCESS, R.drawable.ic_done, false).show();
                        //Toast.makeText(getApplicationContext(), ).show();

                        // menyimpan login ke session
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_NIM, NIM);
                        editor.putString(TAG_NAMA, nama);
                        editor.apply();

                        // Memanggil main activity Home Mahasiswa
                        Intent intent = new Intent(MahasiswaLogin.this, MahasiswaHome.class);
                        intent.putExtra(TAG_NIM, NIM);
                        intent.putExtra(TAG_NAMA, nama);
                        startActivity(intent);
                        finish();
                    } else {
                        etPass.setText("");
                        FancyToast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE),FancyToast.LENGTH_SHORT, FancyToast.ERROR, R.drawable.ic_errorwhite24, false).show();
                        YoYo.with(Techniques.Shake)
                                .duration(500)
                                .playOn(etPass);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    FancyToast.makeText(getApplicationContext(), "Jaringan Anda Bermasalah",FancyToast.LENGTH_SHORT, FancyToast.ERROR, R.drawable.ic_errorwhite24, false).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                FancyToast.makeText(getApplicationContext(),"Server dalam pemeliharaan :(",FancyToast.LENGTH_SHORT, FancyToast.ERROR, R.drawable.ic_errorwhite24, false).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("NIM", nim);
                params.put("password", pass);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    private void showDialog() {
        if(!progressDialog.isShowing())
            progressDialog.show();
    }

    public void gantiPassword(View view){
        //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        Intent linkGantiPassword = new Intent(Intent.ACTION_VIEW, Uri.parse("https://prasyah.000webhostapp.com/GantiPassword/gantiPasswordHome.php"));
        startActivity(linkGantiPassword);
    }

    public static void hideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null){
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public void hideKeyboardOnLayout(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        view = this.getCurrentFocus();
        if (view == null){
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out);
    }
}
