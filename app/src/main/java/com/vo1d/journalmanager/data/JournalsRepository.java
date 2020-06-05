package com.vo1d.journalmanager.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

class JournalsRepository {
    private JournalDao journalDao;
    private PageDao pageDao;
    private LiveData<List<Journal>> allJournals;

    JournalsRepository(Application application) {
        JournalsDatabase database = JournalsDatabase.getInstance(application);
        journalDao = database.journalDao();
        pageDao = database.pageDao();
        allJournals = journalDao.getAllJournals();
    }

    long insert(Journal journal) {
        try {
            InsertJournalAsyncTask task = new InsertJournalAsyncTask(journalDao);
            task.execute(journal);
            return task.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    void insert(Page page) {
        new InsertPageAsyncTask(pageDao).execute(page);
    }

    void update(Journal journal) {
        new UpdateJournalAsyncTask(journalDao).execute(journal);
    }

    void delete(Journal... journals) {
        new DeleteJournalAsyncTask(journalDao).execute(journals);
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

    private static class InsertJournalAsyncTask extends AsyncTask<Journal, Void, Long> {

        private JournalDao journalDao;

        private InsertJournalAsyncTask(JournalDao journalDao) {
            this.journalDao = journalDao;
        }

        @Override
        protected Long doInBackground(Journal... journals) {
            return journalDao.insert(journals[0]);
        }
    }

    private static class InsertPageAsyncTask extends AsyncTask<Page, Void, Void> {

        private PageDao pageDao;

        private InsertPageAsyncTask(PageDao pageDao) {
            this.pageDao = pageDao;
        }

        @Override
        protected Void doInBackground(Page... pages) {
            pageDao.insert(pages[0]);
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
            journalDao.delete(journals);
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
