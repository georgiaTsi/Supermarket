package com.example.supermarket;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.ArrayList;
import java.util.Locale;

public class FirstFragment extends Fragment {

    SQLiteDatabase myDatabase;

    Adapter adapter;

    final Integer RecordAudioRequestCode = 1;
    SpeechRecognizer speechRecognizer;

    Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        button = view.findViewById(R.id.button_first);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_main);

        initRecyclerView(recyclerView);

        initSpeechRecognizer();

        return view;
    }

    private void initSpeechRecognizer(){
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.getActivity());

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }

            @Override
            public void onBeginningOfSpeech() {
//                editText.setText("");
//                editText.setHint("Listening...");
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
//                micButton.setImageResource(R.drawable.ic_mic_black_off);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String result = data.get(0);
                button.setText("Speech recognition");

                if(result.contains("πρόσθεσε") || result.contains("add") || result.contains("and") || result.contains("αν")){
                    String itemName = (result.split(" "))[1];//result.replace("πρόσθεσε ", "");
                    addItem(itemName);
                }
                else if(result.contains("delete") || result.contains("αφαίρεσε") || result.contains("αφαίρεση")){
                    String itemName = (result.split(" "))[1];//result.replace("delete ", "");
                    updateRow(itemName, true);
                    updateRecyclerView();
                }
                else
                    button.setText(result);
            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRecording) {
                    speechRecognizer.startListening(speechRecognizerIntent);
                    button.setText("Recording...");
                }
                else {
                    speechRecognizer.stopListening();
                }

                isRecording = !isRecording;
            }
        });
    }

    boolean isRecording = false;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//            }
//        });
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

    public class ItemClass{
        public String Item;
        public Boolean IsChecked;

        public ItemClass(String item, boolean isChecked){
            Item = item;
            IsChecked = isChecked;
        }
    }
}