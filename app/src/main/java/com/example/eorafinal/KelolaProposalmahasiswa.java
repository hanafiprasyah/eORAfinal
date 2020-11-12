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

public class KelolaProposalmahasiswa extends AppCompatActivity {

    private ProgressBar progressBar;
    private int pStatus = 0;
    private Handler handler = new Handler();
    ConnectivityManager conMgr;
    private long mLastClickTime = 0;

    private static final String JSON_URLKelolaProposal = "https://prasyah.000webhostapp.com/getDataKelolaProposal.php";
    private JsonArrayRequest requestKelolaProposal;
    private RequestQueue requestQueueKelolaProposal;
    private RecyclerView recyclerViewKelolaProposal;
    private List<ModelKelolaProposal> modelKelolaProposal;

    private TextView backKelolaProposal,infoLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_kelola_proposalmahasiswa);

        //ProgressBar
        progressBar = findViewById(R.id.progressBarKelolaProposal);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        infoLoading = findViewById(R.id.infoLoadingData_kelolaProposal);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getAllDataProposal();
            } else {
                Intent i = new Intent(KelolaProposalmahasiswa.this,NetworkErrorActivity.class);
                startActivity(i);
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        modelKelolaProposal = new ArrayList<>();
        recyclerViewKelolaProposal = findViewById(R.id.recyclerviewKelolaProposal);

        backKelolaProposal = findViewById(R.id.btnBack_kelolaProposal);
        backKelolaProposal.setOnClickListener(new View.OnClickListener() {
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

    private void getAllDataProposal(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (pStatus <= 100) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(pStatus);
                            if(pStatus==2){
                                jsonReqForAllDataProposal();
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

    private void jsonReqForAllDataProposal(){
        requestKelolaProposal = new JsonArrayRequest(JSON_URLKelolaProposal, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                for (int i = 0; i < response.length(); i++){
                    try {
                        jsonObject = response.getJSONObject(i);
                        ModelKelolaProposal model = new ModelKelolaProposal();

                        model.setProposal_id(jsonObject.getString("proposal_id"));
                        model.setNIM(jsonObject.getString("NIM"));
                        model.setStatus(jsonObject.getString("status"));

                        infoLoading.setVisibility(View.GONE);

                        modelKelolaProposal.add(model);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setuprecyclerview(modelKelolaProposal);
                runAnimation(recyclerViewKelolaProposal,0);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(KelolaProposalmahasiswa.this, "ERROR CODE 404", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueueKelolaProposal = Volley.newRequestQueue(KelolaProposalmahasiswa.this);
        requestQueueKelolaProposal.add(requestKelolaProposal);
    }

    private void runAnimation(RecyclerView recyclerView, int type) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;

        if (type == 0){
            controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_fall_down);
        }

        recyclerView.setLayoutAnimation(controller);
        setuprecyclerview(modelKelolaProposal);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void setuprecyclerview(List<ModelKelolaProposal> modelProposalKelola) {
        AdapterKelolaProposal myKelolaProposalAdapter = new AdapterKelolaProposal(this,modelProposalKelola);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(myAdapter);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewKelolaProposal.setLayoutManager(mLayoutManager);
        recyclerViewKelolaProposal.addItemDecoration(new KelolaProposalmahasiswa.GridSpacingItemDecoration(2, dpToPx(4), true));
        recyclerViewKelolaProposal.setItemAnimator(new DefaultItemAnimator());
        recyclerViewKelolaProposal.setAdapter(myKelolaProposalAdapter);
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