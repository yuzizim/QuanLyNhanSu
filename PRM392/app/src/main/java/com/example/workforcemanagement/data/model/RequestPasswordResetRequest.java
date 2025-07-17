package com.example.workforcemanagement.data.model;

public class RequestPasswordResetRequest {
    private String email;

    public RequestPasswordResetRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
