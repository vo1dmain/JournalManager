package com.vo1d.journalmanager.data;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Journal.class, Page.class}, version = 1)
abstract class JournalsDatabase extends RoomDatabase {

    private static JournalsDatabase instance;
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            //new PopulateDbAsyncTask(instance).execute();
        }
    };

    static synchronized JournalsDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    JournalsDatabase.class, "journal_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    public abstract JournalDao journalDao();
    public abstract PageDao pageDao();

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private JournalDao journalDao;
        private PageDao pageDao;

        private PopulateDbAsyncTask(JournalsDatabase db) {
            journalDao = db.journalDao();
            pageDao = db.pageDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 1; i <= 10; i++) {
                Journal j = new Journal(Integer.toString(i));
                long id = journalDao.insert(j);

                Page p = new Page((int) id, j.getTitle() + "1");
                pageDao.insert(p);
            }
            return null;
        }
    }

}
