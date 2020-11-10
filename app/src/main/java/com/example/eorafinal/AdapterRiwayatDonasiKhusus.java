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

import java.util.List;

class AdapterRiwayatDonasiKhusus extends RecyclerView.Adapter<AdapterRiwayatDonasiKhusus.MyViewHolder>{

    private Context mContext;
    private List<ModelRiwayatDonasiKhusus> mDataRiwayatKhusus;

    public AdapterRiwayatDonasiKhusus(Context mContext, List<ModelRiwayatDonasiKhusus> mDataKhusus){
        this.mContext = mContext;
        this.mDataRiwayatKhusus = mDataKhusus;
    }

    @NonNull
    @Override
    public AdapterRiwayatDonasiKhusus.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.itemriwayatdonasikhusus, parent,false);
        return new AdapterRiwayatDonasiKhusus.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRiwayatDonasiKhusus.MyViewHolder holder, int position) {
        holder.nama_donatur.setText(mDataRiwayatKhusus.get(position).getNama_donatur());
        holder.NIMmhs.setText(mDataRiwayatKhusus.get(position).getNIM());
        holder.namaMhs.setText(mDataRiwayatKhusus.get(position).getNama());
        holder.notelMhs.setText(mDataRiwayatKhusus.get(position).getNotel());

        holder.cv_riwayatDonasiKhusus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,GreetingDonasiKhusus.class);
                String passingNIMToGreetingKhusus = holder.NIMmhs.getText().toString();
                Bundle NIMtoGreeting = new Bundle();
                NIMtoGreeting.putString("NIM",passingNIMToGreetingKhusus);
                i.putExtra("NIM",mDataRiwayatKhusus.get(position).getNIM());
                i.putExtras(NIMtoGreeting);
                ((RiwayatDonasiKhusus)mContext).startActivityForResult(i,2);
                ((RiwayatDonasiKhusus)mContext).overridePendingTransition(R.anim.falldown,R.anim.stay);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataRiwayatKhusus.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nama_donatur,NIMmhs,namaMhs,notelMhs;
        CardView cv_riwayatDonasiKhusus;

        public MyViewHolder(View itemView){
            super(itemView);
            nama_donatur = itemView.findViewById(R.id.nama_donaturRiwayatDonasiKhusus);
            NIMmhs = itemView.findViewById(R.id.NIMmhs_RiwayatDonasiKhusus);
            namaMhs = itemView.findViewById(R.id.namaMhs_RiwayatDonasiKhusus);
            notelMhs = itemView.findViewById(R.id.notelMhs_donasiRiwayatDonasiKhusus);
            cv_riwayatDonasiKhusus = itemView.findViewById(R.id.card_view_layoutRiwayatDonasiKhusus);
        }
    }
}
