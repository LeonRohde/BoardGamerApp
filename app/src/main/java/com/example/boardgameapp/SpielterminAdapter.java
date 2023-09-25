package com.example.boardgameapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SpielterminAdapter extends RecyclerView.Adapter<SpielterminAdapter.ViewHolder> {
    private List<Spieltermin> spieltermine;
    private Context context;
    private final SpielterminInterface spielterminInterface;

    public SpielterminAdapter(Context context, List<Spieltermin> spieltermine, SpielterminInterface spielterminInterface) {
        this.spieltermine = spieltermine;
        this.context = context;
        this.spielterminInterface = spielterminInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spieltermin, parent, false);
        return new ViewHolder(view, spielterminInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Spieltermin spieltermin = spieltermine.get(position);
        holder.vornameTextView.setText("Gastegeber: " + spieltermin.getVorname());
        holder.ortTextView.setText("Ort: " + spieltermin.getOrt());
        holder.spieldtTextView.setText("Spieldt: " + spieltermin.getSpieldt());
        String email = spieltermin.getEmail();
    }

    @Override
    public int getItemCount() {
        return spieltermine.size();
    }

    public void updateData(List<Spieltermin> newData) {
        spieltermine.clear();
        spieltermine.addAll(newData);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView vornameTextView;
        TextView ortTextView;
        TextView spieldtTextView;

        public ViewHolder(@NonNull View itemView, SpielterminInterface spielterminInterface) {
            super(itemView);
            vornameTextView = itemView.findViewById(R.id.vornameTextView);
            ortTextView = itemView.findViewById(R.id.ortTextView);
            spieldtTextView = itemView.findViewById(R.id.spieldtTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("SpielterminAdapter", "OnClick sollte ausgeführt werden");
                    if (spielterminInterface != null){
                        int pos = getLayoutPosition();
                        Log.d("SpielterminAdapter", "Position: " + pos);
                        if (pos != RecyclerView.NO_POSITION){
                            spielterminInterface.onItemClick(pos);

                        }
                    }
                }
            });
        }
    }
}
