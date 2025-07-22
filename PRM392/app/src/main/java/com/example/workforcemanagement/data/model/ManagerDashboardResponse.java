package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ManagerDashboardResponse implements Serializable {
    @SerializedName("success")
    private boolean success;

    @SerializedName("user")
    private User user;

    @SerializedName("stats")
    private ManagerDashboardStats stats;

    public boolean isSuccess() { return success; }
    public User getUser() { return user; }
    public ManagerDashboardStats getStats() { return stats; }
}