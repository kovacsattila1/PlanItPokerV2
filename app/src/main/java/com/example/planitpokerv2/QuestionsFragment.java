package com.example.planitpokerv2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class QuestionsFragment extends Fragment
{
    private static final String ARG_ADMIN_NAME = "adminName";
    private static final String ARG_GROUP_ID = "groupId";


    private String mAdminName;
    private String mGroupName;

    DatabaseReference dbReference;
    ArrayList<Question> questions;
    RVAdapter rvAdapter;
    RecyclerView recyclerView;

    public QuestionsFragment()
    {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static QuestionsFragment newInstance(String param1, String param2)
    {
        QuestionsFragment fragment = new QuestionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ADMIN_NAME, param1);
        args.putString(ARG_GROUP_ID, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mAdminName = getArguments().getString(ARG_ADMIN_NAME);
            mGroupName= getArguments().getString(ARG_GROUP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View retView = inflater.inflate(R.layout.fragment_questions, container, false);
        initView(retView);
        return retView;
    }

    private void initView(final View view)
    {
        questions = new ArrayList<>();

        recyclerView = view.findViewById(R.id.rv_questionList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        final RVAdapter.RecyclerViewClickListener listener = new RVAdapter.RecyclerViewClickListener()
        {
            @Override
            public void onClick(String questionId)
            {
                AnswersFragment answersFragment = new AnswersFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fg_placeholder, answersFragment.newInstance(mAdminName, mGroupName));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        };

        rvAdapter = new RVAdapter(getActivity(), questions, listener);
        recyclerView.setAdapter(rvAdapter);

        dbReference = FirebaseDatabase.getInstance().getReference("Question");

        final Button addQuestion = view.findViewById(R.id.btn_addQuestion);
        addQuestion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDialog();
            }
        });

        dbReference.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Question newQuesetion=dataSnapshot.getValue(Question.class);
                //display question only if it belongs to this group
                if (newQuesetion.getGroupName().equals(mGroupName))
                {
                    questions.add(newQuesetion);
                    rvAdapter = new RVAdapter(getActivity(),questions,listener);
                    //RecyclerView recyclerView = view.findViewById(R.id.rv_questionList);
                    //recyclerView.setHasFixedSize(true);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(rvAdapter);
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(getContext(), "Some error ocurred", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void showDialog()
    {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

        Fragment prev = getFragmentManager().findFragmentByTag("dialog");

        if(prev != null)
        {
            fragmentTransaction.remove(prev);
        }

        fragmentTransaction.addToBackStack(null);

        AddQuestionFragment addQuestionFragment = AddQuestionFragment.newInstance(mAdminName, mGroupName);

        addQuestionFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        addQuestionFragment.show(fragmentTransaction,"dialog");
    }
}
