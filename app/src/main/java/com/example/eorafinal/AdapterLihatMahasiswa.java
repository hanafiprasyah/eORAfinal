package com.example.eorafinal;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Objects;

public class AdapterLihatMahasiswa extends RecyclerView.Adapter<AdapterLihatMahasiswa.MyViewHolderMahasiswa> {
    private Context mContext;
    private List<ModelLihatMahasiswa> mData;
    RequestOptions options;

    public class MyViewHolderMahasiswa extends RecyclerView.ViewHolder {
        public TextView namaMahasiswa,NIMmahasiswa,idRegLihatMahasiswa;
        public ImageView fotoMahasiswa;
        public CardView cv_mahasiswa;

        public MyViewHolderMahasiswa(View itemViewMhs){
            super(itemViewMhs);
            namaMahasiswa = itemViewMhs.findViewById(R.id.nama_mahasiswa);
            NIMmahasiswa = itemViewMhs.findViewById(R.id.NIM_mahasiswa);
            fotoMahasiswa = itemViewMhs.findViewById(R.id.thumbnailMahasiswa);
            cv_mahasiswa = itemViewMhs.findViewById(R.id.card_view_layoutMahasiswa);
            idRegLihatMahasiswa = itemViewMhs.findViewById(R.id.tv_idRegLihatMahasiswa);

//            Bundle idRegToPilihDonasiKhusus = ((Activity) mContext).getIntent().getExtras();
//            String revIDRegPilihDonasiKhusus = Objects.requireNonNull(idRegToPilihDonasiKhusus).getString("id_reg_donatur");
//            idRegDonaturKhusus = itemViewMhs.findViewById(R.id.tv_idRegDonaturKhusus);
        }
    }

    public AdapterLihatMahasiswa(Context mContext, List<ModelLihatMahasiswa> mData){
        this.mContext = mContext;
        this.mData = mData;
        options = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
    }


    @NonNull
    @Override
    public AdapterLihatMahasiswa.MyViewHolderMahasiswa onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.mahasiswa_item, parent,false);
        return new AdapterLihatMahasiswa.MyViewHolderMahasiswa(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLihatMahasiswa.MyViewHolderMahasiswa holder, final int position) {
        ModelLihatMahasiswa modelLihatMahasiswa = mData.get(position);
        holder.namaMahasiswa.setText(mData.get(position).getNama_mahasiswa());
        holder.NIMmahasiswa.setText(mData.get(position).getNIM_mahasiswa());
        //LoadImage
        Glide.with(mContext).load(modelLihatMahasiswa.getFoto_mahasiswa()).apply(options).into(holder.fotoMahasiswa);

        Bundle idRegToLihatMahasiswa = ((LihatMahasiswa)mContext).getIntent().getExtras();
        String revIDRegLihatMahasiswa = Objects.requireNonNull(idRegToLihatMahasiswa).getString("id_reg_donatur");
        holder.idRegLihatMahasiswa.setText(revIDRegLihatMahasiswa);

        holder.cv_mahasiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,MahasiswaDetail.class);
                String passingIDdonaturToDetailMahasiswa = holder.idRegLihatMahasiswa.getText().toString();
                Bundle idRegToDetailMahasiswa = new Bundle();
                idRegToDetailMahasiswa.putString("id_reg_donatur",passingIDdonaturToDetailMahasiswa);
                i.putExtra("nama",mData.get(position).getNama_mahasiswa());
                i.putExtra("NIM",mData.get(position).getNIM_mahasiswa());
                i.putExtra("image_path",mData.get(position).getFoto_mahasiswa());
                i.putExtras(idRegToDetailMahasiswa);
                ((LihatMahasiswa)mContext).startActivityForResult(i,2);
                ((LihatMahasiswa)mContext).overridePendingTransition(R.anim.falldown,R.anim.stay);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
