package com.example.supermarket;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

public class FirstFragment extends Fragment {

    SQLiteDatabase myDatabase;

    Adapter adapter;

    boolean isRecording = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_main);

        initRecyclerView(recyclerView);

        return view;
    }

    public void initSpeechRecognizer(FloatingActionButton button){
        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.getActivity());

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }

            @Override
            public void onBeginningOfSpeech() {
                Toast.makeText(getActivity(), "Listening...", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRmsChanged(float v) {
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int i) {
            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String result = data.get(0);

                if(result.contains("πρόσθεσε") || result.contains("add") || result.contains("and") || result.contains("αν")){
                    String itemName = (result.split(" ", 2))[1];
                    addItem(itemName);
                }
                else if(result.contains("delete") || result.contains("αφαίρεσε") || result.contains("αφαίρεση")){
                    String itemName = (result.split(" ", 2))[1];
                    updateRow(itemName, true);
                    updateRecyclerView();
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        });

        button.setOnClickListener(view -> {
            if(!isRecording) {
                speechRecognizer.startListening(speechRecognizerIntent);

                button.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            }
            else {
                speechRecognizer.stopListening();

                button.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(213,181,253)));
            }

            isRecording = !isRecording;
        });
    }

    //region RecyclerView
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

        ArrayList<ItemClass> items = new ArrayList<>();

        try {
            myDatabase = this.getActivity().openOrCreateDatabase("SupermarketDatabase", Context.MODE_PRIVATE, null);

            myDatabase.execSQL("CREATE TABLE IF NOT EXISTS SupermarketList(Item VARCHAR,IsChecked BOOLEAN);");

            Cursor resultSet = myDatabase.rawQuery("Select * from SupermarketList", null);
            resultSet.moveToFirst();

            if (resultSet != null) {
                // Loop through all Results
                do {
                    String item = resultSet.getString(0);

                    Boolean isChecked = Boolean.valueOf(resultSet.getString(1));

                    items.add(new ItemClass(item, isChecked));
                } while (resultSet.moveToNext());
            }
        }
        catch(Exception e){
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
    //endregion

    public class ItemClass{
        public String Item;
        public Boolean IsChecked;

        public ItemClass(String item, boolean isChecked){
            Item = item;
            IsChecked = isChecked;
        }
    }
}