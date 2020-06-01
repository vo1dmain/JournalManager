package com.vo1d.journalmanager.journal;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

class JournalRepository {
    private JournalDao journalDao;
    private LiveData<List<Journal>> allJournals;

    JournalRepository(Application application) {
        JournalDatabase database = JournalDatabase.getInstance(application);
        journalDao = database.journalDao();
        allJournals = journalDao.getAllJournals();
    }

    void insert(Journal journal) {
        new InsertJournalAsyncTask(journalDao).execute(journal);
    }

    void update(Journal journal) {
        new UpdateJournalAsyncTask(journalDao).execute(journal);
    }

    void delete(Journal journal) {
        new DeleteJournalAsyncTask(journalDao).execute(journal);
    }

    void deleteAllJournals() {
        new DeleteAllJournalsAsyncTask(journalDao).execute();
    }

    LiveData<List<Journal>> getAllJournals() {
        return allJournals;
    }

    List<Journal> getFilteredJournals(String filter) {
        GetFilteredJournalsAsyncTask task = new GetFilteredJournalsAsyncTask(journalDao);
        task.execute(filter);
        try {
            return task.get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    private static class InsertJournalAsyncTask extends AsyncTask<Journal, Void, Void> {

        private JournalDao journalDao;

        private InsertJournalAsyncTask(JournalDao journalDao) {
            this.journalDao = journalDao;
        }

        @Override
        protected Void doInBackground(Journal... journals) {
            journalDao.insert(journals[0]);
            return null;
        }
    }

    private static class UpdateJournalAsyncTask extends AsyncTask<Journal, Void, Void> {

        private JournalDao journalDao;

        private UpdateJournalAsyncTask(JournalDao journalDao) {
            this.journalDao = journalDao;
        }

        @Override
        protected Void doInBackground(Journal... journals) {
            journalDao.update(journals[0]);
            return null;
        }
    }

    private static class DeleteJournalAsyncTask extends AsyncTask<Journal, Void, Void> {

        private JournalDao journalDao;

        private DeleteJournalAsyncTask(JournalDao journalDao) {
            this.journalDao = journalDao;
        }

        @Override
        protected Void doInBackground(Journal... journals) {
            journalDao.delete(journals[0]);
            return null;
        }
    }

    private static class DeleteAllJournalsAsyncTask extends AsyncTask<Void, Void, Void> {

        private JournalDao journalDao;

        private DeleteAllJournalsAsyncTask(JournalDao journalDao) {
            this.journalDao = journalDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            journalDao.deleteAllJournals();
            return null;
        }
    }

    public static class GetFilteredJournalsAsyncTask extends AsyncTask<String, Void, List<Journal>> {
        private JournalDao journalDao;

        private GetFilteredJournalsAsyncTask(JournalDao journalDao) {
            this.journalDao = journalDao;
        }

        @Override
        protected List<Journal> doInBackground(String... strings) {
            return journalDao.getFilteredJournals(strings[0]);
        }
    }
}