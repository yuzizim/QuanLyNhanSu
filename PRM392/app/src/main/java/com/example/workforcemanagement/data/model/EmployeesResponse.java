package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.SerializedName;

public class EmployeesResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private EmployeesData data;

    @SerializedName("message")
    private String message;

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public EmployeesData getData() { return data; }
    public void setData(EmployeesData data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}