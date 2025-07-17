package com.example.workforcemanagement.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DepartmentResponse {
    private boolean success;
    private String message;
    private DepartmentData data;

    public DepartmentResponse(boolean success, String message, DepartmentData data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public DepartmentData getData() {
        return data;
    }

    public static class DepartmentData {
        private List<Department> departments;
        private DepartmentStats stats;

        public List<Department> getDepartments() {
            return departments;
        }

        public void setDepartments(List<Department> departments) {
            this.departments = departments;
        }

        public DepartmentStats getStats() {
            return stats;
        }

        public void setStats(DepartmentStats stats) {
            this.stats = stats;
        }
    }

    public static class DepartmentStats {
        @SerializedName("total_departments")
        private int totalDepartments;

        @SerializedName("departments_with_managers")
        private int departmentsWithManagers;

        public int getTotalDepartments() {
            return totalDepartments;
        }

        public void setTotalDepartments(int totalDepartments) {
            this.totalDepartments = totalDepartments;
        }

        public int getDepartmentsWithManagers() {
            return departmentsWithManagers;
        }

        public void setDepartmentsWithManagers(int departmentsWithManagers) {
            this.departmentsWithManagers = departmentsWithManagers;
        }
    }
}