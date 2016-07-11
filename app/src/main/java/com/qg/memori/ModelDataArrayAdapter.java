package com.qg.memori;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qg.memori.data.DataHelper;
import com.qg.memori.data.ModelData;

import java.util.List;

/**
 * Created by q on 28/02/2016.
 */
public class ModelDataArrayAdapter<T extends ModelData> extends ArrayAdapter<T> {
    private final Context context;

    public ModelDataArrayAdapter(Context context, List<T> values) {
        super(context, -1, values);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textView = (TextView) inflater.inflate(R.layout.list_text, parent, false);
        textView.setText(DataHelper.toString(getItem(position)));

        return textView;
    }
}
