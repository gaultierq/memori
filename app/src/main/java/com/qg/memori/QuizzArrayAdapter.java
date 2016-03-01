package com.qg.memori;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qg.memori.data.Quizz;

import java.util.List;

/**
 * Created by q on 28/02/2016.
 */
public class QuizzArrayAdapter extends ArrayAdapter<Quizz> {
    private final Context context;

    public QuizzArrayAdapter(Context context, List<Quizz> values) {
        super(context, -1, values);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textView = (TextView) inflater.inflate(R.layout.recollection_in_list, parent, false);
        Quizz item = getItem(position);
        textView.setText("memory id: " + item.memoryId);
        return textView;
    }
}
