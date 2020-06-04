package com.vo1d.journalmanager.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
interface JournalDao {

    @Insert
    long insert(Journal journal);

    @Update
    void update(Journal journal);

    @Delete
    void delete(Journal... journals);

    @Query("DELETE FROM journal_table")
    void deleteAllJournals();

    @Query("SELECT * FROM journal_table ORDER BY title DESC")
    LiveData<List<Journal>> getAllJournals();

    @Query("SELECT * FROM journal_table WHERE title LIKE '%' || :filter || '%'")
    List<Journal> getFilteredJournals(String filter);
}
