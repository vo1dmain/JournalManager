package com.vo1d.journalmanager.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class JournalsViewModel extends AndroidViewModel {
    private JournalsRepository repository;
    private LiveData<List<Journal>> allJournals;

    private LiveData<List<Journal>> selectedJournals = new MutableLiveData<>(new LinkedList<>());

    public JournalsViewModel(@NonNull Application application) {
        super(application);
        repository = new JournalsRepository(application);
        allJournals = repository.getAllJournals();
    }

    public long insert(Journal journal) {
        return repository.insert(journal);
    }

    public void insertPage(Page page) {
        repository.insert(page);
    }

    public void update(Journal journal) {
        repository.update(journal);
    }

    public void delete(Journal... journals) {
        repository.delete(journals);
    }

    public void deleteAllJournals() {
        repository.deleteAllJournals();
    }

    public void deleteSelectedJournals() {
        delete(Objects.requireNonNull(selectedJournals.getValue()).toArray(new Journal[0]));

        selectedJournals.getValue().clear();
    }

    public void addToSelection(Journal journal) {
        Objects.requireNonNull(selectedJournals.getValue()).add(journal);
    }

    public void removeFromSelection(Journal journal) {
        Objects.requireNonNull(selectedJournals.getValue()).remove(journal);
    }

    public void clearSelection() {
        Objects.requireNonNull(selectedJournals.getValue()).clear();
    }

    public LiveData<List<Journal>> getAllJournals() {
        return allJournals;
    }

    public List<Journal> getFilteredJournals(String filter) {
        return repository.getFilteredJournals(filter);
    }

    public LiveData<List<Journal>> getSelectedJournals() {
        return selectedJournals;
    }
}
