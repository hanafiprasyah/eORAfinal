package com.example.eorafinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class AdapterLihatDonatur extends RecyclerView.Adapter<AdapterLihatDonatur.MyViewHolder> {

    private Context mContext;
    private List<ModelLihatDonatur> mData;
    RequestOptions options;

    public AdapterLihatDonatur(Context mContext, List<ModelLihatDonatur> mData){
        this.mContext = mContext;
        this.mData = mData;
        options = new RequestOptions().centerCrop().placeholder(R.drawable.ic_cloud_upload_black_24dp).error(R.drawable.errorfor_imageslider);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.donatur_item, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelLihatDonatur modelLihatDonatur = mData.get(position);

        holder.nama_donatur.setText(mData.get(position).getNama_donatur());
        holder.pekerjaan_donatur.setText(mData.get(position).getPekerjaan_donatur());

        //LoadImage
        Glide.with(mContext).load(modelLihatDonatur.getFoto_donatur()).apply(options).into(holder.foto_donatur);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nama_donatur,pekerjaan_donatur;
        ImageView foto_donatur;

        public MyViewHolder(View itemView){
            super(itemView);
            nama_donatur = itemView.findViewById(R.id.nama_donatur);
            pekerjaan_donatur = itemView.findViewById(R.id.pekerjaan_donatur);
            foto_donatur = itemView.findViewById(R.id.thumbnail);
        }
    }
}
