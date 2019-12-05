package com.example.planitpokerv2;

import java.util.HashMap;
import java.util.Map;

public class Question
{
    private String questionId;
    private String adminName;
    private String groupName;
    private String question;
    private String state = "inactive";

    public Question(){}

    public Question(String questionId, String adminName, String groupName, String question)
    {
        this.questionId = questionId;
        this.adminName = adminName;
        this.groupName = groupName;
        this.question = question;
    }

    public String getQuestionId(){return questionId;}
    public String getAdminName(){return adminName;}
    public String getGroupName(){return groupName;}
    public String getQuestion(){return question;}
    public String getState(){return state;}


    public void setState(String state){this.state = state;}
    public void setQuestion(String question){this.question = question;}


    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("adminName",adminName);
        result.put("groupName", groupName);
        result.put("question",question);
        result.put("state", state);
        return result;
    }


}
