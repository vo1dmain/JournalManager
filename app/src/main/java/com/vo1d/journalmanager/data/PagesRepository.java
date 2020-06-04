package com.vo1d.journalmanager.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

class PagesRepository {
    private PageDao pageDao;
    private LiveData<List<Page>> allPages;

    PagesRepository(Application application, int journalId) {
        JournalsDatabase database = JournalsDatabase.getInstance(application);
        pageDao = database.pageDao();
        allPages = pageDao.findPagesForJournal(journalId);
    }

    void insert(Page page) {
        new InsertPageAsyncTask(pageDao).execute(page);
    }

    void update(Page page) {
        new UpdatePageAsyncTask(pageDao).execute(page);
    }

    void delete(Page page) {
        new DeletePageAsyncTask(pageDao).execute(page);
    }

    LiveData<List<Page>> getAllPages() {
        return allPages;
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

    private static class UpdatePageAsyncTask extends AsyncTask<Page, Void, Void> {

        private PageDao pageDao;

        private UpdatePageAsyncTask(PageDao pageDao) {
            this.pageDao = pageDao;
        }

        @Override
        protected Void doInBackground(Page... pages) {
            pageDao.update(pages[0]);
            return null;
        }
    }

    private static class DeletePageAsyncTask extends AsyncTask<Page, Void, Void> {

        private PageDao pageDao;

        private DeletePageAsyncTask(PageDao pageDao) {
            this.pageDao = pageDao;
        }

        @Override
        protected Void doInBackground(Page... pages) {
                pageDao.delete(pages[0]);
            return null;
        }
    }
}
