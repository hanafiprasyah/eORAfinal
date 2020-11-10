package com.example.eorafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.shashank.sony.fancytoastlib.FancyToast;

public class GreetingDonasiUmum extends AppCompatActivity {
    TextView copyNorek,backtoDonaturHome,greetingDonasiUmum;
    private ClipboardManager mClipManager;
    private ClipData myClip;

    LottieAnimationView lottieWA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_greeting_donasi_umum);

        copyNorek = findViewById(R.id.norekToCopy);
        mClipManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        copyNorek.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String text;
                text = copyNorek.getText().toString();
                myClip = ClipData.newPlainText("text",text);
                mClipManager.setPrimaryClip(myClip);

                Toast.makeText(getApplicationContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        greetingDonasiUmum = findViewById(R.id.thanksDonasiUmum);
        Typeface customGreeting = Typeface.createFromAsset(getAssets(),"font/intimacy.ttf");
        greetingDonasiUmum.setTypeface(customGreeting);

        lottieWA = findViewById(R.id.btn_lottieWABuktiTransfer);
        lottieWA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String url = "https://api.whatsapp.com/send?phone=" + "+6282174651666";
               Intent i = new Intent(Intent.ACTION_VIEW);
               i.setData(Uri.parse(url));
               startActivity(i);
            }
        });

        backtoDonaturHome = findViewById(R.id.btnBacktoDonaturHome);
        backtoDonaturHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FancyToast.makeText(GreetingDonasiUmum.this,"Terima kasih sudah berpartisipasi", Toast.LENGTH_SHORT, FancyToast.INFO, R.drawable.ic_done, false).show();
                finishAfterTransition();
                overridePendingTransition(0,R.anim.slide_outta_right);
            }
        });
    }

    @Override
    public void onBackPressed() {
        FancyToast.makeText(GreetingDonasiUmum.this,"Terima kasih sudah berpartisipasi", Toast.LENGTH_SHORT, FancyToast.INFO, R.drawable.ic_done, false).show();
        finishAfterTransition();
        overridePendingTransition(0,R.anim.slide_outta_right);
    }
}