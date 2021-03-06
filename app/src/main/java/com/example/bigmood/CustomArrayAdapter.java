package com.example.bigmood;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * This is an array adapter created only for the purpose of demonstration for project part 2
 */
public class CustomArrayAdapter extends ArrayAdapter<Mood> {
    private ArrayList<Mood> moodArrayList;
    private Context context;
    TextView moodTitle, moodDescription, moodDate;
    ConstraintLayout linearLayout;
    public CustomArrayAdapter(ArrayList<Mood> Moods, Context context) {
        super(context, 0,Moods);
        this.moodArrayList = Moods;
        this.context = context;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view =convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_moodfragment, parent, false);
        }
        Mood some_mood =  moodArrayList.get(position);
        moodTitle = view.findViewById(R.id.moodUserName);
        moodDate = view.findViewById(R.id.moodDate);
        moodDescription = view.findViewById(R.id.moodDescription_edit);
        linearLayout = view.findViewById(R.id.linearLayout);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");


        moodTitle.setText(some_mood.getMoodTitle());
        try{
            moodDate.setText(dateFormat.format(some_mood.getMoodDate()));
        }catch (Exception e){
            e.printStackTrace();
        }
        moodDescription.setText(some_mood.getMoodDescription());
        String stringHEX = some_mood.getMoodColor();
        try {
            linearLayout.setBackgroundColor(Color.parseColor(stringHEX));
        }catch (Throwable e){
            e.printStackTrace();
        }






        return view;
    }
}
