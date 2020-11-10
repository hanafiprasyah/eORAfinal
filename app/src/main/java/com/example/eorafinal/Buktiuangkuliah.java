package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Objects;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

public class Buktiuangkuliah extends AppCompatActivity implements DownloadFile.Listener{

    TextView urlUKT;
    RemotePDFViewPager remotePDFViewPagerUKT;
    PDFPagerAdapter adapterUKT;
    ProgressBar progressBarUKT;
    ConnectivityManager conMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_buktiuangkuliah);

        Bundle urlUKTToUKTactivity = getIntent().getExtras();
        String revURL = Objects.requireNonNull(urlUKTToUKTactivity).getString("url_ukt");
        urlUKT = findViewById(R.id.tvURLUKT_transfer);
        urlUKT.setText(revURL);

        progressBarUKT = findViewById(R.id.pBarUKT);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {

                String urlUangKuliahTunggal = urlUKT.getText().toString();
                progressBarUKT.setMax(100);
                remotePDFViewPagerUKT = new RemotePDFViewPager(Buktiuangkuliah.this, ""+urlUangKuliahTunggal,this);

            } else {
                Intent i = new Intent(Buktiuangkuliah.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        adapterUKT = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPagerUKT.setAdapter(adapterUKT);
        LinearLayout container = (LinearLayout) findViewById(R.id.containerUKT);
        container.addView(remotePDFViewPagerUKT, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        YoYo.with(Techniques.FadeIn).playOn(container);
        progressBarUKT.getMax();
        progressBarUKT.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(Exception e) {
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapterUKT!=null){
            adapterUKT.close();
        }
    }
}