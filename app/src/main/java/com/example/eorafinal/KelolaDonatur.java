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
import android.widget.Toast;

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

public class KelolaDonatur extends AppCompatActivity {

    private ProgressBar progressBar;
    private int pStatus = 0;
    private Handler handler = new Handler();
    ConnectivityManager conMgr;
    private long mLastClickTime = 0;

    private static final String JSON_URLKelolaDonatur = "https://prasyah.000webhostapp.com/getDataKelolaDonatur.php";
    private JsonArrayRequest requestKelolaDonatur;
    private RequestQueue requestQueueKelolaDonatur;
    private RecyclerView recyclerViewKelolaDonatur;
    private List<ModelKelolaDonatur> modelKelolaDonatur;

    private TextView backKelolaDonatur,infoLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_kelola_donatur);

        Init();
    }

    private void Init() {
        //ProgressBar
        progressBar = findViewById(R.id.progressBarKelolaDonatur);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        infoLoading = findViewById(R.id.infoLoadingData_kelolaDonatur);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getDataDonaturAll();
            } else {
                Intent i = new Intent(KelolaDonatur.this,NetworkErrorActivity.class);
                startActivity(i);
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        modelKelolaDonatur = new ArrayList<>();
        recyclerViewKelolaDonatur = findViewById(R.id.recyclerviewKelolaDonatur);

        backKelolaDonatur = findViewById(R.id.btnBack_kelolaDonatur);
        backKelolaDonatur.setOnClickListener(new View.OnClickListener() {
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

    private void getDataDonaturAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (pStatus <= 100) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(pStatus);
                            if(pStatus==2){
                                jsonReqForAllDataDonatur();
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

    private void jsonReqForAllDataDonatur() {
        requestKelolaDonatur = new JsonArrayRequest(JSON_URLKelolaDonatur, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                for (int i = 0; i < response.length(); i++){
                    try {
                        jsonObject = response.getJSONObject(i);
                        ModelKelolaDonatur model = new ModelKelolaDonatur();

                        model.setId_reg_donatur(jsonObject.getString("id_reg_donatur"));
                        model.setNama_donatur(jsonObject.getString("nama_donatur"));
                        model.setEmail_donatur(jsonObject.getString("email_donatur"));
                        model.setFoto_donatur(jsonObject.getString("foto_donatur"));

                        infoLoading.setVisibility(View.GONE);

                        modelKelolaDonatur.add(model);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setuprecyclerview(modelKelolaDonatur);
                runAnimation(recyclerViewKelolaDonatur,0);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(KelolaDonatur.this, "ERROR CODE 404", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueueKelolaDonatur = Volley.newRequestQueue(KelolaDonatur.this);
        requestQueueKelolaDonatur.add(requestKelolaDonatur);
    }

    private void runAnimation(RecyclerView recyclerView, int type) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;

        if (type == 0){
            controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_fall_down);
        }

        recyclerView.setLayoutAnimation(controller);
        setuprecyclerview(modelKelolaDonatur);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void setuprecyclerview(List<ModelKelolaDonatur> modelKelolaDonaturs) {
        AdapterKelolaDonatur myKelolaDonaturAdapter = new AdapterKelolaDonatur(this,modelKelolaDonaturs);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(myAdapter);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewKelolaDonatur.setLayoutManager(mLayoutManager);
        recyclerViewKelolaDonatur.addItemDecoration(new KelolaDonatur.GridSpacingItemDecoration(2, dpToPx(3), true));
        recyclerViewKelolaDonatur.setItemAnimator(new DefaultItemAnimator());
        recyclerViewKelolaDonatur.setAdapter(myKelolaDonaturAdapter);
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