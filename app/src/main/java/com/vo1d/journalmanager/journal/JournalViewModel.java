package com.vo1d.journalmanager.journal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class JournalViewModel extends AndroidViewModel {
    private JournalRepository repository;
    private LiveData<List<Journal>> allJournals;

    private LiveData<List<Journal>> selectedJournals = new MutableLiveData<>(new LinkedList<>());

    public JournalViewModel(@NonNull Application application) {
        super(application);
        repository = new JournalRepository(application);
        allJournals = repository.getAllJournals();
    }

    public void insert(Journal journal) {
        repository.insert(journal);
    }

    public void update(Journal journal) {
        repository.update(journal);
    }

    public void delete(Journal journal) {
        repository.delete(journal);
    }

    public void deleteAllJournals() {
        repository.deleteAllJournals();
    }

    public void deleteSelectedJournals() {
        for (Journal j :
                Objects.requireNonNull(selectedJournals.getValue())) {
            delete(j);
        }

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
