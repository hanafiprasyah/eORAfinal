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

public class RiwayatDonasiKhusus extends AppCompatActivity {

    TextView IDriwayatDonasiKhusus, infoBelumdonasiTV,backToDonaturHome_khusus;
    private ProgressBar progressBar;
    private int pStatus = 0;
    private Handler handler = new Handler();
    ConnectivityManager conMgr;
    private long mLastClickTime = 0;

    private static final String JSON_URLriwayatDonasiKhusus = "https://prasyah.000webhostapp.com/getRiwayatDonasiKhusus.php?id_reg_donatur=";
    private JsonArrayRequest requestRiwayatDonasiKhusus;
    private RequestQueue requestQueueRiwayatDonasiKhusus;
    private RecyclerView recyclerViewKhusus;
    private List<ModelRiwayatDonasiKhusus> modelRiwayatDonasiKhusus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_riwayat_donasi_khusus);

        Bundle riwayatDonasiKhusus = getIntent().getExtras();
        String revIDRegforPilihRiwayatKhusus = Objects.requireNonNull(riwayatDonasiKhusus).getString("id_reg_donatur");
        IDriwayatDonasiKhusus = findViewById(R.id.tv_IdRiwayatDonasiKhusus);
        IDriwayatDonasiKhusus.setText(revIDRegforPilihRiwayatKhusus);

        infoBelumdonasiTV = findViewById(R.id.infoBelumDonasiKhusus);

        //ProgressBar
        progressBar = findViewById(R.id.progressBarRiwayatDonasiKhusus);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getRiwayatDonasiKhusus();
            } else {
                Intent i = new Intent(RiwayatDonasiKhusus.this,NetworkErrorActivity.class);
                startActivity(i);
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        modelRiwayatDonasiKhusus = new ArrayList<>();
        recyclerViewKhusus = findViewById(R.id.recyclerviewRiwayatDonasiKhusus);

        backToDonaturHome_khusus = findViewById(R.id.btnBacktoPilihRiwayatDonasi_khusus);
        backToDonaturHome_khusus.setOnClickListener(new View.OnClickListener() {
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

    private void getRiwayatDonasiKhusus(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (pStatus <= 100) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(pStatus);
                            if(pStatus==2){
                                jsonReqforRiwayatDonasiKhusus();
                            }
                        }
                    });
                    try {
                        Thread.sleep(50);
                        if (pStatus==100){
                            progressBar.getMax();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pStatus++;
                }
            }
        }).start();
    }

    private void jsonReqforRiwayatDonasiKhusus(){
        requestRiwayatDonasiKhusus = new JsonArrayRequest(JSON_URLriwayatDonasiKhusus+IDriwayatDonasiKhusus.getText().toString(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                for (int i = 0; i < response.length(); i++){
                    try {
                        jsonObject = response.getJSONObject(i);
                        ModelRiwayatDonasiKhusus modelRiwayatKhusus = new ModelRiwayatDonasiKhusus();

                        modelRiwayatKhusus.setNama_donatur(jsonObject.getString("nama_donatur"));
                        modelRiwayatKhusus.setNIM(jsonObject.getString("NIM"));
                        modelRiwayatKhusus.setNama(jsonObject.getString("nama"));
                        modelRiwayatKhusus.setNotel(jsonObject.getString("notel"));

                        modelRiwayatDonasiKhusus.add(modelRiwayatKhusus);
                        infoBelumdonasiTV.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setuprecyclerview(modelRiwayatDonasiKhusus);
                runAnimation(recyclerViewKhusus,0);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueueRiwayatDonasiKhusus = Volley.newRequestQueue(RiwayatDonasiKhusus.this);
        requestQueueRiwayatDonasiKhusus.add(requestRiwayatDonasiKhusus);
    }

    private void runAnimation(RecyclerView recyclerView, int type) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;

        if (type == 0){
            controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_fall_down);
        }
        recyclerView.setLayoutAnimation(controller);
        setuprecyclerview(modelRiwayatDonasiKhusus);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void setuprecyclerview(List<ModelRiwayatDonasiKhusus> model) {
        AdapterRiwayatDonasiKhusus myAdapter = new AdapterRiwayatDonasiKhusus(this,model);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(myAdapter);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewKhusus.setLayoutManager(mLayoutManager);
        recyclerViewKhusus.addItemDecoration(new RiwayatDonasiKhusus.GridSpacingItemDecoration(2, dpToPx(8), true));
        recyclerViewKhusus.setItemAnimator(new DefaultItemAnimator());
        recyclerViewKhusus.setAdapter(myAdapter);
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
}