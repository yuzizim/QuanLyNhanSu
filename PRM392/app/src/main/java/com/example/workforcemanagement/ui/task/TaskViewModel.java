package com.example.workforcemanagement.ui.task;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.workforcemanagement.data.model.EmployeesResponse;
import com.example.workforcemanagement.data.model.Task;
import com.example.workforcemanagement.data.model.TasksResponse;
import com.example.workforcemanagement.data.model.UsersResponse;
import com.example.workforcemanagement.data.repository.TaskRepository;
import com.example.workforcemanagement.util.TokenManager;

public class TaskViewModel extends AndroidViewModel {
    private final TaskRepository taskRepository;
    private final MutableLiveData<TasksResponse> tasks = new MutableLiveData<>();
    private final MutableLiveData<UsersResponse> users = new MutableLiveData<>();
    private final MutableLiveData<EmployeesResponse> employees = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationResult = new MutableLiveData<>();

    public TaskViewModel(Application application) {
        super(application);
        taskRepository = new TaskRepository(new TokenManager(application));
    }

    public LiveData<TasksResponse> getTasks() {
        return tasks;
    }

    public LiveData<UsersResponse> getUsers() {
        return users;
    }

    public LiveData<EmployeesResponse> getEmployees() {
        return employees;
    }

    public void fetchTasks(String search, String status, String priority, int page, int limit) {
        taskRepository.getTasks(search, status, priority, page, limit).observeForever(tasks::setValue);
    }

    public void fetchUsers() {
        taskRepository.getUsers("", "", 1, 100).observeForever(users::setValue);
    }

    public void fetchEmployees() {
        taskRepository.getEmployees("", "", 1, 100).observeForever(employees::setValue);
    }

    public LiveData<Boolean> createTask(Task task) {
        taskRepository.createTask(task).observeForever(operationResult::setValue);
        return operationResult;
    }

    public LiveData<Boolean> updateTask(Task task) {
        taskRepository.updateTask(task.getId(), task).observeForever(operationResult::setValue);
        return operationResult;
    }

    public LiveData<Boolean> deleteTask(int id) {
        taskRepository.deleteTask(id).observeForever(operationResult::setValue);
        return operationResult;
    }
}