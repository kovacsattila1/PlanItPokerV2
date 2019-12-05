package com.example.plainitpokerv2user;


public class Answer
{
    String userName;
    String groupName;
    String questionId;
    String answer;
    String answerId;

    public Answer(){}

    public Answer(String answerId,String userName, String groupName, String questionId, String answer)
    {
        this.answerId = answerId;
        this.userName = userName;
        this.groupName = groupName;
        this.questionId = questionId;
        this.answer = answer;
    }

    public String getAnswerId()
    {
        return answerId;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public String getQuestionId()
    {
        return questionId;
    }

    public String getAnswer()
    {
        return answer;
    }
}