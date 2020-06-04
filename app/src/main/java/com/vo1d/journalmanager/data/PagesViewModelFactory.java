package com.vo1d.journalmanager.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PagesViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private int mParam;


    public PagesViewModelFactory(Application application, int param) {
        mApplication = application;
        mParam = param;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PagesViewModel(mApplication, mParam);
    }
}
