package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.SerializedName;

public class UserStats {
    @SerializedName("total_users")
    private int totalUsers;

    @SerializedName("active_users")
    private int activeUsers;

    @SerializedName("inactive_users")
    private int inactiveUsers;

    @SerializedName("new_users")
    private int newUsers;

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }

    public int getInactiveUsers() {
        return inactiveUsers;
    }

    public void setInactiveUsers(int inactiveUsers) {
        this.inactiveUsers = inactiveUsers;
    }

    public int getNewUsers() {
        return newUsers;
    }

    public void setNewUsers(int newUsers) {
        this.newUsers = newUsers;
    }
}