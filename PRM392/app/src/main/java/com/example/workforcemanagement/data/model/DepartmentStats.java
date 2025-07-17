package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.SerializedName;

public class DepartmentStats {
    @SerializedName("total_departments")
    private int totalDepartments;

    @SerializedName("departments_with_managers")
    private int departmentsWithManagers;

    public int getTotalDepartments() { return totalDepartments; }
    public void setTotalDepartments(int totalDepartments) { this.totalDepartments = totalDepartments; }

    public int getDepartmentsWithManagers() { return departmentsWithManagers; }
    public void setDepartmentsWithManagers(int departmentsWithManagers) { this.departmentsWithManagers = departmentsWithManagers; }
}