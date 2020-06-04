package com.vo1d.journalmanager.ui.journal;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.vo1d.journalmanager.MainActivity;
import com.vo1d.journalmanager.R;
import com.vo1d.journalmanager.data.Journal;
import com.vo1d.journalmanager.data.JournalsViewModel;

import java.util.Objects;

public class JournalActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE =
            "com.vo1d.journalmanager.ui.journal.EXTRA_TITLE";
    public static final String EXTRA_ID =
            "com.vo1d.journalmanager.ui.journal.EXTRA_ID";

    Toolbar toolbar;
    TextInputEditText journalTitle;
    FloatingActionButton fab;
    Menu menu;
    TabLayout tabs;
    ViewPager viewPager;
    SectionsPagerAdapter sectionsPagerAdapter;

    Resources resources;

    JournalsViewModel viewModel;
    Journal currentJournal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        resources = getResources();
        viewModel = new ViewModelProvider(this).get(JournalsViewModel.class);

        journalTitle = findViewById(R.id.journal_title);
        fab = findViewById(R.id.fab);
        tabs = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view_pager);
        toolbar = findViewById(R.id.journal_toolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        int id = intent.getIntExtra(EXTRA_ID, -1);

        if (id == -1) {
            setResult(RESULT_CANCELED, intent);
            finish();
        }

        if (intent.hasExtra(EXTRA_TITLE)) {
            setResult(RESULT_OK);
            currentJournal = new Journal(intent.getStringExtra(EXTRA_TITLE));
            currentJournal.setId(id);

            journalTitle.setText(currentJournal.getTitle());
        }

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);

        tabs.setupWithViewPager(viewPager);

        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        journalTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(intent.getStringExtra(EXTRA_TITLE)).contentEquals(s)) {
                    menu.findItem(R.id.save_journal).setEnabled(false);
                } else if (!s.toString().trim().isEmpty()) {
                    menu.findItem(R.id.save_journal).setEnabled(true);
                } else {
                    menu.findItem(R.id.save_journal).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void addNewPage() {
    }

    private void saveJournal() {
        String title = Objects.requireNonNull(journalTitle.getText()).toString();
        currentJournal.setTitle(title);

        viewModel.update(currentJournal);
        menu.findItem(R.id.save_journal).setEnabled(false);
    }

    private void deleteJournal() {
        String title = Objects.requireNonNull(journalTitle.getText()).toString();

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        int id = getIntent().getIntExtra(EXTRA_ID, -1);

        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        setResult(MainActivity.RESULT_DELETE, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_journal, menu);

        this.menu = toolbar.getMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_page:
                addNewPage();
                return true;
            case R.id.save_journal:
                saveJournal();
                String mes = resources.getString(R.string.journal_update_success_message, currentJournal.getTitle());
                Snackbar.make(viewPager, mes, Snackbar.LENGTH_LONG).show();
                return true;
            case R.id.delete_journal:
                deleteJournal();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        saveJournal();
        super.onDestroy();
    }
}
