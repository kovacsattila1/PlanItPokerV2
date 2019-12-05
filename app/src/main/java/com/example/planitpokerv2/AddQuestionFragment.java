package com.example.planitpokerv2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class AddQuestionFragment extends DialogFragment
{
    EditText et_question;
    Button btn_addQuestion, btn_cancel;
    Question newQuestion;
    DatabaseReference dbReference;

    private static final String ARG_ADMIN_NAME = "adminName";
    private static final String ARG_GROUP_ID = "groupId";

    private String mAdminName;
    private String mGroupName;


    public AddQuestionFragment()
    {
        // Required empty public constructor
    }

    public static AddQuestionFragment newInstance(String param1, String param2)
    {
        AddQuestionFragment fragment = new AddQuestionFragment();
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
            mGroupName = getArguments().getString(ARG_GROUP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_question, container, false);
    }

    //todo
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        getDialog().setTitle("New question");
        et_question = view.findViewById(R.id.et_question);
        btn_addQuestion = view.findViewById(R.id.btn_addQuestion);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        dbReference = FirebaseDatabase.getInstance().getReference("Question");

        //addquestion button saves data to firebase, then returns to previous fragment
        btn_addQuestion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addQuestionToDB();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getDialog().dismiss();
            }
        });
    }

    public void addQuestionToDB()
    {
        //get question from edittext
        String str_question = et_question.getText().toString();

        if (TextUtils.isEmpty(str_question))
        {
            et_question.setError("Quesetion required or Cancel");
            return;
        }
        else
        {
            String id = dbReference.push().getKey();
            newQuestion = new Question(id, mAdminName, mGroupName, str_question);
            //push into db
            dbReference.child(id).setValue(newQuestion);
            Toast.makeText(getContext(), "Question added", Toast.LENGTH_LONG).show();
            //remove dialog
            dismiss();
        }
    }
}
