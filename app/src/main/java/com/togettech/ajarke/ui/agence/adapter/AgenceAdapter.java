package com.togettech.ajarke.ui.agence.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.togettech.ajarke.R;
import com.togettech.ajarke.ui.agence.model.Agence;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AgenceAdapter extends RecyclerView.Adapter<AgenceAdapter.ViewHolder> {

    private Context mContext;
    private List<Agence> agenceList;

    public AgenceAdapter(Context mContext, List<Agence> agenceList, boolean b){
        this.mContext = mContext;
        this.agenceList = agenceList;
    }

    @NonNull
    @Override
    public AgenceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_agence_list, parent, false);
        return new AgenceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgenceAdapter.ViewHolder holder, int position) {
        final Agence agence = agenceList.get(position);

        Glide.with(mContext)
                .load(agence.getAgence_logo())
                .into(holder.agence_logo);
        holder.agence_name.setText(agence.getAgence_name());
        holder.agence_place.setText(agence.getAgence_place());
    }

    @Override
    public int getItemCount() {
        return agenceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView agence_logo;
        public TextView agence_name;
        public TextView agence_place;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            agence_logo = itemView.findViewById(R.id.agence_logo);
            agence_name = itemView.findViewById(R.id.agence_name);
            agence_place = itemView.findViewById(R.id.agence_place);
        }
    }
}
