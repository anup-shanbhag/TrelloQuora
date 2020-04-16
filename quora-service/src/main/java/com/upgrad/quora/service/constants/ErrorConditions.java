package com.upgrad.quora.service.constants;

public enum ErrorConditions {
    //userSignup:
    USERNAME_ALREADY_EXISTS("SGR-001","Try any other Username, this Username has already been taken"),
    EMAIL_ALREADY_EXISTS("SGR-002","This user has already been registered, try with any other emailId"),

    //userSignin
    USERNAME_NOT_FOUND("ATH-001","This username does not exist"),
    USER_WRONG_PASSWORD("ATH-002","Password failed"),

    //userSignout
    USER_SIGNOUT_ERROR("SGR-001","User is not Signed in"),

    //userProfile
    USER_NOT_SIGNED_IN("ATHR-001","User has not signed in"),
    USER_GET_AUTH_FAILURE("ATHR-002","User is signed out.Sign in first to get user details"),
    USER_NOT_FOUND("USR-001","User with entered uuid does not exist"),

    //userDelete
    USER_SIGNED_OUT("ATHR-002","User is signed out"),
    USER_DELETE_UNAUTHORIZED("ATHR-003","Unauthorized Access, Entered user is not an admin"),
    USER_DELETE_FAILURE("USR-001","User with entered uuid to be deleted does not exist"),

    //createQuestion
    QUES_CREATE_AUTH_FAILURE("ATHR-002","User is signed out.Sign in first to post a question"),

    //getAllQuestions
    QUES_GET_ALL_AUTH_FAILURE("ATHR-002","User is signed out.Sign in first to get all questions"),

    //editQuestionContent
    QUES_EDIT_AUTH_FAILURE("ATHR-002","User is signed out.Sign in first to edit the question"),
    QUES_EDIT_UNAUTHORIZED("ATHR-003","Only the question owner can edit the question"),
    QUES_NOT_FOUND("QUES-001","Entered question uuid does not exist"),

    //deleteQuestion
    QUES_DELETE_AUTH_FAILURE("ATHR-002","User is signed out.Sign in first to delete a question"),
    QUES_DELETE_UNAUTHORIZED("ATHR-003","Only the question owner or admin can delete the question"),

    //getAllQuestionsByUser
    QUES_GET_AUTH_FAILURE("ATHR-002","User is signed out.Sign in first to get all questions posted by a specific user"),
    QUES_GET_FAILURE("USR-001","User with entered uuid whose question details are to be seen does not exist"),

    //createAnswer
    ANS_CREATE_FAILURE("QUES-001","The question entered is invalid"),
    ANS_CREATE_AUTH_FAILURE("ATHR-002","User is signed out.Sign in first to post an answer"),

    //editAnswerContent
    ANS_EDIT_AUTH_FAILURE("ATHR-002","User is signed out.Sign in first to edit an answer"),
    ANS_EDIT_UNAUTHORIZED("ATHR-003","Only the answer owner can edit the answer"),
    ANS_NOT_FOUND("ANS-001","Entered answer uuid does not exist"),

    //deleteAnswer
    ANS_DELETE_AUTH_FAILURE("ATHR-002","User is signed out.Sign in first to delete an answer"),
    ANS_DELETE_UNAUTHORIZED("ATHR-003","Only the answer owner or admin can delete the answer"),

    //getAllAnswersToQuestion
    ANS_GET_AUTH_FAILURE("ATHR-002","User is signed out.Sign in first to get the answers"),
    ANS_GET_FAILURE("QUES-001","The question with entered uuid whose details are to be seen does not exist");

    private String errorCode;
    private String errorMessage;

    ErrorConditions(String errorCode, String errorMessage){
        this.errorCode=errorCode;
        this.errorMessage=errorMessage;
    }

    public String getCode() {
        return errorCode;
    }

    public String getMessage() {
        return errorMessage;
    }
}
