package com.example.planitpokerv2;

public class Answer
{
    String userName;
    String groupName;
    String questionId;
    String answer;

    public Answer(){}

    public Answer(String userName, String groupName, String questionId, String answer)
    {
        this.userName = userName;
        this.groupName = groupName;
        this.questionId = questionId;
        this.answer = answer;
    }


    public String getUserName(){return userName;}
    public String getGroupName(){return groupName;}
    public String getQuestionId(){return questionId;}
    public String getAnswer(){return answer;}

}
