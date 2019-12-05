package com.example.planitpokerv2;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder>
{
    private ArrayList<Question> mDataset;
    Context context;
    DatabaseReference dbReference;
    ArrayList<Question> questionList;
    int nrOfActiveQuestions;
    private RecyclerViewClickListener mListener;

    public interface RecyclerViewClickListener
    {
        void onClick(String questionId);
    }

    public RVAdapter(Context context, ArrayList myDataset, RecyclerViewClickListener listener)
    {
        this.context = context;
        this.mDataset = myDataset;
        questionList = myDataset;
        mListener = listener;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tv_question;
        public Switch switch_activation;

        public MyViewHolder(View view)
        {
            super(view);
            tv_question = view.findViewById(R.id.tv_question);
            switch_activation = view.findViewById(R.id.switch_activation);
        }
    }

    @Override
    public RVAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        final Question output = mDataset.get(position);
        String str_question = output.getQuestion();
        holder.tv_question.setText(str_question);

        if(output.getState().equals("active"))
        {
            holder.switch_activation.setChecked(true);
        }
        else
        {
            holder.switch_activation.setChecked(false);
        }


        holder.switch_activation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    updateQuestion(output.getQuestionId(), output.getAdminName(), output.getGroupName(), output.getQuestion(),"active");
                    checkActiveQuestions(output, holder.switch_activation);
                }
                else
                {
                    updateQuestion(output.getQuestionId(), output.getAdminName(), output.getGroupName(), output.getQuestion(),"inactive");
                    Toast.makeText(context, "Question is inactive", Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String questionId = output.getQuestionId();
                mListener.onClick(output.getQuestionId());
            }
        });




    }

    @Override
    public int getItemCount()
    {
        return mDataset.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    public boolean updateQuestion(String questionId, String adminName, String groupName, String question, String state)
    {
        dbReference = FirebaseDatabase.getInstance().getReference("Question").child(questionId);
        Question newQuestion = new Question(questionId, adminName, groupName, question);
        newQuestion.setState(state);
        dbReference.setValue(newQuestion);
        return true;
    }



    public int checkActiveQuestions(final Question actualQuestion, final Switch swtich_activation)
    {
        questionList = new ArrayList<>();
        dbReference = FirebaseDatabase.getInstance().getReference("Question");
        dbReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                questionList.clear();

                for(DataSnapshot questionSanpshot: dataSnapshot.getChildren())
                {
                    Question question = questionSanpshot.getValue(Question.class);
                    questionList.add(question);
                    Log.d("DEBUG","Tomb merete: " + Integer.toString(questionList.size()));
                }

                nrOfActiveQuestions = 0;

                for(Question q:questionList)
                {
                    if(q.getState().equals("active"))
                    {
                        ++nrOfActiveQuestions;
                    }
                }

                if(nrOfActiveQuestions == 2)
                {
                    Toast.makeText(context, "Only one question can be active", Toast.LENGTH_SHORT).show();
                    swtich_activation.setChecked(false);
                    updateQuestion(actualQuestion.getQuestionId(), actualQuestion.getAdminName(), actualQuestion.getGroupName(), actualQuestion.getQuestion(),"inactive");
                }
                else
                {
                    Toast.makeText(context, "Question is active", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        return nrOfActiveQuestions;
    }

}
