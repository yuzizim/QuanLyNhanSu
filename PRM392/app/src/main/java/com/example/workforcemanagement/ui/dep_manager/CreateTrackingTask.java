package com.example.workforcemanagement.ui.dep_manager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.api.ApiClient;
import com.example.workforcemanagement.data.api.ApiService;
import com.example.workforcemanagement.data.model.Employee;
import com.example.workforcemanagement.data.model.EmployeesResponse;
import com.example.workforcemanagement.data.model.EmployeesData;
import com.example.workforcemanagement.data.model.Task;
import com.example.workforcemanagement.util.TokenManager;

import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTrackingTask extends AppCompatActivity {

    private EditText etTaskTitle, etTaskDesc, etDeadline, etStartDate;
    private Spinner spinnerAssignee, spinnerPriority;
    private Button btnCreateTask, btnCancelTask;
    private ApiService apiService;
    private TokenManager tokenManager;
    private List<Employee> employeeList = new ArrayList<>();
    private ArrayAdapter<String> assigneeAdapter;
    private ArrayAdapter<String> priorityAdapter;
    private List<String> assigneeNames = new ArrayList<>();
    private String[] priorities = {"low", "medium", "high", "urgent"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_tracking_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Link UI
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDesc = findViewById(R.id.etTaskDesc);
        etDeadline = findViewById(R.id.etDeadline);
        etStartDate = findViewById(R.id.etStartDate);
        spinnerAssignee = findViewById(R.id.spinnerAssignee);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        btnCreateTask = findViewById(R.id.btnCreateTask);
        btnCancelTask = findViewById(R.id.btnCancelTask);

        apiService = ApiClient.getClient().create(ApiService.class);
        tokenManager = new TokenManager(this);

        // Đổ data cho spinnerPriority
        priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        // Load employees (phòng ban của manager)
        loadAssignees();

        // Ngày bắt đầu mặc định là hôm nay, không cho sửa
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        etStartDate.setText(today);
        etStartDate.setEnabled(false);
        etStartDate.setFocusable(false);

        // Chọn ngày deadline
        etDeadline.setOnClickListener(v -> showDatePicker());

        btnCreateTask.setOnClickListener(v -> createTask());
        btnCancelTask.setOnClickListener(v -> finish());
    }

    private void loadAssignees() {
        String token = tokenManager.getBearerToken();
        apiService.getEmployees(token, "", "", 1, 100)
                .enqueue(new Callback<EmployeesResponse>() {
                    @Override
                    public void onResponse(Call<EmployeesResponse> call, Response<EmployeesResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            EmployeesData employeesData = response.body().getData();
                            employeeList = employeesData.getEmployees();
                            assigneeNames.clear();
                            for (Employee emp : employeeList) {
                                assigneeNames.add(emp.getFullName());
                            }
                            assigneeAdapter = new ArrayAdapter<>(CreateTrackingTask.this,
                                    android.R.layout.simple_spinner_item, assigneeNames);
                            assigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerAssignee.setAdapter(assigneeAdapter);
                        } else {
                            Toast.makeText(CreateTrackingTask.this, "Không thể tải danh sách nhân viên phòng ban", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<EmployeesResponse> call, Throwable t) {
                        Toast.makeText(CreateTrackingTask.this, "Lỗi mạng khi tải nhân viên!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etDeadline.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void createTask() {
        String title = etTaskTitle.getText().toString().trim();
        String desc = etTaskDesc.getText().toString().trim();
        String deadline = etDeadline.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String priority = spinnerPriority.getSelectedItem().toString();
        int assigneeIndex = spinnerAssignee.getSelectedItemPosition();
        Integer assigneeId = (assigneeIndex >= 0 && assigneeIndex < employeeList.size())
                ? employeeList.get(assigneeIndex).getId()
                : null;

        if (title.isEmpty() || deadline.isEmpty() || assigneeId == null) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(desc);
        task.setStartDate(startDate);
        task.setDeadline(deadline);
        task.setPriority(priority);
        task.setAssigneeId(assigneeId);
        task.setStatus("pending");
        task.setProgress(0);

        String token = tokenManager.getBearerToken();
        apiService.createTask(token, task).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateTrackingTask.this, "Tạo nhiệm vụ thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Báo về Activity trước là đã tạo thành công
                    finish();
                } else {
                    Toast.makeText(CreateTrackingTask.this, "Tạo thất bại! " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CreateTrackingTask.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}