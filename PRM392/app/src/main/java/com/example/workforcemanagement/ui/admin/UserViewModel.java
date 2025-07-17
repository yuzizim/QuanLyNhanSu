package com.example.workforcemanagement.ui.admin;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.workforcemanagement.data.model.UsersResponse;
import com.example.workforcemanagement.data.model.User;
import com.example.workforcemanagement.data.repository.UserRepository;
import com.example.workforcemanagement.util.TokenManager;
import java.util.Map;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<String> filter = new MutableLiveData<>("all");

    public UserViewModel(@NonNull Application application) {
        super(application);
        TokenManager tokenManager = new TokenManager(application.getApplicationContext());
        this.userRepository = new UserRepository(tokenManager);
    }

    public LiveData<UsersResponse> getUsers(int page, int limit) {
        isLoading.setValue(true);
        LiveData<UsersResponse> usersResult = userRepository.getUsers(
                searchQuery.getValue(),
                filter.getValue(),
                page,
                limit
        );
        usersResult.observeForever(response -> isLoading.setValue(false));
        return usersResult;
    }

    public LiveData<Boolean> createUser(User user) {
        isLoading.setValue(true);
        LiveData<Boolean> result = userRepository.createUser(user);
        result.observeForever(success -> isLoading.setValue(false));
        return result;
    }

    public LiveData<Boolean> updateUser(int id, Map<String, Object> updateFields) {
        isLoading.setValue(true);
        LiveData<Boolean> result = userRepository.updateUser(id, updateFields);
        result.observeForever(success -> isLoading.setValue(false));
        return result;
    }

    public LiveData<Boolean> deleteUser(int id) {
        isLoading.setValue(true);
        LiveData<Boolean> result = userRepository.deleteUser(id);
        result.observeForever(success -> isLoading.setValue(false));
        return result;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<String> getFilter() {
        return filter;
    }

    public void setFilter(String filterType) {
        filter.setValue(filterType);
    }
}