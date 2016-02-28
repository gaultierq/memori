package com.qg.memori;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by q on 28/02/2016.
 */
public class MemoriesArrayAdapter extends ArrayAdapter<Memory> {
    private final Context context;

    public MemoriesArrayAdapter(Context context, List<Memory> values) {
        super(context, -1, values);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textView = (TextView) inflater.inflate(R.layout.recollection_in_list, parent, false);
        Memory item = getItem(position);
        textView.setText(item.question);
        return textView;
    }
}
