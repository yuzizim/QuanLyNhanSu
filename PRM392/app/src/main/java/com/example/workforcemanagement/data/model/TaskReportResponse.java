package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaskReportResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("report")
    private TaskReport report;

    public boolean isSuccess() { return success; }
    public TaskReport getReport() { return report; }
}