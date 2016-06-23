package com.qg.memori;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qg.memori.data.QuizzData;

import java.util.List;

/**
 * Created by q on 28/02/2016.
 */
public class ExamResultArrayAdapter extends ArrayAdapter<QuizzData> {
    private final Context context;

    public ExamResultArrayAdapter(Context context, List<QuizzData> values) {
        super(context, -1, values);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final QuizzData q = getItem(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup itemViewGroup = (ViewGroup) inflater.inflate(R.layout.exam_result_list_item, parent, false);

        ((TextView)itemViewGroup.findViewById(R.id.memory_question)).setText(q.memory.question);
        ((TextView)itemViewGroup.findViewById(R.id.memory_answer)).setText(q.memory.answer);
        ((TextView)itemViewGroup.findViewById(R.id.quizz_answer)).setText(q.getAnswer());

        SeekBar sb = (SeekBar) itemViewGroup.findViewById(R.id.seekBar);
        boolean ok = q.getAnswer().equalsIgnoreCase(q.memory.answer);


        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                q.score = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        sb.setProgress(ok ? 10 : 1);
        return itemViewGroup;
    }
}
