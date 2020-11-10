package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends AppCompatActivity {

    TextView back,titleheader1;
    NestedScrollView nestedScrollViewAbout;
    FrameLayout aboutFrame;

    //prevent double click
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_about);

        loadAbout();

        Typeface customHeader2 = Typeface.createFromAsset(getAssets(),"font/intimacy.ttf");

        titleheader1 = findViewById(R.id.txt_titleHeader1);
        titleheader1.setTypeface(customHeader2);

        back = findViewById(R.id.btnBacktoMain);
        Animation animBackAbout = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in);
        back.setAnimation(animBackAbout);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent o = new Intent(AboutActivity.this,MainActivity.class);
                startActivity(o);
                finishAfterTransition();
                overridePendingTransition(R.anim.falldown,R.anim.stay);
            }
        });
    }

    public void loadAbout(){
        final FrameLayout flHolder = this.findViewById(R.id.aboutFrame);

        AboutView view = AboutBuilder.with(this)
                .setPhoto(R.drawable.fotohanafi)
                .setCover(R.drawable.backgroundunp1)
                .setName("Muhammad Hanafi Prasyah")
                .setSubTitle("16076067")
                .setBrief("Plain - Do - Check - Action")
                .setAppIcon(R.mipmap.ic_launcher)
                .setAppName(R.string.app_name)
                .setVersionNameAsAppSubTitle()
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(true)
                .setLinksColumnsCount(4)
                .addInstagramLink("prasyaah_")
                .addWhatsappLink("Hanafi","+62082174651666")
                .addYoutubeChannelLink("Muhammad Hanafi Prasyah")
                .addEmailLink("kingmandora98@gmail.com")
                .setActionsColumnsCount(2)
                .setShowDivider(true)
                .build();

        flHolder.addView(view);
    }

    @Override
    public void onBackPressed() {
        Intent ob = new Intent(AboutActivity.this,MainActivity.class);
        startActivity(ob);
        finishAfterTransition();
        overridePendingTransition(R.anim.falldown,R.anim.stay);
    }
}
