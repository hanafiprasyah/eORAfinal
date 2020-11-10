package com.example.eorafinal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;

import java.util.List;

class AdapterRiwayatDonasi extends RecyclerView.Adapter<AdapterRiwayatDonasi.MyViewHolder>{

    private Context mContext;
    private List<ModelRiwayatDonasi> mDataRiwayat;
    RequestOptions options;

    public AdapterRiwayatDonasi(Context mContext, List<ModelRiwayatDonasi> mData){
        this.mContext = mContext;
        this.mDataRiwayat = mData;
        options = new RequestOptions().centerCrop().placeholder(R.drawable.ic_cloud_upload_black_24dp).error(R.drawable.errorfor_imageslider);
    }

    @NonNull
    @Override
    public AdapterRiwayatDonasi.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.itemriwayatdonasi, parent,false);
        return new AdapterRiwayatDonasi.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRiwayatDonasi.MyViewHolder holder, int position) {
        holder.nama_donatur.setText(mDataRiwayat.get(position).getNama_donatur());
        holder.jumlah_donasi.setText(mDataRiwayat.get(position).getJumlah_donasi());
        holder.jenis_donasi.setText(mDataRiwayat.get(position).getJenis_donasi());

        holder.cv_riwayatDonasiUmum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent x = new Intent(mContext,GreetingDonasiUmum.class);
                ((RiwayatDonasi)mContext).startActivityForResult(x,1);
                ((RiwayatDonasi)mContext).overridePendingTransition(R.anim.falldown,R.anim.stay);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataRiwayat.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nama_donatur,jumlah_donasi,jenis_donasi;
        public CardView cv_riwayatDonasiUmum;

        public MyViewHolder(View itemView){
            super(itemView);
            nama_donatur = itemView.findViewById(R.id.nama_donaturRiwayatDonasi);
            jumlah_donasi = itemView.findViewById(R.id.jumlahDonasi_RiwayatDonasi);
            jenis_donasi = itemView.findViewById(R.id.jenis_donasiRiwayatDonasi);
            cv_riwayatDonasiUmum = itemView.findViewById(R.id.card_view_layoutRiwayatDonasi);
        }
    }
}
