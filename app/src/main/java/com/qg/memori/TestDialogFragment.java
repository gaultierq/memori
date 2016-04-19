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

import com.qg.memori.data.DataHelper;
import com.qg.memori.data.QuizzData;
import com.qg.memori.data.SQLHelper;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by q on 03/04/2016.
 */
public class TestDialogFragment extends DialogFragment {
    private List<QuizzData> quizzes;
    private int i = -1;

    private DialogInterface.OnDismissListener onDismissListener;

    public TestDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.test_dialog, null);
        quizzes = DataHelper.readDataList(getArguments(), QuizzData.class);
        configNextViewOrDismiss(view);
        view.findViewById(R.id.validate_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuizzData q = quizzes.get(i);
                boolean ok = ((EditText) view.findViewById(R.id.answer_box)).getText().toString().equalsIgnoreCase(q.memory.answer);
                q.score = ok ? 10 : 1;
                try {
                    new SQLHelper(getContext()).getQuizzDao().update(q);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                configNextViewOrDismiss(view);
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


    private void configNextViewOrDismiss(View view) {
        if (++i < getQuizzes().size()) {
            QuizzData quizz = getQuizzes().get(i);
            ((TextView) view.findViewById(R.id.question_box)).setText(quizz.memory.question);
            ((EditText) view.findViewById(R.id.answer_box)).setText(null);
        }
        else {
            dismiss();
        }
    }

    public List<QuizzData> getQuizzes() {
        return quizzes;
    }
}
