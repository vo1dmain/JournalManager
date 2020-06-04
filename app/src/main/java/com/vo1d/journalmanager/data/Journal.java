package com.vo1d.journalmanager.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "journal_table", indices = {@Index(value = {"id"}, unique = true)})
public class Journal {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

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
