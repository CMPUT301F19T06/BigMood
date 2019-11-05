package com.example.bigmood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.*;

//TODO: extend something...
public class moodFragment {

    private ArrayList<moodObject> moods;
    private Context context;

    public moodFragment(Context context, ArrayList<moodObject> moods){
        //super(context, moods);
        this.moods = moods;
        this.context = context;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_moodfragment, parent, false);
        }

        moodObject mood = moods.get(position);

        TextView rMood = view.findViewById(R.id.name);
        TextView rPlaceholder1 = view.findViewById(R.id.placeholder1);
        TextView rPlaceholder2 = view.findViewById(R.id.placeholder2);

        //rMood.setText(mood.getMood());

        return view;
    }
}
