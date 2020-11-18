package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

public class Karturencanastudi extends AppCompatActivity implements DownloadFile.Listener{

    TextView urlKRS;
    RemotePDFViewPager remotePDFViewPagerKRS;
    PDFPagerAdapter adapterKRS;
    ProgressBar progressBarKRS;
    ConnectivityManager conMgr;
    RelativeLayout layoutKRSNotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_karturencanastudi);

        Bundle urlKRSToKRSactivity = getIntent().getExtras();
        String revURL = Objects.requireNonNull(urlKRSToKRSactivity).getString("url_krs");
        urlKRS = findViewById(R.id.tvURLKRS_transfer);
        urlKRS.setText(revURL);

        progressBarKRS = findViewById(R.id.pBarKRS);
        layoutKRSNotFound = findViewById(R.id.layoutKRS_NotFound);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {

                String urlKarturencanastudi = urlKRS.getText().toString();
                progressBarKRS.setMax(100);
                remotePDFViewPagerKRS = new RemotePDFViewPager(Karturencanastudi.this, ""+urlKarturencanastudi,this);

            } else {
                Intent i = new Intent(Karturencanastudi.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        adapterKRS = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPagerKRS.setAdapter(adapterKRS);
        LinearLayout container = (LinearLayout) findViewById(R.id.containerKRS);
        container.addView(remotePDFViewPagerKRS, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        YoYo.with(Techniques.FadeIn).playOn(container);
        progressBarKRS.getMax();
        progressBarKRS.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(Exception e) {
        progressBarKRS.getMax();
        progressBarKRS.setVisibility(View.GONE);
        layoutKRSNotFound.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapterKRS!=null){
            adapterKRS.close();
        }
    }
}