package com.example.plainitpokerv2user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class AnswersFragment extends Fragment
{
    private static final String ARG_ADMIN_NAME = "adminName";
    private static final String ARG_GROUP_ID = "groupId";

    private String mAdminName;
    private String mGroupName;
    private ArrayList<Answer> answers;
    DatabaseReference dbReference;
    AnswerAdapter answerAdapter;
    RecyclerView recyclerView;

    public AnswersFragment()
    {
        // Required empty public constructor
    }

    public static AnswersFragment newInstance(String param1, String param2)
    {
        AnswersFragment fragment = new AnswersFragment();
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
        if (getArguments() != null) {
            mAdminName = getArguments().getString(ARG_ADMIN_NAME);
            mGroupName = getArguments().getString(ARG_GROUP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View retView= inflater.inflate(R.layout.fragment_answers, container, false);
        initView(retView);
        return retView;
    }

    private void initView(final View view)
    {
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Answers");
        answers = new ArrayList<>();

        //RECYCLERVIEW
        // get the reference of RecyclerView
        recyclerView = view.findViewById(R.id.rv_answerList);
        //recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        //  call the constructor of RVAdapter to send the reference and data to Adapter
        answerAdapter = new AnswerAdapter(getActivity(),answers);
        recyclerView.setAdapter(answerAdapter);

        //for populate recycler view on start with data from firebase
        dbReference = FirebaseDatabase.getInstance().getReference().child("Answers");

        //DUPLAZIK!!!
        /*
        dbReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                answers.clear();
                Log.d("MYDEBUG","OnDataChange:  answer.clear()");
                for (DataSnapshot answerSpanshot: dataSnapshot.getChildren())
                {
                    Log.d("MYDEBUG","Loop, list size : "+ answers.size());
                    Answer answer = answerSpanshot.getValue(Answer.class);
                    if (answer.getGroupName().equals(mGroupName))
                    {
                        answers.add(answer);
                    }
                }
                Log.d("MYDEBUG","Adapter refreshed, list size : "+answers.size());
                answerAdapter = new AnswerAdapter(getActivity(),answers);
                recyclerView.setAdapter(answerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });*/

        //for update recyclerview on every new answer
        dbReference.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Answer newAnswer = dataSnapshot.getValue(Answer.class);
                if (newAnswer.getGroupName().equals(mGroupName))
                {
                    answers.add(newAnswer);
                    answerAdapter = new AnswerAdapter(getActivity(),answers);
                    recyclerView.setAdapter(answerAdapter);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}