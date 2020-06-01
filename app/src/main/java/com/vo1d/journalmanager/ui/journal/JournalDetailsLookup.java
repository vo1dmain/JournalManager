package com.vo1d.journalmanager.ui.journal;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public final class JournalDetailsLookup extends ItemDetailsLookup<Long> {

    private final RecyclerView mRecyclerView;

    public JournalDetailsLookup(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
    }

    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
        View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());

        if (view != null) {
            RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);
            if (holder instanceof JournalAdapter.JournalViewHolder) {
                final JournalAdapter.JournalViewHolder journalHolder = (JournalAdapter.JournalViewHolder) holder;

                return new ItemDetails<Long>() {
                    @Override
                    public int getPosition() {
                        return journalHolder.getAdapterPosition();
                    }

                    @NonNull
                    @Override
                    public Long getSelectionKey() {
                        return journalHolder.getItemId();
                    }
                };
            }
        }
        return null;
    }

}
