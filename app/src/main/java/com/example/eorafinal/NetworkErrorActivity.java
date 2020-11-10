package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class NetworkErrorActivity extends AppCompatActivity {

    ConnectivityManager conMgr;
    ConnectivityStatusReceiver connectivityStatusReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_networ_errork);

        connectivityStatusReceiver = new ConnectivityStatusReceiver();

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {

                Intent i = new Intent(this,getApplicationContext().getClass());
                startActivity(i);
                //Toast.makeText(getApplicationContext(), "Anda terhubung!",Toast.LENGTH_SHORT).show();
            } else {
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityStatusReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectivityStatusReceiver != null){
            unregisterReceiver(connectivityStatusReceiver);
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Hubungkan Koneksi Jaringan Anda!", Toast.LENGTH_SHORT).show();
    }
}
