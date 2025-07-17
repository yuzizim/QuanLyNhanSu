package com.example.workforcemanagement.ui.forgotpassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.workforcemanagement.data.repository.AuthRepository;

public class PasswordViewModel extends ViewModel {
    private AuthRepository repository;
    private MutableLiveData<Boolean> requestResetSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> resetSuccess = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public PasswordViewModel() {
        repository = new AuthRepository(); // Dùng constructor không tham số
    }

    public void requestPasswordReset(String email) {
        repository.requestPasswordReset(email, requestResetSuccess, errorMessage);
    }

    public void resetPassword(String email, String otp, String newPassword) {
        repository.resetPassword(email, otp, newPassword, resetSuccess, errorMessage);
    }

    public LiveData<Boolean> getRequestResetSuccess() {
        return requestResetSuccess;
    }

    public LiveData<Boolean> getResetSuccess() {
        return resetSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
