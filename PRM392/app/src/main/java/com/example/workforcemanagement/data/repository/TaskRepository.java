package com.example.workforcemanagement.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.workforcemanagement.data.api.ApiClient;
import com.example.workforcemanagement.data.api.ApiService;
import com.example.workforcemanagement.data.model.EmployeesResponse;
import com.example.workforcemanagement.data.model.Task;
import com.example.workforcemanagement.data.model.TasksResponse;
import com.example.workforcemanagement.data.model.UsersResponse;
import com.example.workforcemanagement.util.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

public class TaskRepository {
    private final ApiService apiService;
    private final TokenManager tokenManager;

    public TaskRepository(TokenManager tokenManager) {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.tokenManager = tokenManager;
    }

    public LiveData<TasksResponse> getTasks(String search, String status, String priority, int page, int limit) {
        MutableLiveData<TasksResponse> tasksResult = new MutableLiveData<>();
        String token = tokenManager.getToken();

        if (token == null) {
            TasksResponse errorResponse = new TasksResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("No token available");
            tasksResult.setValue(errorResponse);
            return tasksResult;
        }

        apiService.getTasks("Bearer " + token, search, status, priority, page, limit)
                .enqueue(new Callback<TasksResponse>() {
                    @Override
                    public void onResponse(Call<TasksResponse> call, Response<TasksResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            tasksResult.setValue(response.body());
                        } else {
                            TasksResponse errorResponse = new TasksResponse();
                            errorResponse.setSuccess(false);
                            errorResponse.setMessage("Failed to fetch tasks: " + response.code());
                            tasksResult.setValue(errorResponse);
                        }
                    }

                    @Override
                    public void onFailure(Call<TasksResponse> call, Throwable t) {
                        TasksResponse errorResponse = new TasksResponse();
                        errorResponse.setSuccess(false);
                        errorResponse.setMessage("Network error: " + t.getMessage());
                        tasksResult.setValue(errorResponse);
                    }
                });

        return tasksResult;
    }

    public LiveData<UsersResponse> getUsers(String search, String filter, int page, int limit) {
        return new UserRepository(tokenManager).getUsers(search, filter, page, limit);
    }

    public LiveData<EmployeesResponse> getEmployees(String search, String filter, int page, int limit) {
        MutableLiveData<EmployeesResponse> employeesResult = new MutableLiveData<>();
        String token = tokenManager.getToken();

        if (token == null) {
            EmployeesResponse errorResponse = new EmployeesResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("No token available");
            employeesResult.setValue(errorResponse);
            return employeesResult;
        }

        apiService.getEmployees("Bearer " + token, search, filter, page, limit)
                .enqueue(new Callback<EmployeesResponse>() {
                    @Override
                    public void onResponse(Call<EmployeesResponse> call, Response<EmployeesResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            employeesResult.setValue(response.body());
                        } else {
                            EmployeesResponse errorResponse = new EmployeesResponse();
                            errorResponse.setSuccess(false);
                            errorResponse.setMessage("Failed to fetch employees: " + response.code());
                            employeesResult.setValue(errorResponse);
                        }
                    }

                    @Override
                    public void onFailure(Call<EmployeesResponse> call, Throwable t) {
                        EmployeesResponse errorResponse = new EmployeesResponse();
                        errorResponse.setSuccess(false);
                        errorResponse.setMessage("Network error: " + t.getMessage());
                        employeesResult.setValue(errorResponse);
                    }
                });

        return employeesResult;
    }


    public LiveData<Boolean> createTask(Task task) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        String token = tokenManager.getToken();

        apiService.createTask("Bearer " + token, task).enqueue(new Callback<Void>() {
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

    public LiveData<Boolean> updateTask(int id, Task task) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        String token = tokenManager.getToken();

        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("title", task.getTitle());
        if (task.getDescription() != null) updateFields.put("description", task.getDescription());
        if (task.getStartDate() != null) updateFields.put("start_date", task.getStartDate());
        if (task.getDeadline() != null) updateFields.put("deadline", task.getDeadline());
        if (task.getAssigneeId() != null) updateFields.put("assignee_id", task.getAssigneeId());
        updateFields.put("priority", task.getPriority());
        updateFields.put("status", task.getStatus());
        updateFields.put("progress", task.getProgress());
        if (task.getEstimatedHours() != null) updateFields.put("estimated_hours", task.getEstimatedHours());
        updateFields.put("creator_id", task.getCreatorId());

        apiService.updateTask("Bearer " + token, id, updateFields).enqueue(new Callback<Void>() {
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

    public LiveData<Boolean> deleteTask(int id) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        String token = tokenManager.getToken();

        apiService.deleteTask("Bearer " + token, id).enqueue(new Callback<Void>() {
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
}