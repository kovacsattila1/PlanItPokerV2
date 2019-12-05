package com.example.plainitpokerv2user;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class VoteFragment extends Fragment
{
    private static final String ARG_USER_NAME = "userName";
    private static final String ARG_GROUP_NAME = "groupName";
    private static final String ARG_QUESTION_ID = "questionId";

    private String mUserName;
    private String mGroupName;
    private String mQuestionId;
    private int pressed_button_id;
    DatabaseReference dbReference;
    String questionToAnswer;
    String answer;
    Answer userAnswer;
    Boolean userVoted;
    String questionState="active";
    String str_userActualAnswer;

    public VoteFragment()
    {
        // Required empty public constructor
    }

    public static VoteFragment newInstance(String param1, String param2,String param3)
    {
        VoteFragment fragment = new VoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_NAME, param1);
        args.putString(ARG_GROUP_NAME, param2);
        args.putString(ARG_QUESTION_ID, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserName = getArguments().getString(ARG_USER_NAME);
            mGroupName = getArguments().getString(ARG_GROUP_NAME);
            mQuestionId = getArguments().getString(ARG_QUESTION_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View retView= inflater.inflate(R.layout.fragment_vote, container, false);
        initView(retView);
        return retView;
    }


    public void initView(final View view)
    {
        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Vote");
        final ArrayList<String> buttonText = new ArrayList<>(Arrays.asList("0","1/2","1","2","3","5","8","13","20","40","100","?","coffee"));
        GridLayout myGridLayout =view.findViewById(R.id.ly_grid);
        final TextView userActualAnswer = view.findViewById(R.id.tv_answer);
        str_userActualAnswer = userActualAnswer.getText().toString();

        //fill grid with buttons
        for (int i=0; i<12; ++i)
        {
            Button myButton = new Button(getContext());

            myButton.setId(i);
            String btn_text = buttonText.get(i);
            myButton.setText(btn_text);
            myGridLayout.addView(myButton);
        }

        //the last button contains an image, has to be managed differently form other buttons
        ImageButton btn_coffe = new ImageButton(getContext());
        btn_coffe.setLayoutParams(new GridLayout.LayoutParams());
        int btn_id=12;
        //it will be placed in the 4th row, and 1st column (5,2)
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(4),GridLayout.spec(1));
        btn_coffe.setLayoutParams(params);
        btn_coffe.setId(btn_id);
        Drawable dr = getResources().getDrawable(R.drawable.coffe);
        Bitmap bitmap = ((BitmapDrawable)dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(bitmap,50,50,true));
        btn_coffe.setImageDrawable(d);
        myGridLayout.addView(btn_coffe);
        //set the question in the voteFor textview
        dbReference = FirebaseDatabase.getInstance().getReference("Question").child(mQuestionId);
        dbReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                questionToAnswer = dataSnapshot.child("question").getValue().toString();
                TextView tv_voteFor = view.findViewById(R.id.tv_voteFor);
                tv_voteFor.setText(questionToAnswer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });


        //whichever button was pressed last, its id will be stored in pressed_button_id
        btn_coffe.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pressed_button_id = 12;
                str_userActualAnswer = buttonText.get(pressed_button_id);
                userActualAnswer.setText("Your answer: "+str_userActualAnswer);
            }
        });


        //set onClickListener for each button
        for(int i = 0; i < 12; ++i)
        {
            final Button button = view.findViewById(i);
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    v.setSelected(true);
                    pressed_button_id=button.getId();
                    str_userActualAnswer = buttonText.get(pressed_button_id);
                    userActualAnswer.setText("Your answer: "+str_userActualAnswer);
                }
            });
        }
        dbReference = FirebaseDatabase.getInstance().getReference().child("Question").child(mQuestionId);
        dbReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                questionState=dataSnapshot.child("state").getValue().toString();
                Log.d("MYDEBUG","Question state onDataChanged: "+questionState);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        //this button will only be clickable if the user has not voted yet
        final Button btn_vote = view.findViewById(R.id.btn_Vote);
        userVoted = false;
        dbReference = FirebaseDatabase.getInstance().getReference("Answers");
        dbReference.addListenerForSingleValueEvent(new ValueEventListener()
        {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                ArrayList<Answer> answers= new ArrayList<>();
                for(DataSnapshot data: dataSnapshot.getChildren())
                {
                    Answer answer = data.getValue(Answer.class);
                    answers.add(answer);
                }
                for (Answer a: answers)
                {
                    if(a.getUserName().equals(mUserName) && a.getQuestionId().equals(mQuestionId))
                    {
                        userVoted = true;
                        break;
                    }
                }
                if (userVoted)
                {
                    btn_vote.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Toast.makeText(getContext(), "You have already voted!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    //if the user has not voted, the button starts the voteFragment
                    btn_vote.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            if (str_userActualAnswer.equals("Your answer: nothing"))
                            {
                                Toast.makeText(getContext(), "You must vote something!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                if (questionState.equals("active"))
                                {
                                    answer = buttonText.get(pressed_button_id);
                                    String msg =mUserName + " " + questionToAnswer + " "+ answer;
                                    Log.i("Adatbazisba: ",msg);

                                    //insert vote into database: loginName, title, vote
                                    addAnswerToDB();

                                    //start the second fragment, which has the list
                                    AnswersFragment answersFragment = new AnswersFragment();
                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.fg_placeholder,answersFragment.newInstance(mUserName,mGroupName));
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();

                                }
                                else
                                {
                                    Toast.makeText(getContext(), "Sorry, can't vote! Inactive question.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }
    public void addAnswerToDB()
    {
        dbReference = FirebaseDatabase.getInstance().getReference("Answers");
        String id = dbReference.push().getKey();
        userAnswer = new Answer(id,mUserName,mGroupName,mQuestionId,answer);
        dbReference.child(id).setValue(userAnswer);
        Toast.makeText(getContext(), "Answer added", Toast.LENGTH_LONG).show();
    }

}
