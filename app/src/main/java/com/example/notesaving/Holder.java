package com.example.notesaving;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Holder extends RecyclerView.ViewHolder {

    public TextView textViewDate, textViewMessage,textViewTitle;


    public Holder(@NonNull View itemView) {
        super(itemView);
        textViewDate=itemView.findViewById(R.id.layoutdate);
        textViewMessage=itemView.findViewById(R.id.layoutmessage);
       textViewTitle=itemView.findViewById(R.id.layouttitle);
    }
}
