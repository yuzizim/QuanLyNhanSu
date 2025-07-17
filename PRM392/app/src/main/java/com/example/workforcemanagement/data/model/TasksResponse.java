package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TasksResponse {
    private boolean success;
    private String message;
    private TaskData data;

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

    public TaskData getData() {
        return data;
    }

    public void setData(TaskData data) {
        this.data = data;
    }

    public static class TaskData {
        @SerializedName("tasks")
        private List<Task> tasks;

        private int total;
        private int page;
        private int limit;

        public List<Task> getTasks() {
            return tasks;
        }

        public void setTasks(List<Task> tasks) {
            this.tasks = tasks;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }
    }
}