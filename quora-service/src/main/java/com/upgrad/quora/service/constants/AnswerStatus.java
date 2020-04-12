package com.upgrad.quora.service.constants;

public enum AnswerStatus {
    ANSWER_CREATED ("ANSWER CREATED"), ANSWER_EDITED ("ANSWER EDITED"), ANSWER_DELETED ("ANSWER DELETED");
    private String textStatus;
    AnswerStatus(String textStatus){
        this.textStatus = textStatus;
    }
    public String getStatus(){
        return this.textStatus;
    }
}
