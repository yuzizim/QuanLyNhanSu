package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.SerializedName;

public class StatsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Stats data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Stats getData() {
        return data;
    }

    public void setData(Stats data) {
        this.data = data;
    }
}