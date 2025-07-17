package com.example.workforcemanagement.ui.department;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.workforcemanagement.data.repository.DepartmentRepository;

public class DepartmentViewModelFactory implements ViewModelProvider.Factory {
    private final DepartmentRepository departmentRepository;

    public DepartmentViewModelFactory(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DepartmentViewModel.class)) {
            return (T) new DepartmentViewModel(departmentRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}