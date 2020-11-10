package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RiwayatDonasi extends AppCompatActivity {

    private static final String JSON_URLriwayatDonasi = "https://prasyah.000webhostapp.com/getRiwayatDonasi.php?id_reg_donatur=";
    private JsonArrayRequest requestRiwayatDonasi;
    private RequestQueue requestQueueRiwayatDonasi;
    private RecyclerView recyclerView;
    private List<ModelRiwayatDonasi> modelRiwayatDonasi;

//    LottieAnimationView loadingLottie;
//    ObjectAnimator lottieObject;

    //back
    TextView backToDonaturHome,IDriwayatDonasiUmum;

    //ProgressBar
    private ProgressBar progressBar;
    private int pStatus = 0;
    private Handler handler = new Handler();

    TextView infoBelumdonasiTV;

    ConnectivityManager conMgr;

    //prevent double click
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_riwayat_donasi);

        Bundle riwayatDonasiUmum = getIntent().getExtras();
        String revIDRegforPilihRiwayatUmum = Objects.requireNonNull(riwayatDonasiUmum).getString("id_reg_donatur");
        IDriwayatDonasiUmum = findViewById(R.id.tv_IdRiwayatDonasiUmum);
        IDriwayatDonasiUmum.setText(revIDRegforPilihRiwayatUmum);

        infoBelumdonasiTV = findViewById(R.id.infoBelumDonasi);

        //ProgressBar
        progressBar = findViewById(R.id.progressBarRiwayatDonasi);
        progressBar.setProgress(0);
        progressBar.setMax(100);

//        loadingLottie = findViewById(R.id.lottieLoading);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getRiwayatDonasi();
            } else {
                Intent i = new Intent(RiwayatDonasi.this,NetworkErrorActivity.class);
                startActivity(i);
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        modelRiwayatDonasi = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerviewRiwayatDonasi);

        backToDonaturHome = findViewById(R.id.btnBacktoPilihRiwayatDonasi);
        backToDonaturHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                finishAfterTransition();
                overridePendingTransition(0,R.anim.slide_outta_right);
            }
        });
    }

    private void getRiwayatDonasi() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (pStatus <= 100) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(pStatus);
                            if(pStatus==2){
                                jsonReqforRiwayatDonasi();
                            }
                        }
                    });
                    try {
                        Thread.sleep(50);
                        if (pStatus==100){
                            progressBar.getMax();
//                            lottieObject.pause();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pStatus++;
                }
            }
        }).start();
    }

    private void jsonReqforRiwayatDonasi() {
        requestRiwayatDonasi = new JsonArrayRequest(JSON_URLriwayatDonasi+IDriwayatDonasiUmum.getText().toString(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                for (int i = 0; i < response.length(); i++){
                    try {
                        jsonObject = response.getJSONObject(i);
                            ModelRiwayatDonasi modelRiwayat = new ModelRiwayatDonasi();
                            modelRiwayat.setNama_donatur(jsonObject.getString("nama_donatur"));
                            modelRiwayat.setJumlah_donasi(jsonObject.getString("jumlah_donasi"));
                            modelRiwayat.setJenis_donasi(jsonObject.getString("jenis_donasi"));
                            modelRiwayatDonasi.add(modelRiwayat);
                            infoBelumdonasiTV.setVisibility(View.GONE);
//                            lottieObject = ObjectAnimator.ofFloat(loadingLottie,"alpha",0f);
//                            lottieObject.start();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setuprecyclerview(modelRiwayatDonasi);
                runAnimation(recyclerView,0);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueueRiwayatDonasi = Volley.newRequestQueue(RiwayatDonasi.this);
        requestQueueRiwayatDonasi.add(requestRiwayatDonasi);
    }

    private void runAnimation(RecyclerView recyclerView, int type) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;

        if (type == 0){
            controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_fall_down);
        }
        recyclerView.setLayoutAnimation(controller);
        setuprecyclerview(modelRiwayatDonasi);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void setuprecyclerview(List<ModelRiwayatDonasi> model) {
        AdapterRiwayatDonasi myAdapter = new AdapterRiwayatDonasi(this,model);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(myAdapter);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new RiwayatDonasi.GridSpacingItemDecoration(2, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myAdapter);
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        overridePendingTransition(0,R.anim.slide_outta_right);
    }
}