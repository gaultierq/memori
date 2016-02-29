package com.qg.memori;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qg.memori.data.Memory;

/**
 * Created by q on 29/02/2016.
 */
public class MemoryConfigFragment extends Fragment {

    private Memory memory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.memory_config_fragment, container, false);
        TextView memQuestion = (TextView) res.findViewById(R.id.memory_question);
        res.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemoriAppActivity maa = (MemoriAppActivity) v.getContext();
                maa.obtainSqlHelper().deleteByPK(memory);
                maa.getFragmentManager().beginTransaction().remove(MemoryConfigFragment.this).commit();
                maa.adapter.remove(memory);
                maa.refresh();
            }
        });
        memQuestion.setText(memory.question);
        return res;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }
}
