package com.vo1d.journalmanager.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.vo1d.journalmanager.R;
import com.vo1d.journalmanager.data.Journal;

import java.util.LinkedList;
import java.util.List;

public class JournalAdapter extends ListAdapter<Journal, JournalAdapter.JournalViewHolder> {
    private static final DiffUtil.ItemCallback<Journal> DIFF_CALLBACK = new DiffUtil.ItemCallback<Journal>() {
        @Override
        public boolean areItemsTheSame(@NonNull Journal oldItem, @NonNull Journal newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Journal oldItem, @NonNull Journal newItem) {
            return oldItem.equals(newItem);
        }
    };

    private OnItemClickListener itemClickListener;
    private OnSelectionChangedListener selectionChangedListener;

    private SelectionTracker<Long> tracker;

    public JournalAdapter() {
        super(DIFF_CALLBACK);
        setHasStableIds(true);
    }

    @NonNull
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_journal, parent, false);

        return new JournalViewHolder(itemView, itemClickListener, selectionChangedListener);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        selectionChangedListener = listener;
    }

    public void setTracker(SelectionTracker<Long> tracker) {
        this.tracker = tracker;
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        Journal current = getItem(position);

        holder.textViewTitle.setText(current.getTitle());

        if (tracker != null) {
            if (tracker.hasSelection()) {
                holder.checkBox.setChecked(tracker.isSelected(getItemId(holder.getAdapterPosition())));
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public List<Long> getAllIds() {
        List<Long> list = new LinkedList<>();

        for (int i = 0; i < getItemCount(); i++) {
            list.add(getItemId(i));
        }

        return list;
    }

    public interface OnItemClickListener {
        void onItemClick(Journal journal);
    }

    public interface OnSelectionChangedListener {
        void onSelectionChanged(Journal journal, boolean isChecked);
    }

    class JournalViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private CheckBox checkBox;

        JournalViewHolder(@NonNull View itemView, final OnItemClickListener cListener, final OnSelectionChangedListener scListener) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.title);
            checkBox = itemView.findViewById(R.id.checkBox);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (cListener != null && position != RecyclerView.NO_POSITION) {
                    cListener.onItemClick(getItem(position));
                }
            });

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (buttonView != null && position != RecyclerView.NO_POSITION) {
                    scListener.onSelectionChanged(getItem(position), isChecked);
                }
            });


            tracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
                @Override
                public void onSelectionChanged() {
                    if (tracker.hasSelection()) {
                        checkBox.setVisibility(View.VISIBLE);
                    } else if (!tracker.hasSelection()) {
                        checkBox.setChecked(false);
                        checkBox.setVisibility(View.GONE);
                    }
                }
            });

        }
    }
}
