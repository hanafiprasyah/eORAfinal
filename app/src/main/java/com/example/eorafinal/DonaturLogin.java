package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DonaturLogin extends AppCompatActivity {

    TextView back,register;

    ConnectivityManager conMgr;

    Button btnLoginDonatur;
    EditText etUsername,etPassDonatur;
    ProgressDialog progressDialog;

    int success;

    String Username,RegisterID;

    public final static String TAG_USERNAME = "username_login";
    public final static String TAG_IDREG    = "id_reg_donatur";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private String url = Server.URLDonatur + "login.php";
    private static final String TAG = DonaturLogin.class.getSimpleName();

    SharedPreferences sharedPreferencesDonatur;
    public static final String donatur_shared_preferences = "donatur_shared_preferences";
    public static final String session_status = "session_status";
    Boolean session = false;

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_donatur_login);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Intent i = new Intent(DonaturLogin.this,NetworkErrorActivity.class);
                startActivity(i);
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        sharedPreferencesDonatur = getSharedPreferences(donatur_shared_preferences, Context.MODE_PRIVATE);
        session = sharedPreferencesDonatur.getBoolean(session_status, false);
        //***************************************************
        Username = sharedPreferencesDonatur.getString(TAG_USERNAME, null);
        RegisterID = sharedPreferencesDonatur.getString(TAG_IDREG,null);

        if (session) {
            //Goto Donatur Home
            Intent intent = new Intent(DonaturLogin.this, DonaturHome.class);
            intent.putExtra(TAG_USERNAME,Username);
            intent.putExtra(TAG_IDREG,RegisterID);
            finish();
            startActivity(intent);
        }

        TextView alertlogin = (TextView)findViewById(R.id.TValert);
        etUsername = findViewById(R.id.etUsernameDonatur);
        etPassDonatur = findViewById(R.id.etPasswordDonatur);

        btnLoginDonatur = findViewById(R.id.signInDonatur);
        btnLoginDonatur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final String username      = etUsername.getText().toString();
                final String passDonatur   = etPassDonatur.getText().toString();

                //Tutup keyboard saat tombol ditekan
                hideKeyboard(DonaturLogin.this);

                // mengecek kolom yang kosong
                if (username.trim().length() > 0 && passDonatur.trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        checkLogin(username,passDonatur);
                        alertlogin.setVisibility(View.GONE);
                    } else {
                        Intent i = new Intent(DonaturLogin.this,NetworkErrorActivity.class);
                        startActivity(i);
                        //Toast.makeText(getApplicationContext() ,"Tidak ada koneksi Internet :(", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Animation animAlertforDonaturLogin = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.item_animation_falldown);
                    alertlogin.setAnimation(animAlertforDonaturLogin);
                    alertlogin.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.Bounce)
                            .duration(500)
                            .repeat(1)
                            .playOn(etUsername);
                }
            }
        });

        register = findViewById(R.id.registerDonatur);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (DonaturLogin.this,RegisterDonatur.class);
                startActivity(i);
                finishAfterTransition();
                overridePendingTransition(R.anim.slide_in,R.anim.stay);
            }
        });

        back = findViewById(R.id.btnBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
                overridePendingTransition(0,R.anim.slide_out);
            }
        });
    }

    private void checkLogin(final String username, final String password) {
        //normalButton
        progressDialog = new ProgressDialog(this,R.style.ProgressBarDonatur);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang verifikasi data ..");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        String username = jObj.getString(TAG_USERNAME);
                        String id       = jObj.getString(TAG_IDREG);

                        Log.e("Login Berhasil", jObj.toString());

                        FancyToast.makeText(getApplicationContext(),jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT, FancyToast.SUCCESS, R.drawable.ic_done, false).show();
                        //Toast.makeText(getApplicationContext(), ).show();

                        // menyimpan login ke session
                        SharedPreferences.Editor editor = sharedPreferencesDonatur.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_USERNAME, username);
                        editor.putString(TAG_IDREG, id);
                        editor.apply();

                        // Memanggil main activity Home Mahasiswa
                        Intent intent = new Intent(DonaturLogin.this, DonaturHome.class);
                        intent.putExtra(TAG_USERNAME, username);
                        intent.putExtra(TAG_IDREG, id);
                        startActivity(intent);
                        finish();
                    } else {
                        etPassDonatur.setText("");
                        FancyToast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE),FancyToast.LENGTH_SHORT, FancyToast.ERROR, R.drawable.ic_errorwhite24, false).show();
                        YoYo.with(Techniques.Shake)
                                .duration(500)
                                .playOn(etPassDonatur);
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("username_login", username);
                params.put("password", password);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if(!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
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
