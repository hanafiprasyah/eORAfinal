package com.example.eorafinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class AdapterKelolaProposal extends RecyclerView.Adapter<AdapterKelolaProposal.MyViewHolder> {
    private Context mContext;
    private List<ModelKelolaProposal> mKelolaProposals;

    public AdapterKelolaProposal(Context mContext, List<ModelKelolaProposal> mData){
        this.mContext = mContext;
        this.mKelolaProposals = mData;
    }

    @NonNull
    @Override
    public AdapterKelolaProposal.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.itemkelolaproposal, parent,false);
        return new AdapterKelolaProposal.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterKelolaProposal.MyViewHolder holder, int position) {
        holder.idProposal.setText(mKelolaProposals.get(position).getProposal_id());
        holder.NIM.setText(mKelolaProposals.get(position).getNIM());
        holder.statusDonasi.setText(mKelolaProposals.get(position).getStatus());

        holder.cv_gotoDetailProposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,DetailProposal.class);
                String passingProposalID = holder.idProposal.getText().toString();
                Bundle proposalIDbundle = new Bundle();
                proposalIDbundle.putString("proposal_id",passingProposalID);
                i.putExtra("proposal_id",mKelolaProposals.get(position).getProposal_id());
                i.putExtras(proposalIDbundle);
                ((KelolaProposalmahasiswa)mContext).startActivityForResult(i,2);
                ((KelolaProposalmahasiswa)mContext).overridePendingTransition(R.anim.fade_in,R.anim.stay);
            }
        });

        if (holder.statusDonasi.getText().equals("diterima")){
            holder.statusDonasi.setTextColor(Color.parseColor("#FF00FF00"));
        } else if (holder.statusDonasi.getText().equals("upload berkas")){
            holder.statusDonasi.setTextColor(Color.parseColor("#FFD700"));
        } else if (holder.statusDonasi.getText().equals("ditolak")){
            holder.statusDonasi.setTextColor(Color.parseColor("#DC143C"));
        } else if (holder.statusDonasi.getText().equals("menunggu")){
            holder.statusDonasi.setTextColor(Color.parseColor("#6495ED"));
        } else if (holder.statusDonasi.getText().equals("perbaikan")){
            holder.statusDonasi.setTextColor(Color.parseColor("#FFD700"));
        }
    }

    @Override
    public int getItemCount() {
        return mKelolaProposals.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView idProposal,NIM,statusDonasi;
        CardView cv_gotoDetailProposal;

        public MyViewHolder(View itemView){
            super(itemView);
            idProposal = itemView.findViewById(R.id.proposalID_kelolaProposal);
            NIM = itemView.findViewById(R.id.NIM_kelolaProposal);
            statusDonasi = itemView.findViewById(R.id.status_kelolaProposal);
            cv_gotoDetailProposal = itemView.findViewById(R.id.card_view_layoutKelolaProposal);
        }
    }
}
