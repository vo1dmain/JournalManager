package com.vo1d.journalmanager.journal;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

class PagesConverter {
    @TypeConverter
    public String fromPages(List<JournalPage> value) {
        String pages;

        pages = new Gson().toJson(value);

        return pages;
    }

    @TypeConverter
    public List<JournalPage> toPages(String value) {
        List<JournalPage> pages;

        Type type = new TypeToken<List<JournalPage>>() {
        }.getType();

        pages = new Gson().fromJson(value, type);

        return pages;
    }
}
