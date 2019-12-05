package com.example.plainitpokerv2user;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class  QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyViewHolder>
{
    private ArrayList<Question> mDataset;
    Context context;
    DatabaseReference dbReference;
    ArrayList<Question> questionList;
    private RecyclerViewClickListener mListener;

    public interface RecyclerViewClickListener
    {
        void onClick(String questionId);
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public QuestionAdapter(Context context,ArrayList myDataset,RecyclerViewClickListener listener)
    {
        this.context = context;
        this.mDataset = myDataset;
        //questionList = myDataset;
        mListener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tv_question;
        public MyViewHolder(View view)
        {
            super(view);
            tv_question =view.findViewById(R.id.tv_question);
        }
    }
    // Create new views (invoked by the layout manager)
    @Override
    public QuestionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view/ infalte item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // set the data in items
        final Question output = mDataset.get(position);
        String str_question= output.getQuestion();
        holder.tv_question.setText(str_question);
        if (output.getState().equals("active"))
        {
            holder.tv_question.setTextColor(Color.BLACK);
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    //start a new fragment with recyclerview for vote
                    mListener.onClick(output.getQuestionId());
                }
            });
        }
        else
        {
            holder.tv_question.setTextColor(Color.GRAY);
            holder.itemView.setClickable(false);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return mDataset.size();
    }
    @Override
    public long getItemId(int position) { return position; }

    @Override
    public int getItemViewType(int position) { return position; }

}