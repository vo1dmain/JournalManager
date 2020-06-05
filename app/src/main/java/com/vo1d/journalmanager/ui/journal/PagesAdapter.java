package com.vo1d.journalmanager.ui.journal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vo1d.journalmanager.R;
import com.vo1d.journalmanager.data.Page;

import java.util.LinkedList;
import java.util.List;

public class PagesAdapter extends RecyclerView.Adapter<PagesAdapter.PageViewHolder> {

    List<Page> pages = new LinkedList<>();

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_view, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    public void submitList(List<Page> pages) {
        this.pages = pages;
        notifyDataSetChanged();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}