package com.example.supermarket;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<String> dataSet;

    public Adapter(List<String> dataSet){
        this.dataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textview.setText(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textview;
        private CheckBox checkBox;

        public ViewHolder(View view){
            super(view);

            textview = view.findViewById(R.id.textview_viewholder);
            checkBox = view.findViewById(R.id.checkbox_viewholder);

            textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkBox.setChecked(!checkBox.isChecked());
                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked){
                        textview.setPaintFlags(textview.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    else{
                        textview.setPaintFlags(textview.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                }
            });
        }
    }
}
