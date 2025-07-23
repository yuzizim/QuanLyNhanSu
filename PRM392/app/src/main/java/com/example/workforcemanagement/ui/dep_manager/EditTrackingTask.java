package com.example.workforcemanagement.ui.dep_manager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.example.workforcemanagement.data.model.Task;
import com.example.workforcemanagement.util.TokenManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditTrackingTask extends AppCompatActivity {

    private Task task;
    private EditText etTaskTitle, etTaskDesc, etDeadline, etStartDate;
    private Spinner spinnerAssignee, spinnerPriority, spinnerStatus;
    private List<Employee> employeeList = new ArrayList<>();
    private ArrayAdapter<String> assigneeAdapter, priorityAdapter, statusAdapter;
    private String[] priorities = {"low", "medium", "high", "urgent"};
    private String[] statuses = {"pending", "in_progress", "review", "completed", "cancelled"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_tracking_task);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Init views
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDesc = findViewById(R.id.etTaskDesc);
        etDeadline = findViewById(R.id.etDeadline);
        etStartDate = findViewById(R.id.etStartDate);
        spinnerAssignee = findViewById(R.id.spinnerAssignee);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        spinnerStatus = findViewById(R.id.spinnerStatus);

        // Lấy và hiển thị chi tiết task
        task = (Task) getIntent().getSerializableExtra("task");
        if (task != null) {
            etTaskTitle.setText(task.getTitle());
            etTaskDesc.setText(task.getDescription());
            etDeadline.setText(formatDateForDisplay(task.getDeadline()));
            etStartDate.setText(formatDateForDisplay(task.getStartDate()));
        }

        // Nạp data cho các spinner
        loadEmployees();

        // Nạp static cho priority, status
        priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Set giá trị cho priority/status nếu task đã có
        if (task != null) {
            // Ưu tiên
            for (int i = 0; i < priorities.length; i++) {
                if (priorities[i].equalsIgnoreCase(task.getPriority())) {
                    spinnerPriority.setSelection(i);
                    break;
                }
            }
            // Trạng thái
            for (int i = 0; i < statuses.length; i++) {
                if (statuses[i].equalsIgnoreCase(task.getStatus())) {
                    spinnerStatus.setSelection(i);
                    break;
                }
            }
        }

        // Chọn ngày cho deadline
        etDeadline.setOnClickListener(v -> showDatePicker());

        // Không cho phép sửa start_date
        etStartDate.setEnabled(false);
        etStartDate.setFocusable(false);

        // Nút cập nhật
        findViewById(R.id.btnUpdateTask).setOnClickListener(v -> updateTask());

        // Nút hủy (quay lại)
        findViewById(R.id.btnCancelTask).setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String dateStr = etDeadline.getText().toString();
            if (dateStr.contains("T")) dateStr = dateStr.substring(0, 10);
            calendar.setTime(sdf.parse(dateStr));
        } catch (Exception ignored) {}

        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            etDeadline.setText(dateStr);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    private void loadEmployees() {
        String token = new TokenManager(this).getToken();
        String authHeader = "Bearer " + token;
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getEmployees(authHeader, "", "", 1, 100)
                .enqueue(new Callback<EmployeesResponse>() {
                    @Override
                    public void onResponse(Call<EmployeesResponse> call, Response<EmployeesResponse> response) {
                        employeeList.clear();
                        List<Employee> employees = response.body() != null && response.body().getData() != null
                                ? response.body().getData().getEmployees()
                                : new ArrayList<>();
                        if (employees != null) employeeList.addAll(employees);
                        List<String> assigneeNames = new ArrayList<>();
                        for (Employee e : employeeList) {
                            assigneeNames.add(e.getFullName());
                        }
                        assigneeAdapter = new ArrayAdapter<>(EditTrackingTask.this, android.R.layout.simple_spinner_item, assigneeNames);
                        assigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerAssignee.setAdapter(assigneeAdapter);

                        // Nếu task có sẵn assignee
                        if (task != null && task.getAssigneeId() != null) {
                            for (int i = 0; i < employeeList.size(); i++) {
                                if (employeeList.get(i).getId() == task.getAssigneeId()) {
                                    spinnerAssignee.setSelection(i);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EmployeesResponse> call, Throwable t) {
                        Toast.makeText(EditTrackingTask.this, "Không load được danh sách nhân viên", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Định dạng lại ngày ISO hoặc yyyy-MM-dd thành yyyy-MM-dd cho EditText
    private String formatDateForDisplay(String iso) {
        if (iso == null) return "";
        if (iso.contains("T")) {
            return iso.substring(0, 10);
        }
        return iso;
    }

    // Đảm bảo gửi về backend định dạng yyyy-MM-dd (MySQL không nhận T...Z)
    private String formatDateForBackend(String date) {
        if (date == null) return null;
        try {
            String input = date;
            if (input.contains("T")) {
                input = input.substring(0, 10);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.format(sdf.parse(input));
        } catch (Exception e) {
            return date;
        }
    }

    private void updateTask() {
        if (task == null) return;
        String title = etTaskTitle.getText().toString().trim();
        String desc = etTaskDesc.getText().toString().trim();
        String deadline = formatDateForBackend(etDeadline.getText().toString().trim());
        String startDate = formatDateForBackend(etStartDate.getText().toString().trim());

        // Lấy assignee_id từ spinner
        Integer assigneeId = null;
        if (employeeList.size() > 0 && spinnerAssignee.getSelectedItemPosition() >= 0) {
            assigneeId = employeeList.get(spinnerAssignee.getSelectedItemPosition()).getId();
        }

        String priority = spinnerPriority.getSelectedItem() != null ? spinnerPriority.getSelectedItem().toString() : null;
        String status = spinnerStatus.getSelectedItem() != null ? spinnerStatus.getSelectedItem().toString() : null;

        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("title", title);
        updateFields.put("description", desc);
        updateFields.put("deadline", deadline);
        updateFields.put("start_date", startDate);  // Gửi luôn start_date lên backend
        if (assigneeId != null) updateFields.put("assignee_id", assigneeId);
        if (priority != null) updateFields.put("priority", priority);
        if (status != null) updateFields.put("status", status);

        String token = new TokenManager(this).getToken();
        String authHeader = "Bearer " + token;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.updateTask(authHeader, task.getId(), updateFields)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(EditTrackingTask.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            String errMsg = "Lỗi cập nhật!";
                            try {
                                errMsg = response.errorBody() != null ? response.errorBody().string() : errMsg;
                            } catch (Exception ignored) {}
                            Toast.makeText(EditTrackingTask.this, errMsg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(EditTrackingTask.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}