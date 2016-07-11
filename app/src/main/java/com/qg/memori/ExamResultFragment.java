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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.logger.Logger;
import com.qg.memori.alarm.NotificationManager;
import com.qg.memori.data.DataHelper;
import com.qg.memori.data.DbHelper;
import com.qg.memori.data.QuizzData;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


        //save the results
        view.findViewById(R.id.saveExamResult).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                Map<String, Object> updates = new HashMap<>();
                for (QuizzData q : quizzes) {
                    Assert.assertNotNull(q.score);
                    String key = db.child(DbHelper.NODE_OLD_QUIZZ_BY_MEMORY_UID).child(q.memoryId).push().getKey();
                    updates.put("/" + DbHelper.NODE_MEMORY_BY_USER_UID + "/" + user.getUid() + "/" + q.memoryId + "/pendingQuizz", null);
                    updates.put("/" + DbHelper.NODE_OLD_QUIZZ_BY_MEMORY_UID + "/" + q.memoryId + "/" + key, q.toMap());
                }

                Logger.i("Updating exam results. %s", updates);

                db.updateChildren(updates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Logger.e("Someting went wrong wile updating all children.", databaseError);
                        }
                        else {
                            NotificationManager.refreshNotification(getContext());
                            Toast.makeText(getContext(), "Exam result saved", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }
                    }
                });


            }
        });
        return view;
    }
}
