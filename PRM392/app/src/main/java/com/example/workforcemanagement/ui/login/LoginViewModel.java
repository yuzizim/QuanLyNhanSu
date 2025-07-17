// LoginViewModel.java
package com.example.workforcemanagement.ui.login;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.workforcemanagement.data.model.LoginResponse;
import com.example.workforcemanagement.data.repository.AuthRepository;
import com.example.workforcemanagement.util.PreferenceManager;
import com.example.workforcemanagement.util.TokenManager;

public class LoginViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final PreferenceManager preferenceManager;
    private final MutableLiveData<LoginResponse> loginResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<LoginResponse> googleLoginResponse = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        TokenManager tokenManager = new TokenManager(application.getApplicationContext());
        this.authRepository = new AuthRepository(tokenManager);
        this.preferenceManager = new PreferenceManager(application.getApplicationContext());
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<LoginResponse> getLoginResponse() {
        return loginResponse;
    }

    public LiveData<LoginResponse> getGoogleLoginResponse() {
        return googleLoginResponse;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }

    public boolean validateInputs(String email, String password) {
        return isValidEmail(email) && isValidPassword(password);
    }

    private boolean isValidEmail(String email) {
        return email != null && !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public LiveData<LoginResponse> login(String email, String password, boolean rememberMe) {
        isLoading.setValue(true);

        // Save remember me preference
        preferenceManager.setRememberMe(rememberMe);
        if (rememberMe) {
            preferenceManager.saveEmail(email);
        } else {
            preferenceManager.clearEmail();
        }

        LiveData<LoginResponse> loginResult = authRepository.login(email, password);
        return loginResult;
    }

    public String getSavedEmail() {
        return preferenceManager.getEmail();
    }

    public void googleLogin(String idToken) {
        isLoading.setValue(true);
        authRepository.googleLogin(idToken, googleLoginResponse);
    }

    public boolean isRememberMeEnabled() {
        return preferenceManager.isRememberMeEnabled();
    }

    public LiveData<LoginResponse> getProfile() {
        isLoading.setValue(true);
        LiveData<LoginResponse> profileResult = authRepository.getProfile();
        profileResult.observeForever(response -> {
            isLoading.setValue(false);
            loginResponse.setValue(response);
        });
        return profileResult;
    }
}