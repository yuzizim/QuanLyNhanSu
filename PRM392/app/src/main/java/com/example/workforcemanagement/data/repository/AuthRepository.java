// AuthRepository.java
package com.example.workforcemanagement.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.workforcemanagement.data.api.ApiClient;
import com.example.workforcemanagement.data.api.ApiService;
import com.example.workforcemanagement.data.model.GoogleLoginRequest;
import com.example.workforcemanagement.data.model.LoginRequest;
import com.example.workforcemanagement.data.model.LoginResponse;
import com.example.workforcemanagement.data.model.RequestPasswordResetRequest;
import com.example.workforcemanagement.data.model.ResetPasswordRequest;
import com.example.workforcemanagement.util.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private final ApiService apiService;
    private final TokenManager tokenManager;

    public AuthRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.tokenManager = null;
    }

    public AuthRepository(TokenManager tokenManager) {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.tokenManager = tokenManager;
    }

    public LiveData<LoginResponse> login(String email, String password) {
        MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();

        // Create login request object
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Make API call
        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "onResponse: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Login success: " + loginResponse.isSuccess());

                    // If login was successful, save the token
                    if (loginResponse.isSuccess()) {
                        tokenManager.saveToken(loginResponse.getToken());
                    }

                    loginResult.setValue(loginResponse);
                } else {
                    // Handle error response
                    LoginResponse errorResponse = new LoginResponse();
                    errorResponse.setSuccess(false);
                    String errorMessage = "Login failed: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    errorResponse.setMessage(errorMessage);
                    loginResult.setValue(errorResponse);
                    Log.e(TAG, "Login failed: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Handle network or other failures
                LoginResponse errorResponse = new LoginResponse();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Network error: " + t.getMessage());
                loginResult.setValue(errorResponse);
                Log.e(TAG, "Network error", t);
            }
        });

        return loginResult;
    }

//    public void googleLogin(String idToken, MutableLiveData<LoginResponse> loginResult) {
//        GoogleLoginRequest googleLoginRequest = new GoogleLoginRequest(idToken);
//
//        apiService.googleLogin(googleLoginRequest).enqueue(new Callback<LoginResponse>() {
//            @Override
//            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//                Log.d(TAG, "Google login onResponse: " + response.code());
//                if (response.isSuccessful() && response.body() != null) {
//                    LoginResponse loginResponse = response.body();
//                    Log.d(TAG, "Google login success: " + loginResponse.isSuccess());
//                    if (loginResponse.isSuccess()) {
//                        tokenManager.saveToken(loginResponse.getToken());
//                    }
//                    loginResult.setValue(loginResponse);
//                } else {
//                    LoginResponse errorResponse = new LoginResponse();
//                    errorResponse.setSuccess(false);
//                    String errorMessage = "Google login failed: " + response.code();
//                    if (response.errorBody() != null) {
//                        try {
//                            errorMessage = response.errorBody().string();
//                        } catch (Exception e) {
//                            Log.e(TAG, "Error parsing error body", e);
//                        }
//                    }
//                    errorResponse.setMessage(errorMessage);
//                    loginResult.setValue(errorResponse);
//                    Log.e(TAG, "Google login failed: " + errorMessage);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                LoginResponse errorResponse = new LoginResponse();
//                errorResponse.setSuccess(false);
//                errorResponse.setMessage("Network error: " + t.getMessage());
//                loginResult.setValue(errorResponse);
//                Log.e(TAG, "Google login network error", t);
//            }
//        });
//    }

    public void googleLogin(String idToken, MutableLiveData<LoginResponse> loginResult) {
        GoogleLoginRequest googleLoginRequest = new GoogleLoginRequest(idToken);
        apiService.googleLogin(googleLoginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "Google login response code: " + response.code());
                Log.d(TAG, "Google login response body: " + (response.body() != null ? response.body().toString() : "null"));
                if (response.errorBody() != null) {
                    try {
                        Log.e(TAG, "Google login error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                }
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Google login success: " + loginResponse.isSuccess());
                    if (loginResponse.isSuccess()) {
                        tokenManager.saveToken(loginResponse.getToken());
                    }
                    loginResult.setValue(loginResponse);
                } else {
                    LoginResponse errorResponse = new LoginResponse();
                    errorResponse.setSuccess(false);
                    String errorMessage = "Google login failed: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    errorResponse.setMessage(errorMessage);
                    loginResult.setValue(errorResponse);
                    Log.e(TAG, "Google login failed: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Google login network error: " + t.getMessage(), t);
                LoginResponse errorResponse = new LoginResponse();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Network error: " + t.getMessage());
                loginResult.setValue(errorResponse);
            }
        });
    }

    // AuthRepository.java
    public LiveData<LoginResponse> getProfile() {
        MutableLiveData<LoginResponse> profileResult = new MutableLiveData<>();
        String token = tokenManager.getToken();
        if (token == null) {
            LoginResponse errorResponse = new LoginResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("No token available");
            profileResult.setValue(errorResponse);
            return profileResult;
        }

        apiService.getProfile("Bearer " + token).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    profileResult.setValue(response.body());
                } else {
                    LoginResponse errorResponse = new LoginResponse();
                    errorResponse.setSuccess(false);
                    String errorMessage = "Failed to fetch profile: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    errorResponse.setMessage(errorMessage);
                    profileResult.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                LoginResponse errorResponse = new LoginResponse();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Network error: " + t.getMessage());
                profileResult.setValue(errorResponse);
            }
        });

        return profileResult;
    }

    public void requestPasswordReset(String email, MutableLiveData<Boolean> success, MutableLiveData<String> errorMessage) {
        RequestPasswordResetRequest request = new RequestPasswordResetRequest(email);
        apiService.requestPasswordReset(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Request failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void resetPassword(String email, String otp, String newPassword, MutableLiveData<Boolean> success, MutableLiveData<String> errorMessage) {
        ResetPasswordRequest request = new ResetPasswordRequest(email, otp, newPassword);
        apiService.resetPassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Reset failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}
