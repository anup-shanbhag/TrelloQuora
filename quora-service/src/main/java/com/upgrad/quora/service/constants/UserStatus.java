package com.upgrad.quora.service.constants;

public enum UserStatus {
    REGISTERED_OK ("USER SUCCESSFULLY REGISTERED"), SIGN_IN_OK ("SIGNED IN SUCCESSFULLY"), SIGN_OUT_OK ("SIGNED OUT SUCCESSFULLY"), DELETED_OK("USER SUCCESSFULLY DELETED");
    private String textStatus;
    UserStatus(String textStatus){
        this.textStatus = textStatus;
    }
    public String getStatus(){
        return this.textStatus;
    }
}
