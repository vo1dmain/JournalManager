package com.vo1d.journalmanager.ui.journal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.vo1d.journalmanager.journal.JournalPage;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private List<JournalPage> pages;
    private List<String> tabTitles;
    private List<Fragment> pageFragments;

    SectionsPagerAdapter(FragmentManager fm, List<JournalPage> pages) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        tabTitles = new LinkedList<>();

        this.pages = pages;

        for (JournalPage jp :
                pages) {
            tabTitles.add(jp.getTitle());
        }

        pageFragments = new LinkedList<>(Collections.nCopies(pages.size(), new Fragment()));
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return pageFragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

}