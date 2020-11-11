package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class RiwayatDonasiMahasiswa extends AppCompatActivity {

    TextView btnBack,infoDonasi,NIMdonasiMahasiswa,idRegDonatur_riwayatDonasiMahasiswa;
    private RecyclerView recycleViewMahasiswa;
    private ProgressBar progressBarMahasiswa;

    private int pStatus = 0;
    private Handler handler = new Handler();
    ConnectivityManager conMgr;
    private long mLastClickTime = 0;

    private static final String JSON_URLriwayatDonasiMahasiswa = "https://prasyah.000webhostapp.com/getRiwayatDonasiMahasiswa.php?NIM=";
    private JsonArrayRequest requestRiwayatDonasiMahasiswa;
    private RequestQueue requestQueueRiwayatDonasiMahasiswa;
    private List<ModelRiwayatDonasiMahasiswa> modelRiwayatDonasiMahasiswa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_riwayat_donasi_mahasiswa);

        inital();

        Bundle riwayatDonasiMahasiswa = getIntent().getExtras();
        String revNIMdonasiMahasiswa = Objects.requireNonNull(riwayatDonasiMahasiswa).getString("NIM");
        NIMdonasiMahasiswa.setText(revNIMdonasiMahasiswa);

        //ProgressBar
        progressBarMahasiswa.setProgress(0);
        progressBarMahasiswa.setMax(100);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getOrangtuaAsuh();
            } else {
                Intent i = new Intent(RiwayatDonasiMahasiswa.this,NetworkErrorActivity.class);
                startActivity(i);
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        modelRiwayatDonasiMahasiswa = new ArrayList<>();

        btnBack.setOnClickListener(new View.OnClickListener() {
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

    private void getOrangtuaAsuh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (pStatus <= 100) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBarMahasiswa.setProgress(pStatus);
                            if(pStatus==4){
                                jsonReqOrangtuaAsuh();
                            }
                        }
                    });
                    try {
                        Thread.sleep(50);
                        if (pStatus==100){
                            progressBarMahasiswa.getMax();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pStatus++;
                }
            }
        }).start();
    }

    private void jsonReqOrangtuaAsuh() {
        requestRiwayatDonasiMahasiswa = new JsonArrayRequest(JSON_URLriwayatDonasiMahasiswa+NIMdonasiMahasiswa.getText().toString(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                for (int i = 0; i < response.length(); i++){
                    try {
                        jsonObject = response.getJSONObject(i);
                        ModelRiwayatDonasiMahasiswa modelRiwayatMahasiswa = new ModelRiwayatDonasiMahasiswa();

                        modelRiwayatMahasiswa.setNIM(jsonObject.getString("NIM"));
                        modelRiwayatMahasiswa.setNama_donatur(jsonObject.getString("nama_donatur"));
                        modelRiwayatMahasiswa.setStatus_donasi(jsonObject.getString("status_donasi"));
                        modelRiwayatMahasiswa.setId_reg_donatur(jsonObject.getString("id_reg_donatur"));

                        modelRiwayatDonasiMahasiswa.add(modelRiwayatMahasiswa);
                        infoDonasi.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setuprecyclerview(modelRiwayatDonasiMahasiswa);
                runAnimation(recycleViewMahasiswa,0);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueueRiwayatDonasiMahasiswa = Volley.newRequestQueue(RiwayatDonasiMahasiswa.this);
        requestQueueRiwayatDonasiMahasiswa.add(requestRiwayatDonasiMahasiswa);
    }

    private void runAnimation(RecyclerView recyclerView, int type) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;

        if (type == 0){
            controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_fall_down);
        }
        recyclerView.setLayoutAnimation(controller);
        setuprecyclerview(modelRiwayatDonasiMahasiswa);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void setuprecyclerview(List<ModelRiwayatDonasiMahasiswa> model) {
        AdapterRiwayatDonasiMahasiswa myAdapter = new AdapterRiwayatDonasiMahasiswa(this,model);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(myAdapter);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recycleViewMahasiswa.setLayoutManager(mLayoutManager);
        recycleViewMahasiswa.addItemDecoration(new RiwayatDonasiMahasiswa.GridSpacingItemDecoration(2, dpToPx(8), true));
        recycleViewMahasiswa.setItemAnimator(new DefaultItemAnimator());
        recycleViewMahasiswa.setAdapter(myAdapter);
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

    private void inital() {
        btnBack = findViewById(R.id.btnBacktoDashboardMahasiswa);
        recycleViewMahasiswa = findViewById(R.id.recyclerviewRiwayatDonasiMahasiswa);
        progressBarMahasiswa = findViewById(R.id.progressBarRiwayatDonasiMahasiswa);
        infoDonasi = findViewById(R.id.infoBelumDonasi);
        recycleViewMahasiswa = findViewById(R.id.recyclerviewRiwayatDonasiMahasiswa);
        NIMdonasiMahasiswa = findViewById(R.id.tvNIM_RiwayatDonasiMahasiswa);
        idRegDonatur_riwayatDonasiMahasiswa = findViewById(R.id.idRegdonatur_riwayatMahasiswa);
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        overridePendingTransition(0,R.anim.slide_outta_right);
    }
}