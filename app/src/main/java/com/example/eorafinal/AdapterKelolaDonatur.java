package com.example.eorafinal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.concurrent.Executor;

class AdapterKelolaDonatur extends RecyclerView.Adapter<AdapterKelolaDonatur.MyViewHolder> {
    private Context mContext;
    private List<ModelKelolaDonatur> mKelolaDonaturs;

    public AdapterKelolaDonatur(Context mContext, List<ModelKelolaDonatur> mData){
        this.mContext = mContext;
        this.mKelolaDonaturs = mData;
    }

    @NonNull
    @Override
    public AdapterKelolaDonatur.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.itemkeloladonatur, parent,false);
        return new AdapterKelolaDonatur.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterKelolaDonatur.MyViewHolder holder, int position) {
        holder.idReg_Donatur.setText(mKelolaDonaturs.get(position).getId_reg_donatur());
        holder.namaDonatur.setText(mKelolaDonaturs.get(position).getNama_donatur());
        holder.emailDonatur.setText(mKelolaDonaturs.get(position).getEmail_donatur());

        holder.btnHapusDataDonatur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.btnLihatPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,CekPasswordDonatur.class);
                String passRegIdDonatur = holder.idReg_Donatur.getText().toString();
                Bundle idRegDonaturBundle = new Bundle();
                idRegDonaturBundle.putString("id_reg_donatur",passRegIdDonatur);
                i.putExtra("id_reg_donatur",mKelolaDonaturs.get(position).getId_reg_donatur());
                i.putExtras(idRegDonaturBundle);
                ((KelolaDonatur)mContext).startActivityForResult(i,2);
                ((KelolaDonatur)mContext).overridePendingTransition(R.anim.fade_in,R.anim.stay);
            }
        });

        String uri = mKelolaDonaturs.get(position).getFoto_donatur();
        Glide.with(holder.itemView.getContext()).load(uri).apply(RequestOptions.centerCropTransform()).into(holder.thumbnailDonatur);
    }

    @Override
    public int getItemCount() {
        return mKelolaDonaturs.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView idReg_Donatur,namaDonatur,emailDonatur;
        Button btnHapusDataDonatur,btnLihatPassword;
        ImageView thumbnailDonatur;
        private Executor executor;
        private BiometricPrompt biometricPrompt;
        private androidx.biometric.BiometricPrompt.PromptInfo promptInfo;

        public MyViewHolder(View itemView){
            super(itemView);
            idReg_Donatur = itemView.findViewById(R.id.RegIDdonatur_kelolaDonatur);
            namaDonatur = itemView.findViewById(R.id.namaDonatur_kelolaDonatur);
            emailDonatur = itemView.findViewById(R.id.emailDonatur_kelolaDonatur);
            btnHapusDataDonatur = itemView.findViewById(R.id.hapusDataDonatur);
            thumbnailDonatur = itemView.findViewById(R.id.thumbnailKelolaDonatur);
            btnLihatPassword = itemView.findViewById(R.id.lihatPassword);
        }
    }
}
