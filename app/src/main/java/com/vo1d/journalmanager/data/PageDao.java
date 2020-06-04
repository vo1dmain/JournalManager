package com.vo1d.journalmanager.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
interface PageDao {

    @Insert
    void insert(Page page);

    @Update
    void update(Page page);

    @Delete
    void delete(Page page);

    @Query("SELECT * FROM journal_page_table WHERE journalId=:journalId")
    List<Page> findPagesForJournal(final int journalId);
}
