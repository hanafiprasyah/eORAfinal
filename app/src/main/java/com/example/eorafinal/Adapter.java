package com.example.eorafinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class Adapter extends PagerAdapter {

    private long mLastClickTime = 0;

    private List<Model> models;
    private LayoutInflater layoutInflater;
    private Context context;

    public Adapter(List<Model> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item,container,false);

        ImageView imageView;
        TextView title, desc, btnBackToMenuUtama;

        imageView = view.findViewById(R.id.image);
        title = view.findViewById(R.id.title);
        desc = view.findViewById(R.id.desc);
        btnBackToMenuUtama = view.findViewById(R.id.btn_backToMenuUtama);

        imageView.setImageResource(models.get(position).getImage());
        title.setText(models.get(position).getTitle());
        desc.setText(models.get(position).getDesc());

        CardView cardView = view.findViewById(R.id.cardViewLogin);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (models.get(position).getTitle().equals("Mahasiswa")){
                    //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent intent = new Intent(context, MahasiswaLogin.class);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in,R.anim.stay);

                    //Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_down);
                    //viewPager.startAnimation(animSlideDown);
                } else if (models.get(position).getTitle().equals("Donatur")){
                    //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent intent = new Intent(context, DonaturLogin.class);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in,R.anim.stay);
                }

            }
        });

        btnBackToMenuUtama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent back = new Intent(context, MainActivity.class);
                context.startActivity(back);
                ((Activity) context).finishAfterTransition();
                ((Activity) context).overridePendingTransition(0, R.anim.slide_out);
            }
        });

        container.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
