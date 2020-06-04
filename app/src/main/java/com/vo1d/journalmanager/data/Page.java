package com.vo1d.journalmanager.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "journal_page_table",
        foreignKeys = @ForeignKey(entity = Journal.class, parentColumns = "id", childColumns = "journalId", onDelete = CASCADE),
        indices = {@Index(value = {"id"}, unique = true), @Index(value = "journalId")})
public class Page {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int journalId;
    private String title;

    public Page(int journalId, String title) {
        this.journalId = journalId;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJournalId() {
        return journalId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
