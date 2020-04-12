package com.upgrad.quora.service.constants;

public enum UserRole {
    ADMIN ("ADMIN"), REGULAR ("NONADMIN");
    private String textRole;
    UserRole(String textRole){
        this.textRole = textRole;
    }
    public String getRole(){
        return this.textRole;
    }
}
