package com.example.eorafinal;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class LihatMahasiswa extends AppCompatActivity {

    LottieAnimationView loadingLottie;

    private static final String JSON_URL = "https://prasyah.000webhostapp.com/getMahasiswaFromStatus.php";
    private JsonArrayRequest request;
    private RequestQueue requestQueue;
    private List<ModelLihatMahasiswa> modelMhs;
    private RecyclerView recyclerViewLihatMahasiswa;

    //ProgressBar
    private ProgressBar progressBar;
    private int pStatus = 0;
    private Handler handler = new Handler();

    ConnectivityManager conMgr;
    ObjectAnimator lottieObject;

    //back
    TextView back,idRegDonaturKhusus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_lihat_mahasiswa);

        //ProgressBar
        progressBar = findViewById(R.id.progressBarLihatMahasiswa);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        loadingLottie = findViewById(R.id.lottieLoadingLihatMahasiswa);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getDataMahasiswa();
            } else {
                Intent i = new Intent(LihatMahasiswa.this,NetworkErrorActivity.class);
                startActivity(i);
            }
        }

        modelMhs = new ArrayList<>();
        recyclerViewLihatMahasiswa = findViewById(R.id.recyclerviewLihatMahasiswa);

        back = findViewById(R.id.btnBacktoDonatur);
        Animation animBackForLihatMahasiswa = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in);
        back.setAnimation(animBackForLihatMahasiswa);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
                overridePendingTransition(0,R.anim.slide_outta_right);
            }
        });
    }

    private void getDataMahasiswa() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (pStatus <= 100) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(pStatus);
                            if(pStatus==10){
                                jsonReq();
                            }
                        }
                    });
                    try {
                        Thread.sleep(50);
                        if (pStatus==100){
                            progressBar.getMax();
                            lottieObject.pause();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pStatus++;
                }
            }
        }).start();
    }

    private void jsonReq() {
        request = new JsonArrayRequest(JSON_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                for (int i = 0; i < response.length(); i++){
                    try {
                        jsonObject = response.getJSONObject(i);
                        ModelLihatMahasiswa modelMahasiswa = new ModelLihatMahasiswa();
                        modelMahasiswa.setNama_mahasiswa(jsonObject.getString("nama"));
                        modelMahasiswa.setNIM_mahasiswa(jsonObject.getString("NIM"));
                        modelMahasiswa.setFoto_mahasiswa(jsonObject.getString("image_path"));
                        modelMhs.add(modelMahasiswa);

                        lottieObject = ObjectAnimator.ofFloat(loadingLottie,"alpha",0f);
                        lottieObject.start();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setuprecyclerview(modelMhs);
                runAnimation(recyclerViewLihatMahasiswa,0);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue = Volley.newRequestQueue(LihatMahasiswa.this);
        requestQueue.add(request);

    }

    private void runAnimation(RecyclerView recyclerView, int type) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;

        if (type == 0){
            controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_fall_down);
        }
        recyclerView.setLayoutAnimation(controller);
        setuprecyclerview(modelMhs);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void setuprecyclerview(List<ModelLihatMahasiswa> model) {
        AdapterLihatMahasiswa myAdapter = new AdapterLihatMahasiswa(this,model);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(myAdapter);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewLihatMahasiswa.setLayoutManager(mLayoutManager);
        recyclerViewLihatMahasiswa.addItemDecoration(new LihatMahasiswa.GridSpacingItemDecoration(2, dpToPx(8), true));
        recyclerViewLihatMahasiswa.setItemAnimator(new DefaultItemAnimator());
        recyclerViewLihatMahasiswa.setAdapter(myAdapter);
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