package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class KelolaProposalmahasiswa extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_proposalmahasiswa);
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        overridePendingTransition(0,R.anim.slide_outta_right);
    }
}