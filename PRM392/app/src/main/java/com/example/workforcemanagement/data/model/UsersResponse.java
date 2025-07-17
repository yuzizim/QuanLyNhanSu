package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UsersResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        @SerializedName("users")
        private List<User> users;

        @SerializedName("stats")
        private UserStats stats;

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

        public UserStats getStats() {
            return stats;
        }

        public void setStats(UserStats stats) {
            this.stats = stats;
        }
    }
}