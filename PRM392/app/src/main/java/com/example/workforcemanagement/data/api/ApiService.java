// ApiService.java
package com.example.workforcemanagement.data.api;

import com.example.workforcemanagement.data.model.Department;
import com.example.workforcemanagement.data.model.DepartmentResponse;
import com.example.workforcemanagement.data.model.EmployeeListResponse;
import com.example.workforcemanagement.data.model.EmployeesResponse;
import com.example.workforcemanagement.data.model.LoginRequest;
import com.example.workforcemanagement.data.model.GoogleLoginRequest;
import com.example.workforcemanagement.data.model.LoginResponse;
import com.example.workforcemanagement.data.model.RequestPasswordResetRequest;
import com.example.workforcemanagement.data.model.ResetPasswordRequest;
import com.example.workforcemanagement.data.model.StatsResponse;
import com.example.workforcemanagement.data.model.SuccessResponse;
import com.example.workforcemanagement.data.model.TaskReportResponse;
import com.example.workforcemanagement.data.model.TasksResponse;
import com.example.workforcemanagement.data.model.UpdateProfileResponse;
import com.example.workforcemanagement.data.model.User;
import com.example.workforcemanagement.data.model.UsersResponse;
import com.example.workforcemanagement.data.model.Task;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/users/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/users/google")
    Call<LoginResponse> googleLogin(@Body GoogleLoginRequest googleLoginRequest);

    @POST("api/users/request-password-reset")
    Call<Void> requestPasswordReset(@Body RequestPasswordResetRequest request);

    @POST("api/users/reset-password")
    Call<Void> resetPassword(@Body ResetPasswordRequest request);

    @GET("api/users/profile")
    Call<LoginResponse> getProfile(@Header("Authorization") String token);

    @GET("api/stats/dashboard")
    Call<StatsResponse> getDashboardStats(@Header("Authorization") String token);

    @GET("api/users")
    Call<UsersResponse> getUsers(
            @Header("Authorization") String token,
            @Query("search") String search,
            @Query("filter") String filter,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @POST("api/users/create")
    Call<Void> createUser(@Header("Authorization") String token, @Body User user);

    @PUT("api/users/{id}")
    Call<Void> updateUser(@Header("Authorization") String token, @Path("id") int id, @Body Map<String, Object> updateFields);

    @DELETE("api/users/{id}")
    Call<Void> deleteUser(@Header("Authorization") String token, @Path("id") int id);

    @PUT("api/users/profile")
    Call<UpdateProfileResponse> updateProfile(@Header("Authorization") String token, @Body Map<String, Object> updateFields);

    // Department endpoints
    @GET("api/departments")
    Call<DepartmentResponse> getDepartments(
            @Header("Authorization") String token,
            @Query("search") String search,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("api/departments/{id}")
    Call<DepartmentResponse> getDepartment(@Header("Authorization") String token, @Path("id") int id);

    @POST("api/departments")
    Call<Void> createDepartment(
            @Header("Authorization") String token,
            @Body Department department
    );

    @PUT("api/departments/{id}")
    Call<Void> updateDepartment(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Body Department department
    );

    @DELETE("api/departments/{id}")
    Call<Void> deleteDepartment(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    @GET("api/departments/managers/eligible")
    Call<EmployeeListResponse> getEligibleManagers(
            @Header("Authorization") String token
    );


    @GET("api/employees")
    Call<EmployeesResponse> getEmployees(
            @Header("Authorization") String token,
            @Query("search") String search,
            @Query("filter") String filter,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("api/tasks")
    Call<TasksResponse> getTasks(
            @Header("Authorization") String token,
            @Query("search") String search,
            @Query("status") String status,
            @Query("priority") String priority,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @POST("api/tasks")
    Call<Void> createTask(@Header("Authorization") String token, @Body Task task);

    @PUT("api/tasks/{id}")
    Call<Void> updateTask(@Header("Authorization") String token, @Path("id") int id, @Body Map<String, Object> updateFields);

    @DELETE("api/tasks/{id}")
    Call<Void> deleteTask(@Header("Authorization") String token, @Path("id") int id);

    @GET("api/tasks/report")
    Call<TaskReportResponse> getTaskReport(
            @Header("Authorization") String authToken
    );
}