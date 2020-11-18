package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Objects;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

public class Lembarhasilstudi extends AppCompatActivity implements DownloadFile.Listener {

    TextView urlLHS,tvGeserLayar;
    RemotePDFViewPager remotePDFViewPagerLHS;
    PDFPagerAdapter adapterLHS;
    ProgressBar progressBarLHS;
    ConnectivityManager conMgr;
    RelativeLayout layoutLHSNotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_lembarhasilstudi);

        Bundle urlLHSToLHSactivity = getIntent().getExtras();
        String revURL = Objects.requireNonNull(urlLHSToLHSactivity).getString("url_lhs");
        urlLHS = findViewById(R.id.tvURLLHS_transfer);
        urlLHS.setText(revURL);

        tvGeserLayar = findViewById(R.id.tv_slideformoreLHS);
        progressBarLHS = findViewById(R.id.pBarLHS);
        layoutLHSNotFound = findViewById(R.id.layoutLHS_NotFound);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {

                String urlLembarHasilStudi = urlLHS.getText().toString();
                progressBarLHS.setMax(100);
                remotePDFViewPagerLHS = new RemotePDFViewPager(Lembarhasilstudi.this, ""+urlLembarHasilStudi,this);

            } else {
                Intent i = new Intent(Lembarhasilstudi.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        adapterLHS = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPagerLHS.setAdapter(adapterLHS);
        LinearLayout container = (LinearLayout) findViewById(R.id.containerLHS);
        container.addView(remotePDFViewPagerLHS, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        YoYo.with(Techniques.FadeIn).playOn(container);
        progressBarLHS.getMax();
        progressBarLHS.setVisibility(View.GONE);
        tvGeserLayar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(Exception e) {
        progressBarLHS.getMax();
        progressBarLHS.setVisibility(View.GONE);
        tvGeserLayar.setVisibility(View.GONE);
        layoutLHSNotFound.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapterLHS!=null){
            adapterLHS.close();
        }
    }
}