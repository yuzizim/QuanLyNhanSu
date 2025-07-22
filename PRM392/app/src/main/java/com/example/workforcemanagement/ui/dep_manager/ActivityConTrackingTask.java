package com.example.workforcemanagement.ui.dep_manager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.api.ApiClient;
import com.example.workforcemanagement.data.api.ApiService;
import com.example.workforcemanagement.data.model.Task;
import com.example.workforcemanagement.data.model.TasksResponse;
import com.example.workforcemanagement.ui.adapter.TaskAdapter; // Sửa lại import này
import com.example.workforcemanagement.util.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityConTrackingTask extends AppCompatActivity {
    private RecyclerView rvTaskList;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();
    private EditText etSearchTask;
    private Spinner spinnerPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_con_tracking_task); // layout đúng mẫu có RV và search/filter

        rvTaskList = findViewById(R.id.rvTaskList);
        etSearchTask = findViewById(R.id.etSearchTask);
        spinnerPriority = findViewById(R.id.spinnerPriority);

        rvTaskList.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList);
        rvTaskList.setAdapter(taskAdapter);

        // Search task
        etSearchTask.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskAdapter.filter(s.toString(), spinnerPriority.getSelectedItem().toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Filter by priority
        spinnerPriority.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                taskAdapter.filter(etSearchTask.getText().toString(), spinnerPriority.getSelectedItem().toString());
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Add task button
        ImageView btnAddTask = findViewById(R.id.btnAddTask);
        btnAddTask.setOnClickListener(v -> {
            // TODO: Mở dialog/thêm nhiệm vụ mới
        });

        // Back button
        ImageView btnBack = findViewById(R.id.btnBackTracking);
        btnBack.setOnClickListener(v -> finish());

        // Load dữ liệu từ backend
        loadTasks("", "");
    }

    private void loadTasks(String search, String priority) {
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();
        String authHeader = "Bearer " + token;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getTasks(authHeader, search, null, priority, 1, 100)
                .enqueue(new Callback<TasksResponse>() {
                    @Override
                    public void onResponse(Call<TasksResponse> call, Response<TasksResponse> response) {
                        if (response.body() != null && response.body().isSuccess() && response.body().getData() != null) {
                            taskList.clear();
                            if (response.body().getData().getTasks() != null) {
                                taskList.addAll(response.body().getData().getTasks());
                                taskAdapter.setFullList(taskList); // For search/filter
                            }
                            taskAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onFailure(Call<TasksResponse> call, Throwable t) {
                        // Handle error here
                    }
                });
    }
}