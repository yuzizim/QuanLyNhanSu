package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Department implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("code")
    private String code;

    @SerializedName("description")
    private String description;

    @SerializedName("manager_id")
    private Integer managerId;

    @SerializedName("manager_first_name")
    private String managerFirstName;

    @SerializedName("manager_last_name")
    private String managerLastName;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getManagerId() { return managerId; }
    public void setManagerId(Integer managerId) { this.managerId = managerId; }

    public String getManagerFirstName() { return managerFirstName; }
    public void setManagerFirstName(String managerFirstName) { this.managerFirstName = managerFirstName; }

    public String getManagerLastName() { return managerLastName; }
    public void setManagerLastName(String managerLastName) { this.managerLastName = managerLastName; }

    public String getManagerFullName() {
        if (managerFirstName == null && managerLastName == null) return "No Manager";
        return (managerFirstName != null ? managerFirstName + " " : "") + (managerLastName != null ? managerLastName : "");
    }
}