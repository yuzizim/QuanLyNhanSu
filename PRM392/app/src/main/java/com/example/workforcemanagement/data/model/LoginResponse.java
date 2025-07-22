package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private User data;

    @SerializedName("stats")
    private ManagerDashboardStats stats;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public User getData() { return data; }
    public void setData(User data) { this.data = data; }
    public ManagerDashboardStats getStats() { return stats; }
    public void setStats(ManagerDashboardStats stats) { this.stats = stats; }
}