package com.example.workforcemanagement.ui.admin;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.workforcemanagement.data.model.StatsResponse;
import com.example.workforcemanagement.data.repository.StatsRepository;
import com.example.workforcemanagement.util.TokenManager;

public class StatsViewModel extends AndroidViewModel {
    private final StatsRepository statsRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public StatsViewModel(@NonNull Application application) {
        super(application);
        TokenManager tokenManager = new TokenManager(application.getApplicationContext());
        this.statsRepository = new StatsRepository(tokenManager);
    }

    public LiveData<StatsResponse> getDashboardStats() {
        isLoading.setValue(true);
        LiveData<StatsResponse> statsResult = statsRepository.getDashboardStats();
        statsResult.observeForever(response -> isLoading.setValue(false));
        return statsResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}