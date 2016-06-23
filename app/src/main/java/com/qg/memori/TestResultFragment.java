package com.qg.memori;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.qg.memori.data.DataHelper;
import com.qg.memori.data.QuizzData;
import com.qg.memori.data.SQLHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The quizz exam result will be displayed in this fragment.
 * User can correct if he was right or not.
 */
public class TestResultFragment extends Fragment {

    private List<QuizzData> quizzes;

    public TestResultFragment() {
    }

    public static TestResultFragment newInstance(ArrayList<QuizzData> quizzes) {
        TestResultFragment fragment = new TestResultFragment();
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
        Log.d("TestResultFragment", "onCreate: quizzes size=" + quizzes.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TestResultFragment", "onCreateView");
        View view =  inflater.inflate(R.layout.test_result_fragment, container, false);
        ListView listView = (ListView) view.findViewById(R.id.test_result);
        listView.setAdapter(new ExamResultArrayAdapter(getContext(), quizzes));

        //add title
        TextView listHeader = new TextView(getContext());
        listView.addHeaderView(listHeader);
        listHeader.setText("Result of your test");

        view.findViewById(R.id.saveExamResult).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (QuizzData q : quizzes) {
                    try {
                        new SQLHelper(getContext()).obtainDao(QuizzData.class).update(q);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                getActivity().finish();
            }
        });
        return view;
    }
}
