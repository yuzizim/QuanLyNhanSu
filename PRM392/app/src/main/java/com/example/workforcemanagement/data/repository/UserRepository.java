package com.example.workforcemanagement.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.workforcemanagement.data.api.ApiClient;
import com.example.workforcemanagement.data.api.ApiService;
import com.example.workforcemanagement.data.model.UsersResponse;
import com.example.workforcemanagement.data.model.User;
import com.example.workforcemanagement.util.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private final ApiService apiService;
    private final TokenManager tokenManager;

    public UserRepository(TokenManager tokenManager) {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.tokenManager = tokenManager;
    }

    public LiveData<UsersResponse> getUsers(String search, String filter, int page, int limit) {
        MutableLiveData<UsersResponse> usersResult = new MutableLiveData<>();
        String token = tokenManager.getToken();

        if (token == null) {
            UsersResponse errorResponse = new UsersResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("No token available");
            usersResult.setValue(errorResponse);
            return usersResult;
        }

        apiService.getUsers("Bearer " + token, search, filter, page, limit)
                .enqueue(new Callback<UsersResponse>() {
                    @Override
                    public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            usersResult.setValue(response.body());
                        } else {
                            UsersResponse errorResponse = new UsersResponse();
                            errorResponse.setSuccess(false);
                            String errorMessage = "Failed to fetch users: " + response.code();
                            if (response.errorBody() != null) {
                                try {
                                    errorMessage = response.errorBody().string();
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing error body", e);
                                }
                            }
                            errorResponse.setMessage(errorMessage);
                            usersResult.setValue(errorResponse);
                        }
                    }

                    @Override
                    public void onFailure(Call<UsersResponse> call, Throwable t) {
                        UsersResponse errorResponse = new UsersResponse();
                        errorResponse.setSuccess(false);
                        errorResponse.setMessage("Network error: " + t.getMessage());
                        usersResult.setValue(errorResponse);
                    }
                });

        return usersResult;
    }

    public LiveData<Boolean> createUser(User user) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        String token = tokenManager.getToken();

        apiService.createUser("Bearer " + token, user).enqueue(new Callback<Void>() {
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

    public LiveData<Boolean> updateUser(int id, Map<String, Object> updateFields) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        String token = tokenManager.getToken();

        apiService.updateUser("Bearer " + token, id, updateFields).enqueue(new Callback<Void>() {
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

    public LiveData<Boolean> deleteUser(int id) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        String token = tokenManager.getToken();

        apiService.deleteUser("Bearer " + token, id).enqueue(new Callback<Void>() {
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