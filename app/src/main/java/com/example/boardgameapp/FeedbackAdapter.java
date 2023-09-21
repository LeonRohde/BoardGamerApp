package com.example.boardgameapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
    private Context context;
    private ArrayList<FeedbackItem> feedbackList;

    public FeedbackAdapter(Context context, ArrayList<FeedbackItem> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
    }


    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Erstellt eine neue Ansicht für jedes Element in der RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        FeedbackItem feedbackItem = feedbackList.get(position);

        // Setzt die Daten für jedes Element in der RecyclerView
        holder.spielerTextView.setText(feedbackItem.getSpieler());
        holder.gastgeberBewertungTextView.setText("Gastgeber:" + feedbackItem.getGastgeber());
        holder.essenBewertungTextView.setText("Essen: " + feedbackItem.getEssen());
        holder.abendBewertungTextView.setText("Abend: " + feedbackItem.getAbend());
    }

    @Override
    public int getItemCount() {
        // Gibt die Gesamtanzahl der Elemente in der RecyclerView zurück
        return feedbackList.size();
    }

    public class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView spielerTextView;
        TextView gastgeberBewertungTextView;
        TextView essenBewertungTextView;
        TextView abendBewertungTextView;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            spielerTextView = itemView.findViewById(R.id.spielerTextView);
            gastgeberBewertungTextView = itemView.findViewById(R.id.gastgeberBewertungTextView);
            essenBewertungTextView = itemView.findViewById(R.id.essenBewertungTextView);
            abendBewertungTextView = itemView.findViewById(R.id.abendBewertungTextView);
        }
    }
}
