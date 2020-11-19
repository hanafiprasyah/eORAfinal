package com.example.eorafinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.Executor;

public class HapusDonatur extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private androidx.biometric.BiometricPrompt.PromptInfo promptInfo;

    private TextView back,regIDdonatur;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hapus_donatur);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle idRegDonaturBundle = getIntent().getExtras();
        String revIDRegKelolaDonatur = Objects.requireNonNull(idRegDonaturBundle).getString("id_reg_donatur");
        regIDdonatur = findViewById(R.id.registerIDDonatur_hapusDonatur);
        regIDdonatur.setText(revIDRegKelolaDonatur);

        back = findViewById(R.id.btnBack_hapusDonatur);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
                overridePendingTransition(0,R.anim.slide_outta_right);
            }
        });

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(HapusDonatur.this,
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
                new android.app.AlertDialog.Builder(HapusDonatur.this)
                        .setMessage("Anda yakin ingin menghapus data berikut?")
                        .setCancelable(false)
                        .setPositiveButton("Y A K I N", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                hapusUserDonatur();
                            }
                        })
                        .setNegativeButton("CEK ULANG", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAfterTransition();
                                overridePendingTransition(0,R.anim.slide_outta_right);
                            }
                        })
                        .show();
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

    private void hapusUserDonatur() {
        loading = new ProgressDialog(this,R.style.ProgressBarDonatur);
        loading.setCancelable(false);
        loading.setMessage("Menghapus data ..");
        loading.show();

        String id = regIDdonatur.getText().toString();

        AndroidNetworking.post("https://prasyah.000webhostapp.com/admin/hapusDonatur.php")
                .addBodyParameter("id_reg_donatur",""+id)
                .setTag("Hapus Donatur")
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
                                new android.app.AlertDialog.Builder(HapusDonatur.this)
                                        .setMessage("Berhasil Hapus Akses Donatur")
                                        .setCancelable(false)
                                        .setPositiveButton("MENU UTAMA", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent goHomeAdmin = new Intent(HapusDonatur.this,AdminHome.class);
                                                startActivity(goHomeAdmin);
                                                finishAfterTransition();
                                                overridePendingTransition(0,R.anim.slide_outta_right);
                                            }
                                        })
                                        .show();
                            }else{
                                new android.app.AlertDialog.Builder(HapusDonatur.this)
                                        .setMessage("Gagal Hapus Data")
                                        .setCancelable(false)
                                        .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                loading.dismiss();
                                                finishAfterTransition();
                                                overridePendingTransition(0,R.anim.slide_outta_right);
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
}