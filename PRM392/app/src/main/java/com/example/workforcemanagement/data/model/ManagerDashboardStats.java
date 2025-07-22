package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ManagerDashboardStats implements Serializable {
    @SerializedName("employee_count_active")
    private int employeeCount;

    @SerializedName("employee_count_total")
    private int employeeTotal;

    @SerializedName("dept_performance")
    private int deptPerformance;

    @SerializedName("tasks_in_progress")
    private int tasksInProgress;

    @SerializedName("tasks_completed_this_week")
    private int tasksCompletedThisWeek;

    public int getEmployeeCount() { return employeeCount; }
    public int getEmployeeTotal() { return employeeTotal; }
    public int getDeptPerformance() { return deptPerformance; }
    public int getTasksInProgress() { return tasksInProgress; }
    public int getTasksCompletedThisWeek() { return tasksCompletedThisWeek; }
}