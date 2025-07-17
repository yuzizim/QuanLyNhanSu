package com.example.workforcemanagement.ui.department;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.workforcemanagement.data.model.DepartmentResponse;
import com.example.workforcemanagement.data.repository.DepartmentRepository;

public class DepartmentViewModel extends ViewModel {
    private final DepartmentRepository departmentRepository;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<DepartmentResponse> departments = new MutableLiveData<>();
    private int currentPage = 1;
    private final int limit = 10;

    public DepartmentViewModel(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
        loadDepartments();
    }

    public LiveData<DepartmentResponse> getDepartments() {
        return departments;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
        loadDepartments();
    }

    public void loadDepartments() {
        departmentRepository.getDepartments(searchQuery.getValue(), currentPage, limit).observeForever(departments::setValue);
    }

    public LiveData<Boolean> deleteDepartment(int id) {
        return departmentRepository.deleteDepartment(id);
    }
}