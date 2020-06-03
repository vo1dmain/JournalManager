package com.vo1d.journalmanager.journal;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Journal.class}, version = 2)
abstract class JournalDatabase extends RoomDatabase {

    private static JournalDatabase instance;
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    static synchronized JournalDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    JournalDatabase.class, "journal_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    public abstract JournalDao journalDao();

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private JournalDao journalDao;

        private PopulateDbAsyncTask(JournalDatabase db) {
            journalDao = db.journalDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 1; i <= 10; i++) {
                journalDao.insert(new Journal(Integer.toString(i)));
            }
            return null;
        }
    }

}
