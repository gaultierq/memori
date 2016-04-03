package com.qg.memori;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.qg.memori.data.QuizzData;

import java.util.Iterator;
import java.util.List;

/**
 * Created by q on 03/04/2016.
 */
public class TestDialogFragment extends DialogFragment {
    private final Iterator<QuizzData> iterator;
    private DialogInterface.OnDismissListener onDismissListener;

    public TestDialogFragment(List<QuizzData> quizzes) {
        iterator = quizzes.iterator();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.test_dialog, null);
        configViewOrDismiss(view);
        view.findViewById(R.id.validate_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configViewOrDismiss(view);
            }
        });
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }


    private void configViewOrDismiss(View view) {
        if (iterator.hasNext()) {
            QuizzData quizz = iterator.next();
            ((TextView) view.findViewById(R.id.question_box)).setText(quizz.memory.question);
            ((EditText) view.findViewById(R.id.answer_box)).setText(null);
        }
        else {
            dismiss();
        }
    }
}
