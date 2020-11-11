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

class AdapterRiwayatDonasiMahasiswa extends RecyclerView.Adapter<AdapterRiwayatDonasiMahasiswa.MyViewHolder>{
    private Context mContext;
    private List<ModelRiwayatDonasiMahasiswa> mDataRiwayatMahasiswa;

    public AdapterRiwayatDonasiMahasiswa(Context mContext, List<ModelRiwayatDonasiMahasiswa> mDataMahasiswa){
        this.mContext = mContext;
        this.mDataRiwayatMahasiswa = mDataMahasiswa;
    }

    @NonNull
    @Override
    public AdapterRiwayatDonasiMahasiswa.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.itemriwayatmahasiswa, parent,false);
        return new AdapterRiwayatDonasiMahasiswa.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRiwayatDonasiMahasiswa.MyViewHolder holder, int position) {
        holder.NIM.setText(mDataRiwayatMahasiswa.get(position).getNIM());
        holder.nama_donatur.setText(mDataRiwayatMahasiswa.get(position).getNama_donatur());
        holder.status.setText(mDataRiwayatMahasiswa.get(position).getStatus_donasi());
        holder.tvIdRegDetailDonatur.setText((mDataRiwayatMahasiswa.get(position).getId_reg_donatur()));

        holder.cv_riwayatDonasiMahasiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,DonaturDetail.class);
                String passingidDonaturToDonaturDetail = holder.tvIdRegDetailDonatur.getText().toString();
                Bundle idDonaturtoDonaturDetail = new Bundle();
                idDonaturtoDonaturDetail.putString("NIM",passingidDonaturToDonaturDetail);
                i.putExtra("id_reg_donatur",mDataRiwayatMahasiswa.get(position).getId_reg_donatur());
                i.putExtras(idDonaturtoDonaturDetail);
                ((RiwayatDonasiMahasiswa)mContext).startActivityForResult(i,2);
                ((RiwayatDonasiMahasiswa)mContext).overridePendingTransition(R.anim.falldown,R.anim.stay);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataRiwayatMahasiswa.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView NIM,nama_donatur,status,tvIdRegDetailDonatur;
        CardView cv_riwayatDonasiMahasiswa;

        public MyViewHolder(View itemView){
            super(itemView);
            NIM = itemView.findViewById(R.id.NIMmahasiswa_riwayatDonasiMahasiswa);
            nama_donatur = itemView.findViewById(R.id.namaDonatur_riwayatDonasiMahasiswa);
            status = itemView.findViewById(R.id.status_riwayatDonasiMahasiswa);
            cv_riwayatDonasiMahasiswa = itemView.findViewById(R.id.card_view_layoutRiwayatDonasiMahasiswa);
            tvIdRegDetailDonatur = itemView.findViewById(R.id.idRegdonatur_riwayatMahasiswa);
        }
    }
}
