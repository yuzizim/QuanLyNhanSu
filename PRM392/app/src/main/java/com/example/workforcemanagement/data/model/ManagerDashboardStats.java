package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ManagerDashboardStats implements Serializable {
    private int employeeCount;
    private int employeeTotal;
    private int deptPerformance;
    private int tasksInProgress;
    private int tasksCompletedThisWeek;

    public ManagerDashboardStats(int employeeCount, int employeeTotal, int deptPerformance, int tasksInProgress, int tasksCompletedThisWeek) {
        this.employeeCount = employeeCount;
        this.employeeTotal = employeeTotal;
        this.deptPerformance = deptPerformance;
        this.tasksInProgress = tasksInProgress;
        this.tasksCompletedThisWeek = tasksCompletedThisWeek;
    }

    public int getEmployeeCount() { return employeeCount; }
    public int getEmployeeTotal() { return employeeTotal; }
    public int getDeptPerformance() { return deptPerformance; }
    public int getTasksInProgress() { return tasksInProgress; }
    public int getTasksCompletedThisWeek() { return tasksCompletedThisWeek; }
}
