package com.example.workforcemanagement.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.workforcemanagement.data.api.ApiClient;
import com.example.workforcemanagement.data.api.ApiService;
import com.example.workforcemanagement.data.model.StatsResponse;
import com.example.workforcemanagement.util.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatsRepository {
    private static final String TAG = "StatsRepository";
    private final ApiService apiService;
    private final TokenManager tokenManager;

    public StatsRepository(TokenManager tokenManager) {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.tokenManager = tokenManager;
    }

    public LiveData<StatsResponse> getDashboardStats() {
        MutableLiveData<StatsResponse> statsResult = new MutableLiveData<>();
        String token = tokenManager.getToken();

        if (token == null) {
            StatsResponse errorResponse = new StatsResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("No token available");
            statsResult.setValue(errorResponse);
            return statsResult;
        }

        apiService.getDashboardStats("Bearer " + token).enqueue(new Callback<StatsResponse>() {
            @Override
            public void onResponse(Call<StatsResponse> call, Response<StatsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    statsResult.setValue(response.body());
                } else {
                    StatsResponse errorResponse = new StatsResponse();
                    errorResponse.setSuccess(false);
                    String errorMessage = "Failed to fetch stats: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    errorResponse.setMessage(errorMessage);
                    statsResult.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<StatsResponse> call, Throwable t) {
                StatsResponse errorResponse = new StatsResponse();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Network error: " + t.getMessage());
                statsResult.setValue(errorResponse);
            }
        });

        return statsResult;
    }
}