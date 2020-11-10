package com.example.eorafinal;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseView;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends AppCompatActivity {

    NestedScrollView nestedScrollView;
    boolean doubleBackToExitPressedOnce = false;

    FloatingActionButton FABlogin, FABinfo;
    FloatingActionMenu menuEORA;

    StepView stepViewMahasiswa, stepViewDonatur;

    FancyShowCaseView fancyShowCaseView;

    //kontenDelay
    TextView visionTitle,visionText,greeting1,greeting2,titlePanduan,footerText;
    ImageView visionSeparator,vision2,panduanSeparator;
    ImageSlider imageSlider;

    //prevent double click
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        setContentView(R.layout.activity_main);

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingtoolbar);

        //StepViewMahasiswa
        stepViewMahasiswa = findViewById(R.id.step_viewMahasiswa);
        stepViewMahasiswa.getState().steps(new ArrayList<String>(){{
            add("Register");
            add("Login");
            add("Ajukan Proposal");
            add("Upload Berkas");
            add("Verifikasi Admin");
            add("Dapatkan Donasi");
        }}).commit();
        stepViewMahasiswa.setOnStepClickListener(step -> {
            switch (step){
                case 0 :
                    new MaterialTapTargetPrompt.Builder(MainActivity.this)
                            .setTarget(R.id.btn_gotoLogin)
                            .setPrimaryText("Pilihan Register")
                            .setFocalColour(getResources().getColor(R.color.gold))
                            .setBackgroundColour(getResources().getColor(R.color.black90))
                            .setSecondaryTextColour(getResources().getColor(R.color.ghostwhite))
                            .setPrimaryTextColour(getResources().getColor(R.color.gold))
                            .setSecondaryText("Sentuh tombol Menu, lalu pilih opsi Login E-ORA")
                            .show();break;
                case 1 :
                    new MaterialTapTargetPrompt.Builder(MainActivity.this)
                            .setTarget(R.id.btn_gotoLogin)
                            .setPrimaryText("Pilihan Login")
                            .setSecondaryText("Sentuh tombol Menu, lalu pilih opsi Login E-ORA")
                            .setFocalColour(getResources().getColor(R.color.gold))
                            .setBackgroundColour(getResources().getColor(R.color.black90))
                            .setPrimaryTextColour(getResources().getColor(R.color.gold))
                            .setSecondaryTextColour(getResources().getColor(R.color.ghostwhite))
                            .setBackButtonDismissEnabled(true)
                            .show();break;
            }
        });

        //StepViewDonatur
        stepViewDonatur = findViewById(R.id.step_viewDonatur);
        stepViewDonatur.getState().steps(new ArrayList<String>(){{
            add("Register");
            add("Login");
            add("Lihat Mahasiswa");
            add("Pilih Mahasiswa");
            add("Verifikasi Admin");
            add("Berikan Donasi");
        }}).commit();
        stepViewDonatur.setOnStepClickListener(step -> {
            switch (step){
                case 0 :
                    new MaterialTapTargetPrompt.Builder(MainActivity.this)
                            .setTarget(R.id.btn_gotoLogin)
                            .setPrimaryText("Pilihan Register")
                            .setFocalColour(getResources().getColor(R.color.gold))
                            .setBackgroundColour(getResources().getColor(R.color.black90))
                            .setSecondaryTextColour(getResources().getColor(R.color.ghostwhite))
                            .setPrimaryTextColour(getResources().getColor(R.color.gold))
                            .setSecondaryText("Sentuh tombol Menu, lalu pilih opsi Login E-ORA")
                            .show();break;
                case 1 :
                    new MaterialTapTargetPrompt.Builder(MainActivity.this)
                            .setTarget(R.id.btn_gotoLogin)
                            .setPrimaryText("Pilihan Login")
                            .setSecondaryText("Sentuh tombol Menu, lalu pilih opsi Login E-ORA")
                            .setFocalColour(getResources().getColor(R.color.gold))
                            .setBackgroundColour(getResources().getColor(R.color.black90))
                            .setPrimaryTextColour(getResources().getColor(R.color.gold))
                            .setSecondaryTextColour(getResources().getColor(R.color.ghostwhite))
                            .setBackButtonDismissEnabled(true)
                            .show();break;
            }
        });

        //SelamatDatang Words
        greeting1 = findViewById(R.id.TV_selamatDatang1);
        greeting2 = findViewById(R.id.TV_selamatDatang2);
        footerText = findViewById(R.id.tv_footerText);
        Typeface customGreeting = Typeface.createFromAsset(getAssets(),"font/intimacy.ttf");
        greeting1.setTypeface(customGreeting);
        greeting2.setTypeface(customGreeting);
        footerText.setTypeface(customGreeting);

        Animation animForGreetings = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in);
        greeting1.setAnimation(animForGreetings);
        greeting2.setAnimation(animForGreetings);

        imageSlider = findViewById(R.id.slider);
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("https://calonmahasiswa.com/wp-content/uploads/auditorium-unp.jpeg","Sistem Penghubung Mahasiswa Universitas Negeri Padang Dengan Calon Orang Tua Asuhnya"));
        slideModels.add(new SlideModel("https://alibrahgresik.or.id/home/wp-content/uploads/2016/02/3.jpg","Donasi Anda Membantu Mereka"));
        slideModels.add(new SlideModel("https://mimbarsumbar.id/wp-content/uploads/2019/05/IMG-20190507-WA0008.jpg","Dukung Mereka Untuk Menggapai Prestasi"));
        imageSlider.setImageList(slideModels,true);

        FABlogin = findViewById(R.id.btn_gotoLogin);
        FABinfo = findViewById(R.id.bt_floatabout);

        menuEORA        = findViewById(R.id.menuMain);
        menuEORA.setClosedOnTouchOutside(true);
        Animation animMenu = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.item_animation_falldown);
        menuEORA.setAnimation(animMenu);
        menuEORA.setVisibility(View.VISIBLE);

        FABlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                Intent i = new Intent(MainActivity.this,UserLoginActivity.class);
                startActivity(i);
                finishAfterTransition();
                overridePendingTransition(R.anim.slide_in,R.anim.stay);
            }
        });

        FABinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                Intent ab = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(ab);
                finishAfterTransition();
                overridePendingTransition(R.anim.falldown,R.anim.stay);
            }
        });

        visionTitle = findViewById(R.id.tv_visionTitle);
        visionText = findViewById(R.id.tv_visionText);
        visionSeparator = findViewById(R.id.iv_visionSeparator);
        vision2 = findViewById(R.id.iv_vision2);
        titlePanduan = findViewById(R.id.tv_panduanMahasiswaTitle);
        panduanSeparator = findViewById(R.id.iv_panduanSeparator);

        Animation animForContent = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.item_animation_falldown);
        visionTitle.setAnimation(animForContent);
        visionText.setAnimation(animForContent);
        visionSeparator.setAnimation(animForContent);
        vision2.setAnimation(animForContent);
        titlePanduan.setAnimation(animForContent);
        panduanSeparator.setAnimation(animForContent);

        visionTitle.setVisibility(View.VISIBLE);
        visionText.setVisibility(View.VISIBLE);
        visionSeparator.setVisibility(View.VISIBLE);
        vision2.setVisibility(View.VISIBLE);
        titlePanduan.setVisibility(View.VISIBLE);
        panduanSeparator.setVisibility(View.VISIBLE);


        nestedScrollView = findViewById(R.id.nestedView);
        if (nestedScrollView != null){
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    //ScrollDown
                    if (scrollY > oldScrollY) {
                        //collapsingToolbarLayout.setTitleEnabled(true);
                        //menuEORA.hideMenu(true);
                    }
                    //ScrollUp
                    if (scrollY < oldScrollY) {
                        //collapsingToolbarLayout.setTitleEnabled(true);
                    }
                    if (scrollY == 0) {
                        //TopOfView
                        //collapsingToolbarLayout.setTitleEnabled(false);
                    }
                    //BottomOfView
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        //collapsingToolbarLayout.setTitleEnabled(true);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if(doubleBackToExitPressedOnce){
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        FancyToast.makeText(this,"Lakukan sekali lagi untuk keluar", Toast.LENGTH_SHORT, FancyToast.CONFUSING, R.drawable.ic_refresh_black_24dp, false).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        },2000);
    }
}
