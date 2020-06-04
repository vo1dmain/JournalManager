package com.vo1d.journalmanager.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PagesViewModel extends AndroidViewModel {

    private PagesRepository repository;
    private LiveData<List<Page>> allPages;

    public PagesViewModel(@NonNull Application application, int journalId) {
        super(application);
        repository = new PagesRepository(application, journalId);
        allPages = repository.getAllPages();
    }

    public void insert(Page page) {
        repository.insert(page);
    }

    public void update(Page page) {
        repository.update(page);
    }

    public void delete(Page page) {
        repository.delete(page);
    }

    public LiveData<List<Page>> getAllPages() {
        return allPages;
    }
}
