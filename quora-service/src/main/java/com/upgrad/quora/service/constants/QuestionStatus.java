package com.upgrad.quora.service.constants;

public enum QuestionStatus {
    QUESTION_CREATED ("QUESTION CREATED"), QUESTION_EDITED ("QUESTION EDITED"), QUESTION_DELETED ("QUESTION DELETED");
    private String textStatus;
    QuestionStatus(String textStatus){
        this.textStatus = textStatus;
    }
    public String getStatus(){
        return this.textStatus;
    }
}
