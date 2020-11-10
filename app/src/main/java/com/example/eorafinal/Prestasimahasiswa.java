package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Objects;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

public class Prestasimahasiswa extends AppCompatActivity implements DownloadFile.Listener{

    TextView urlPrestasi,tvGeserPrestasi,tvNotFound;
    RemotePDFViewPager remotePDFViewPagerPrestasi;
    PDFPagerAdapter adapterPrestasi;
    ProgressBar progressBarPrestasi;
    ConnectivityManager conMgr;
    RelativeLayout layoutNotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_prestasimahasiswa);

        Bundle urlPrestasiToPrestasiactivity = getIntent().getExtras();
        String revURLPrestasi = Objects.requireNonNull(urlPrestasiToPrestasiactivity).getString("url_fileprestasi");
        urlPrestasi = findViewById(R.id.tvURLPrestasi_transfer);
        urlPrestasi.setText(revURLPrestasi);

        tvGeserPrestasi = findViewById(R.id.tv_slideformorePrestasi);
        progressBarPrestasi = findViewById(R.id.pBarPrestasi);

        layoutNotFound = findViewById(R.id.layout_NotFound);
        tvNotFound = findViewById(R.id.tv_notFound);
        Typeface customText = Typeface.createFromAsset(getAssets(),"font/intimacy.ttf");
        tvNotFound.setTypeface(customText);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {

                String urlFilePrestasi = urlPrestasi.getText().toString();
                progressBarPrestasi.setMax(100);
                remotePDFViewPagerPrestasi = new RemotePDFViewPager(Prestasimahasiswa.this, ""+urlFilePrestasi,this);

            } else {
                Intent i = new Intent(Prestasimahasiswa.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        adapterPrestasi = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPagerPrestasi.setAdapter(adapterPrestasi);
        LinearLayout container = (LinearLayout) findViewById(R.id.containerPrestasi);
        container.addView(remotePDFViewPagerPrestasi, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        YoYo.with(Techniques.FadeIn).playOn(container);
        progressBarPrestasi.getMax();
        progressBarPrestasi.setVisibility(View.GONE);
        tvGeserPrestasi.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(Exception e) {
        progressBarPrestasi.getMax();
        progressBarPrestasi.setVisibility(View.GONE);
        tvGeserPrestasi.setVisibility(View.GONE);
        layoutNotFound.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapterPrestasi!=null){
            adapterPrestasi.close();
        }
    }
}