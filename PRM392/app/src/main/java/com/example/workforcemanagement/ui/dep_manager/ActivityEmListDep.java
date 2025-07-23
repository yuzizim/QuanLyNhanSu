package com.example.workforcemanagement.ui.dep_manager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.api.ApiClient;
import com.example.workforcemanagement.data.api.ApiService;
import com.example.workforcemanagement.data.model.Department;
import com.example.workforcemanagement.data.model.Employee;
import com.example.workforcemanagement.data.model.EmployeesResponse;
import com.example.workforcemanagement.ui.adapter.EmployeeAdapter;
import com.example.workforcemanagement.util.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityEmListDep extends AppCompatActivity {
    private RecyclerView rvEmployeeList;
    private EmployeeAdapter employeeAdapter;
    private List<Employee> employeeList = new ArrayList<>();
    private EditText etSearchEmployee;
    private Department department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_em_list_dep);

        // Lấy thông tin phòng ban từ Intent
        department = (Department) getIntent().getSerializableExtra("department");

        initViews();
        setupRecyclerView();
        setupSearchFunctionality();
        setupBackButton();

        // Load dữ liệu từ backend - không truyền search parameter ban đầu
        loadEmployees();
    }

    private void initViews() {
        rvEmployeeList = findViewById(R.id.rvEmployeeList);
        etSearchEmployee = findViewById(R.id.etSearchEmployee);
    }

    private void setupRecyclerView() {
        rvEmployeeList.setLayoutManager(new LinearLayoutManager(this));
        employeeAdapter = new EmployeeAdapter(employeeList);
        rvEmployeeList.setAdapter(employeeAdapter);
    }

    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupSearchFunctionality() {
        etSearchEmployee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Search trong danh sách hiện có
                if (employeeAdapter != null) {
                    employeeAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadEmployees() {
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();
        Log.d("TOKEN_CHECK", "Token vừa lấy từ SharedPreferences: " + token);

        if (token == null) {
            Log.e("TOKEN_CHECK", "Token null, người dùng chưa đăng nhập hoặc đã bị xóa token!");
            // TODO: Thông báo user đăng nhập lại
            return;
        }

        String authHeader = "Bearer " + token;
        Log.d("TOKEN_CHECK", "Header Authorization gửi lên: " + authHeader);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Gọi API để lấy tất cả nhân viên (hoặc theo department nếu cần)
        // Không truyền search parameter ở đây vì chúng ta sẽ search local
        apiService.getEmployees(authHeader, "", null, 1, 100)
                .enqueue(new Callback<EmployeesResponse>() {
                    @Override
                    public void onResponse(Call<EmployeesResponse> call, Response<EmployeesResponse> response) {
                        Log.d("TOKEN_CHECK", "Response code: " + response.code());
                        if (response.body() != null && response.body().isSuccess() && response.body().getData() != null) {
                            employeeList.clear();
                            if (response.body().getData().getEmployees() != null) {
                                // Nếu có thông tin department, có thể filter theo department ở đây
                                List<Employee> allEmployees = response.body().getData().getEmployees();

                                // TODO: Nếu muốn filter theo department cụ thể, thêm logic ở đây
                                // Ví dụ: if (department != null) { filter by department.getId() }

                                employeeList.addAll(allEmployees);

                                // Cập nhật adapter với danh sách đầy đủ để search local hoạt động
                                employeeAdapter.updateEmployeeList(employeeList);
                            }
                            employeeAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("TOKEN_CHECK", "API không trả về data (body null hoặc lỗi): " +
                                    (response.body() != null ? response.body().getMessage() : "body null"));
                        }
                    }

                    @Override
                    public void onFailure(Call<EmployeesResponse> call, Throwable t) {
                        Log.e("TOKEN_CHECK", "API call failed: " + t.getMessage());
                    }
                });
    }
}