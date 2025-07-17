package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Task implements Serializable {
    @SerializedName("id")
    private Integer id; // Changed to Integer

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("deadline")
    private String deadline;

    @SerializedName("assignee_id")
    private Integer assigneeId;

    @SerializedName("creator_id")
    private Integer creatorId; // Changed to Integer

    @SerializedName("priority")
    private String priority;

    @SerializedName("status")
    private String status;

    @SerializedName("progress")
    private int progress;

    @SerializedName("estimated_hours")
    private Float estimatedHours;

    @SerializedName("assignee_first_name")
    private String assigneeFirstName;

    @SerializedName("assignee_last_name")
    private String assigneeLastName;

    @SerializedName("creator_first_name")
    private String creatorFirstName;

    @SerializedName("creator_last_name")
    private String creatorLastName;

    // Update getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCreatorId() { return creatorId; }
    public void setCreatorId(Integer creatorId) { this.creatorId = creatorId; }

    // Other getters and setters remain the same
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public Integer getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Integer assigneeId) { this.assigneeId = assigneeId; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public Float getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Float estimatedHours) { this.estimatedHours = estimatedHours; }

    public String getAssigneeName() {
        if (assigneeFirstName == null && assigneeLastName == null) return null;
        return (assigneeFirstName != null ? assigneeFirstName + " " : "") + (assigneeLastName != null ? assigneeLastName : "");
    }
}