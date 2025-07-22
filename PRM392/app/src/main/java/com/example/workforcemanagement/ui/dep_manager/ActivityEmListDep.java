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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_em_list_dep);


        // Lấy thông tin phòng ban từ Intent
        Department department = (Department) getIntent().getSerializableExtra("department");
        // Sử dụng department để gọi API lấy danh sách nhân viên của phòng ban này

        rvEmployeeList = findViewById(R.id.rvEmployeeList);
        etSearchEmployee = findViewById(R.id.etSearchEmployee);
        rvEmployeeList.setLayoutManager(new LinearLayoutManager(this));
        employeeAdapter = new EmployeeAdapter(employeeList);
        rvEmployeeList.setAdapter(employeeAdapter);

        // Xử lý nút Back
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Tìm kiếm nhân viên
        etSearchEmployee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                employeeAdapter.filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Load dữ liệu từ backend
        loadEmployees("");
    }

    private void loadEmployees(String search) {
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken(); // chỉ là JWT thuần
        Log.d("TOKEN_CHECK", "Token vừa lấy từ SharedPreferences: " + token);

        if (token == null) {
            Log.e("TOKEN_CHECK", "Token null, người dùng chưa đăng nhập hoặc đã bị xóa token!");
            // TODO: Thông báo user đăng nhập lại
            return;
        }

        String authHeader = "Bearer " + token;
        Log.d("TOKEN_CHECK", "Header Authorization gửi lên: " + authHeader);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getEmployees(authHeader, search, null, 1, 100)
                .enqueue(new Callback<EmployeesResponse>() {
                    @Override
                    public void onResponse(Call<EmployeesResponse> call, Response<EmployeesResponse> response) {
                        Log.d("TOKEN_CHECK", "Response code: " + response.code());
                        if (response.body() != null && response.body().isSuccess() && response.body().getData() != null) {
                            employeeList.clear();
                            if (response.body().getData().getEmployees() != null) {
                                employeeList.addAll(response.body().getData().getEmployees());
                                employeeAdapter.setFullList(employeeList); // For search filter
                            }
                            employeeAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("TOKEN_CHECK", "API không trả về data (body null hoặc lỗi): " + (response.body() != null ? response.body().getMessage() : "body null"));
                        }
                    }
                    @Override
                    public void onFailure(Call<EmployeesResponse> call, Throwable t) {
                        Log.e("TOKEN_CHECK", "API call failed: " + t.getMessage());
                    }
                });
    }

}