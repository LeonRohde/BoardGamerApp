package com.example.boardgameapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class SpielterminAdapter extends RecyclerView.Adapter<SpielterminAdapter.ViewHolder> {

    private List<Spieltermin> spieltermine;

    public SpielterminAdapter(List<Spieltermin> spieltermine) {

        this.spieltermine = spieltermine;
    }

    public SpielterminAdapter(ArrayList<Player> playerList) {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spieltermin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Spieltermin spieltermin = spieltermine.get(position);
        holder.dateTextView.setText("Datum: " + spieltermin.getDate());
        holder.locationTextView.setText("Ort: " + spieltermin.getLocation());
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
        TextView dateTextView;
        TextView locationTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
        }
    }
}
