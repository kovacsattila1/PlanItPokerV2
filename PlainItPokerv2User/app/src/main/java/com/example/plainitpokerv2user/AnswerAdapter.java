package com.example.plainitpokerv2user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.MyViewHolder>
{
    ArrayList<Answer> mDataset;
    Context context;
    DatabaseReference dbReference;
    String questionToAnswer;

    public AnswerAdapter(Context context, ArrayList myDataset)
    {
        this.context = context;
        this.mDataset = myDataset;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv_userName;
        TextView tv_question;
        TextView tv_answer;
        public MyViewHolder(View view)
        {
            super(view);
            tv_userName = view.findViewById(R.id.tv_userName);
            tv_question = view.findViewById(R.id.tv_question);
            tv_answer = view.findViewById(R.id.tv_answer);
        }
    }
    @Override
    public AnswerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        // set the data in items
        Answer output = mDataset.get(position);
        // implement setOnClickListener event on item view.
        holder.tv_userName.setText(output.getUserName());
        final String questionId = output.getQuestionId();
        //get question from database with questionId
        dbReference = FirebaseDatabase.getInstance().getReference("Question").child(questionId);
        dbReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Question question = dataSnapshot.getValue(Question.class);
                questionToAnswer = question.getQuestion();
                holder.tv_question.setText(questionToAnswer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        holder.tv_answer.setText(output.getAnswer());
    }

    @Override
    public int getItemCount()
    {
        return mDataset.size();
    }
}