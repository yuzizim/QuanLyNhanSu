package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EmployeesData {
    @SerializedName("employees")
    private List<Employee> employees;

    // Getters and Setters
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
}