package com.example.plainitpokerv2user;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class StartFragment extends Fragment
{


    DatabaseReference dbReference;
    boolean inputError;

    public StartFragment()
    {
        // Required empty public constructor
    }

    public static StartFragment newInstance(String param1, String param2)
    {
        StartFragment fragment = new StartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //get parameter sent from activity
        if (getArguments() != null)
        {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View retView =inflater.inflate(R.layout.fragment_start, container, false);
        final EditText et_userName = retView.findViewById(R.id.et_userName);
        final EditText et_groupName = retView.findViewById(R.id.et_groupName);
        Button btn_joinGroup = retView.findViewById(R.id.btn_joinGroup);

        //if Join Group button is pushed
        btn_joinGroup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //send the two input string to the next fragment
                final String str_userName = et_userName.getText().toString();
                final String str_groupName = et_groupName.getText().toString();
                inputError = false;
                if (TextUtils.isEmpty(str_userName))
                {
                    et_userName.setError("Username required");
                    inputError=true;
                }
                if (TextUtils.isEmpty(str_groupName))
                {
                    et_groupName.setError("Groupname required");
                    inputError=true;
                }
                dbReference = FirebaseDatabase.getInstance().getReference("Question");
                dbReference.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    boolean groupExists=false;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        for(DataSnapshot data: dataSnapshot.getChildren())
                        {
                            Question question = data.getValue(Question.class);
                            if(question.getGroupName().equals(str_groupName) && !(TextUtils.isEmpty(str_userName)))
                            {
                                groupExists=true;
                                break;
                            }

                        }
                        if (!groupExists)
                        {
                            et_groupName.setError("Group doesn't exist!");
                            inputError=true;
                        }
                        if (!inputError)
                        {
                            QuestionsFragment questionsFragment = new QuestionsFragment();
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fg_placeholder,questionsFragment.newInstance(str_userName,str_groupName));
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

            }
        });

        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Log in");
        return retView;
    }
}
