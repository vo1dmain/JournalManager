package com.vo1d.journalmanager.journal;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.LinkedList;
import java.util.List;

@Entity(tableName = "journal_table")
public class Journal {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @TypeConverters({PagesConverter.class})
    private List<JournalPage> pages = new LinkedList<>();

    public Journal(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<JournalPage> getPages() {
        return pages;
    }

    public void setPages(List<JournalPage> pages) {
        this.pages = pages;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Journal) {
            Journal j2 = ((Journal) obj);

            boolean c1 = (this.id == j2.getId());

            boolean c2 = (this.title.contentEquals(j2.title));

            return c1 && c2;
        } else {
            return obj.equals(this);
        }
    }
}
