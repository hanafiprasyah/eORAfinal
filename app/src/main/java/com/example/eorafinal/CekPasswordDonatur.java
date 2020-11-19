package com.example.eorafinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.Executor;

public class CekPasswordDonatur extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private androidx.biometric.BiometricPrompt.PromptInfo promptInfo;

    private TextView regIdDonaturKelola,passwordUser,back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autentikasi_jari);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle idRegDonaturBundle = getIntent().getExtras();
        String revIDRegKelolaDonatur = Objects.requireNonNull(idRegDonaturBundle).getString("id_reg_donatur");
        regIdDonaturKelola = findViewById(R.id.registerIDDonatur);
        regIdDonaturKelola.setText(revIDRegKelolaDonatur);

        passwordUser = findViewById(R.id.passwordDonatur);
        back = findViewById(R.id.btnBack_cekpassword);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
                overridePendingTransition(0,R.anim.slide_outta_right);
            }
        });

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(CekPasswordDonatur.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Anda membatalkan autentikasi", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Harap berhati-hati dengan penyebaran password donatur ini!",
                        Toast.LENGTH_LONG)
                        .show();
                getPassword();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Verifikasi Sidik Jari")
                .setSubtitle("Buktikan bahwa anda memang admin E-ORA!")
                .setNegativeButtonText("Hentikan")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void getPassword(){
        String id = regIdDonaturKelola.getText().toString();

        if (id.equals("")) {
            FancyToast.makeText(getApplicationContext(),"Server bermasalah", Toast.LENGTH_SHORT, FancyToast.WARNING, R.drawable.ic_errorwhite24, false).show();
            return;
        }

        final String urlGetPassword                = ConfigProfileDonatur.URL_GETPASSWORD+id;

        StringRequest loadNaker = new StringRequest(urlGetPassword, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showPasswordUserDonatur(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CekPasswordDonatur.this,"Server Bermasalah",Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loadNaker);
    }

    private void showPasswordUserDonatur(String response) {
        String pw="";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(ConfigProfileDonatur.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);

            pw = collegeData.getString(ConfigProfileDonatur.KEY_PASSWORD);

        } catch (JSONException e){
            e.printStackTrace();
        }
        passwordUser.setText(""+pw);
        YoYo.with(Techniques.RubberBand).duration(1500).playOn(passwordUser);
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        overridePendingTransition(0,R.anim.slide_outta_right);
    }
}