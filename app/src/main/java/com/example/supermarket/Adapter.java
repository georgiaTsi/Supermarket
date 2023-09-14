package com.example.supermarket;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private FirstFragment firstFragment;

    private List<FirstFragment.ItemClass> dataSet;

    public Adapter(FirstFragment firstFragment, List<FirstFragment.ItemClass> dataSet){
        this.firstFragment = firstFragment;
        this.dataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textview.setText(dataSet.get(position).Item);

        holder.checkBox.setChecked(dataSet.get(position).IsChecked);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void updateAdapter(List<FirstFragment.ItemClass> newDataset) {
        dataSet = newDataset;

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textview;
        private CheckBox checkBox;
        private ImageButton delete;

        public ViewHolder(View view){
            super(view);

            textview = view.findViewById(R.id.textview_viewholder);
            checkBox = view.findViewById(R.id.checkbox_viewholder);
            delete = view.findViewById(R.id.imagebutton_viewholder_delete);

            textview.setOnClickListener(view1 -> checkBox.setChecked(!checkBox.isChecked()));

            checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                if(isChecked){
                    textview.setPaintFlags(textview.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else{
                    textview.setPaintFlags(textview.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                }

                firstFragment.updateRow(textview.getText().toString(), isChecked);
            });

            delete.setOnClickListener(view12 -> firstFragment.deleteRow(textview.getText().toString()));
        }
    }
}
