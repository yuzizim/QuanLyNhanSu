// TaskReport.java (Model for task report)
package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaskReport {
    @SerializedName("status_breakdown")
    private List<StatusBreakdown> statusBreakdown;

    @SerializedName("priority_breakdown")
    private List<PriorityBreakdown> priorityBreakdown;

    @SerializedName("department_summary")
    private List<DepartmentSummary> departmentSummary;

    @SerializedName("assignee_performance")
    private List<AssigneePerformance> assigneePerformance;

    @SerializedName("overdue_tasks")
    private List<OverdueTask> overdueTasks;

    @SerializedName("time_metrics")
    private TimeMetrics timeMetrics;

    @SerializedName("creator_distribution")
    private List<CreatorDistribution> creatorDistribution;

    // Getters and Setters
    public List<StatusBreakdown> getStatusBreakdown() { return statusBreakdown; }
    public void setStatusBreakdown(List<StatusBreakdown> statusBreakdown) { this.statusBreakdown = statusBreakdown; }
    public List<PriorityBreakdown> getPriorityBreakdown() { return priorityBreakdown; }
    public void setPriorityBreakdown(List<PriorityBreakdown> priorityBreakdown) { this.priorityBreakdown = priorityBreakdown; }
    public List<DepartmentSummary> getDepartmentSummary() { return departmentSummary; }
    public void setDepartmentSummary(List<DepartmentSummary> departmentSummary) { this.departmentSummary = departmentSummary; }
    public List<AssigneePerformance> getAssigneePerformance() { return assigneePerformance; }
    public void setAssigneePerformance(List<AssigneePerformance> assigneePerformance) { this.assigneePerformance = assigneePerformance; }
    public List<OverdueTask> getOverdueTasks() { return overdueTasks; }
    public void setOverdueTasks(List<OverdueTask> overdueTasks) { this.overdueTasks = overdueTasks; }
    public TimeMetrics getTimeMetrics() { return timeMetrics; }
    public void setTimeMetrics(TimeMetrics timeMetrics) { this.timeMetrics = timeMetrics; }
    public List<CreatorDistribution> getCreatorDistribution() { return creatorDistribution; }
    public void setCreatorDistribution(List<CreatorDistribution> creatorDistribution) { this.creatorDistribution = creatorDistribution; }

    public static class StatusBreakdown {
        @SerializedName("status")
        private String status;
        @SerializedName("task_count")
        private int taskCount;
        @SerializedName("avg_progress")
        private double avgProgress;
        @SerializedName("overdue_tasks")
        private int overdueTasks;

        public String getStatus() { return status; }
        public int getTaskCount() { return taskCount; }
        public double getAvgProgress() { return avgProgress; }
        public int getOverdueTasks() { return overdueTasks; }
    }

    public static class PriorityBreakdown {
        @SerializedName("priority")
        private String priority;
        @SerializedName("task_count")
        private int taskCount;
        @SerializedName("overdue_tasks")
        private int overdueTasks;

        public String getPriority() { return priority; }
        public int getTaskCount() { return taskCount; }
        public int getOverdueTasks() { return overdueTasks; }
    }

    public static class DepartmentSummary {
        @SerializedName("department_name")
        private String departmentName;
        @SerializedName("department_id")
        private Integer departmentId;
        @SerializedName("task_count")
        private int taskCount;
        @SerializedName("avg_progress")
        private double avgProgress;
        @SerializedName("overdue_tasks")
        private int overdueTasks;

        public String getDepartmentName() { return departmentName; }
        public Integer getDepartmentId() { return departmentId; }
        public int getTaskCount() { return taskCount; }
        public double getAvgProgress() { return avgProgress; }
        public int getOverdueTasks() { return overdueTasks; }
    }

    public static class AssigneePerformance {
        @SerializedName("employee_id")
        private int employeeId;
        @SerializedName("employee_name")
        private String employeeName;
        @SerializedName("total_tasks")
        private int totalTasks;
        @SerializedName("completed_tasks")
        private int completedTasks;
        @SerializedName("avg_progress")
        private double avgProgress;
        @SerializedName("overdue_tasks")
        private int overdueTasks;

        public int getEmployeeId() { return employeeId; }
        public String getEmployeeName() { return employeeName; }
        public int getTotalTasks() { return totalTasks; }
        public int getCompletedTasks() { return completedTasks; }
        public double getAvgProgress() { return avgProgress; }
        public int getOverdueTasks() { return overdueTasks; }
    }

    public static class OverdueTask {
        @SerializedName("id")
        private int id;
        @SerializedName("title")
        private String title;
        @SerializedName("deadline")
        private String deadline;
        @SerializedName("assignee_name")
        private String assigneeName;
        @SerializedName("department_name")
        private String departmentName;

        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getDeadline() { return deadline; }
        public String getAssigneeName() { return assigneeName; }
        public String getDepartmentName() { return departmentName; }
    }

    public static class TimeMetrics {
        @SerializedName("tasks_created_last_30_days")
        private int tasksCreatedLast30Days;
        @SerializedName("tasks_completed_last_30_days")
        private int tasksCompletedLast30Days;
        @SerializedName("avg_completion_hours")
        private Double avgCompletionHours;

        public int getTasksCreatedLast30Days() { return tasksCreatedLast30Days; }
        public int getTasksCompletedLast30Days() { return tasksCompletedLast30Days; }
        public Double getAvgCompletionHours() { return avgCompletionHours; }
    }

    public static class CreatorDistribution {
        @SerializedName("employee_id")
        private int employeeId;
        @SerializedName("creator_name")
        private String creatorName;
        @SerializedName("tasks_created")
        private int tasksCreated;

        public int getEmployeeId() { return employeeId; }
        public String getCreatorName() { return creatorName; }
        public int getTasksCreated() { return tasksCreated; }
    }
}