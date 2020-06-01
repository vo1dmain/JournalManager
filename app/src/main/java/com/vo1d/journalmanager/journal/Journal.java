package com.vo1d.journalmanager.journal;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "journal_table")
public class Journal {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    //private List<Page> pages = new LinkedList<>();

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

    /*public List<Page> getPages() {
        return pages;
    }*/

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
