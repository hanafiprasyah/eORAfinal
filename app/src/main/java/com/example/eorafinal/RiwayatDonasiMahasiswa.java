package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RiwayatDonasiMahasiswa extends AppCompatActivity {

    TextView btnBack,infoDonasi;
    RecyclerView recycleViewMahasiswa;
    ProgressBar progressBarMahasiswa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_donasi_mahasiswa);

        inital();
    }

    private void inital() {
        btnBack = findViewById(R.id.btnBacktoDashboardMahasiswa);
        recycleViewMahasiswa = findViewById(R.id.recyclerviewRiwayatDonasiMahasiswa);
        progressBarMahasiswa = findViewById(R.id.progressBarRiwayatDonasiMahasiswa);
        infoDonasi = findViewById(R.id.infoBelumDonasi);
    }
}