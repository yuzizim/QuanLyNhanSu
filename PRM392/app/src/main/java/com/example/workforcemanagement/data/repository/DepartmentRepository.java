package com.example.workforcemanagement.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.workforcemanagement.data.api.ApiClient;
import com.example.workforcemanagement.data.api.ApiService;
import com.example.workforcemanagement.data.model.Department;
import com.example.workforcemanagement.data.model.DepartmentResponse;
import com.example.workforcemanagement.data.model.EmployeeListResponse;
import com.example.workforcemanagement.util.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepartmentRepository {
    private final ApiService apiService;
    private final TokenManager tokenManager;

    public DepartmentRepository(TokenManager tokenManager) {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.tokenManager = tokenManager;
    }

    public LiveData<DepartmentResponse> getDepartments(String search, int page, int limit) {
        MutableLiveData<DepartmentResponse> result = new MutableLiveData<>();
        String token = tokenManager.getToken();

        if (token == null) {
            DepartmentResponse errorResponse = new DepartmentResponse(false, "No token available", null);
            result.setValue(errorResponse);
            return result;
        }

        apiService.getDepartments("Bearer " + token, search, page, limit).enqueue(new Callback<DepartmentResponse>() {
            @Override
            public void onResponse(Call<DepartmentResponse> call, Response<DepartmentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    DepartmentResponse errorResponse = new DepartmentResponse(false, "Failed to fetch departments: " + response.code(), null);
                    result.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<DepartmentResponse> call, Throwable t) {
                DepartmentResponse errorResponse = new DepartmentResponse(false, "Network error: " + t.getMessage(), null);
                result.setValue(errorResponse);
            }
        });

        return result;
    }

    public LiveData<DepartmentResponse> getDepartment(int id) {
        MutableLiveData<DepartmentResponse> result = new MutableLiveData<>();
        String token = tokenManager.getToken();

        if (token == null) {
            DepartmentResponse errorResponse = new DepartmentResponse(false, "No token available", null);
            result.setValue(errorResponse);
            return result;
        }

        apiService.getDepartment("Bearer " + token, id).enqueue(new Callback<DepartmentResponse>() {
            @Override
            public void onResponse(Call<DepartmentResponse> call, Response<DepartmentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    DepartmentResponse errorResponse = new DepartmentResponse(false, "Failed to fetch department: " + response.code(), null);
                    result.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<DepartmentResponse> call, Throwable t) {
                DepartmentResponse errorResponse = new DepartmentResponse(false, "Network error: " + t.getMessage(), null);
                result.setValue(errorResponse);
            }
        });

        return result;
    }

    public LiveData<Boolean> createDepartment(Department department) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        String token = tokenManager.getToken();

        apiService.createDepartment("Bearer " + token, department).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                result.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(false);
            }
        });

        return result;
    }

    public LiveData<Boolean> updateDepartment(int id, Department department) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        String token = tokenManager.getToken();

        apiService.updateDepartment("Bearer " + token, id, department).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                result.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(false);
            }
        });

        return result;
    }

    public LiveData<Boolean> deleteDepartment(int id) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        String token = tokenManager.getToken();

        apiService.deleteDepartment("Bearer " + token, id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                result.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(false);
            }
        });

        return result;
    }

    public LiveData<EmployeeListResponse> getEligibleManagers() {
        MutableLiveData<EmployeeListResponse> result = new MutableLiveData<>();
        String token = tokenManager.getToken();

        if (token == null) {
            EmployeeListResponse errorResponse = new EmployeeListResponse(false, "No token available", null);
            result.setValue(errorResponse);
            return result;
        }

        apiService.getEligibleManagers("Bearer " + token).enqueue(new Callback<EmployeeListResponse>() {
            @Override
            public void onResponse(Call<EmployeeListResponse> call, Response<EmployeeListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    EmployeeListResponse errorResponse = new EmployeeListResponse(false, "Failed to fetch managers: " + response.code(), null);
                    result.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<EmployeeListResponse> call, Throwable t) {
                EmployeeListResponse errorResponse = new EmployeeListResponse(false, "Network error: " + t.getMessage(), null);
                result.setValue(errorResponse);
            }
        });

        return result;
    }
}