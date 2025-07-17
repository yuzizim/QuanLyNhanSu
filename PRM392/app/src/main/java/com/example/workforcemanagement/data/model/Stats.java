package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Stats implements Serializable {
    @SerializedName("totalEmployees")
    private int totalEmployees;

    @SerializedName("departmentCount")
    private int departmentCount;

    @SerializedName("activeJobs")
    private int activeJobs;

    @SerializedName("avgPerformance")
    private String avgPerformance;

    @SerializedName("attendanceRate")
    private String attendanceRate;

    @SerializedName("taskCompletionRate")
    private String taskCompletionRate;

    // Getters and Setters
    public int getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public int getDepartmentCount() {
        return departmentCount;
    }

    public void setDepartmentCount(int departmentCount) {
        this.departmentCount = departmentCount;
    }

    public int getActiveJobs() {
        return activeJobs;
    }

    public void setActiveJobs(int activeJobs) {
        this.activeJobs = activeJobs;
    }

    public String getAvgPerformance() {
        return avgPerformance;
    }

    public void setAvgPerformance(String avgPerformance) {
        this.avgPerformance = avgPerformance;
    }

    public String getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(String attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    public String getTaskCompletionRate() {
        return taskCompletionRate;
    }

    public void setTaskCompletionRate(String taskCompletionRate) {
        this.taskCompletionRate = taskCompletionRate;
    }
}