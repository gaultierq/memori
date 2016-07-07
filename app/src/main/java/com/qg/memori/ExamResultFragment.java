package com.qg.memori;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.qg.memori.alarm.NotificationManager;
import com.qg.memori.data.DataHelper;
import com.qg.memori.data.QuizzData;
import com.qg.memori.data.SQLHelper;

import junit.framework.Assert;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The quizz exam result will be displayed in this fragment.
 * User can correct if he was right or not.
 */
public class ExamResultFragment extends Fragment {

    public static final int LAYOUT = R.layout.exam_result_fragment;
    //for layout of list elements see ExamResultArrayAdapter

    private List<QuizzData> quizzes;

    public ExamResultFragment() {
    }

    public static ExamResultFragment newInstance(ArrayList<QuizzData> quizzes) {
        ExamResultFragment fragment = new ExamResultFragment();
        Bundle args = new Bundle();
        DataHelper.putListInBundle(args, quizzes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            quizzes = DataHelper.readDataList(getArguments(), QuizzData.class);
        }
        Log.d("ExamResultFragment", "onCreate: quizzes size=" + quizzes.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ExamResultFragment", "onCreateView");
        View view =  inflater.inflate(LAYOUT, container, false);
        ListView listView = (ListView) view.findViewById(R.id.test_result);
        listView.setAdapter(new ExamResultArrayAdapter(getContext(), quizzes));

        //add title
        TextView listHeader = new TextView(getContext());
        listView.addHeaderView(listHeader);
        listHeader.setText("Result of your test");

        view.findViewById(R.id.saveExamResult).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dao<QuizzData, Long> dao = new SQLHelper(getContext()).obtainDao(QuizzData.class);
                for (QuizzData q : quizzes) {
                    Assert.assertNotNull(q.score);
                    try {
                        dao.update(q);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                NotificationManager.refreshNotification(getContext());
                Toast.makeText(getContext(), "Exam result saved", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        });
        return view;
    }
}
