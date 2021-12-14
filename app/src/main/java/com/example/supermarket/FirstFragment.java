package com.example.supermarket;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class FirstFragment extends Fragment {

    SQLiteDatabase myDatabase;

    Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_main);

        initRecyclerView(recyclerView);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    private void initRecyclerView(RecyclerView recyclerView){
        ArrayList<ItemClass> items = readFromDatabase();

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        adapter = new Adapter(this, items);
        recyclerView.setAdapter(adapter);
    }

    private void updateRecyclerView(){
        ArrayList<ItemClass> items = readFromDatabase();
        adapter.updateAdapter(items);
    }

    private ArrayList<ItemClass> readFromDatabase(){
        ArrayList<ItemClass> items = new ArrayList<ItemClass>();

        myDatabase = this.getActivity().openOrCreateDatabase("SupermarketDatabase", Context.MODE_PRIVATE,null);

        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS SupermarketList(Item VARCHAR,IsChecked BOOLEAN);");

        Cursor resultSet = myDatabase.rawQuery("Select * from SupermarketList",null);
        resultSet.moveToFirst();

        if (resultSet != null) {
            // Loop through all Results
            do {
                String item = resultSet.getString(0);

                Boolean isChecked = Boolean.valueOf(resultSet.getString(1));

                items.add(new ItemClass(item, isChecked));
            }while(resultSet.moveToNext());
        }

        return items;
    }

    public void addItem(String itemName){
        myDatabase.execSQL("INSERT INTO SupermarketList (Item, IsChecked) VALUES('" + itemName + "', 'false');");

        updateRecyclerView();
    }

    public void deleteRow(String item){
        myDatabase.execSQL("DELETE FROM SupermarketList WHERE Item = '" + item + "'");

        updateRecyclerView();
    }

    public void updateRow(String item, boolean isChecked){
        String query = String.format("UPDATE SupermarketList SET IsChecked = '%b' WHERE Item = '" + item + "'", isChecked);
        myDatabase.execSQL(query);
    }

    public class ItemClass{
        public String Item;
        public Boolean IsChecked;

        public ItemClass(String item, boolean isChecked){
            Item = item;
            IsChecked = isChecked;
        }
    }
}