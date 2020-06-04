package com.vo1d.journalmanager.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.widget.RecyclerView;

public class JournalKeyProvider extends ItemKeyProvider<Long> {

    private MainAdapter adapter;
    private RecyclerView recyclerView;

    public JournalKeyProvider(MainAdapter adapter, RecyclerView recyclerView) {
        super(SCOPE_MAPPED);
        this.adapter = adapter;
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public Long getKey(int position) {
        return adapter.getItemId(position);
    }

    @Override
    public int getPosition(@NonNull Long key) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForItemId(key);
        return viewHolder == null ? RecyclerView.NO_POSITION : viewHolder.getLayoutPosition();
    }
}
