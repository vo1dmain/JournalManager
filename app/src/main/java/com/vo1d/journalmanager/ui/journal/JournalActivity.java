package com.vo1d.journalmanager.ui.journal;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.vo1d.journalmanager.MainActivity;
import com.vo1d.journalmanager.R;
import com.vo1d.journalmanager.data.Journal;
import com.vo1d.journalmanager.data.Page;
import com.vo1d.journalmanager.data.PagesViewModel;
import com.vo1d.journalmanager.data.PagesViewModelFactory;

import java.util.Objects;

public class JournalActivity extends AppCompatActivity implements CreateNewPageDialog.DialogListener {

    public static final String EXTRA_TITLE =
            "com.vo1d.journalmanager.ui.journal.EXTRA_TITLE";
    public static final String EXTRA_ID =
            "com.vo1d.journalmanager.ui.journal.EXTRA_ID";

    Toolbar toolbar;
    TextInputEditText journalTitle;
    FloatingActionButton fab;
    Menu menu;
    TabLayout tabs;
    ViewPager2 viewPager;
    PagesAdapter pagesAdapter;

    Resources resources;
    Intent data;

    PagesViewModel pViewModel;
    Journal currentJournal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        resources = getResources();

        journalTitle = findViewById(R.id.title);
        fab = findViewById(R.id.fab);
        tabs = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view_pager);
        toolbar = findViewById(R.id.journal_toolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        data = getIntent();

        int id = data.getIntExtra(EXTRA_ID, -1);

        if (id == -1) {
            setResult(RESULT_CANCELED, data);
            finish();
        }

        if (data.hasExtra(EXTRA_TITLE)) {
            setResult(RESULT_OK, data);
            currentJournal = new Journal(data.getStringExtra(EXTRA_TITLE));
            currentJournal.setId(id);

            journalTitle.setText(currentJournal.getTitle());
        }

        pViewModel = new ViewModelProvider(this, new PagesViewModelFactory(this.getApplication(), id)).get(PagesViewModel.class);

        pagesAdapter = new PagesAdapter();

        pViewModel.getAllPages().observe(this, list -> pagesAdapter.submitList(list));

        viewPager.setAdapter(pagesAdapter);

        new TabLayoutMediator(tabs, viewPager, (tab, position)
                -> tab.setText(Objects.requireNonNull(pViewModel.getAllPages().getValue()).get(position).getTitle()))
                .attach();

        fab.setOnClickListener(view
                -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        journalTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(data.getStringExtra(EXTRA_TITLE)).contentEquals(s)) {
                    menu.findItem(R.id.save_journal).setEnabled(false);
                } else if (!s.toString().trim().isEmpty()) {
                    menu.findItem(R.id.save_journal).setEnabled(true);
                } else {
                    menu.findItem(R.id.save_journal).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                saveJournal();
            }
        });

        journalTitle.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.clearFocus();
            }
            return false;
        });
    }

    private void saveJournal() {
        String title = Objects.requireNonNull(journalTitle.getText()).toString();
        currentJournal.setTitle(title);

        Intent data = new Intent();
        data.putExtra(EXTRA_ID, currentJournal.getId());
        data.putExtra(EXTRA_TITLE, currentJournal.getTitle());

        setResult(RESULT_OK, data);

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
                openCreateDialog();
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

    @Override
    public void onDialogPositiveClick(AppCompatDialogFragment dialog) {
        EditText et = Objects.requireNonNull(dialog.getDialog()).findViewById(R.id.title);

        String title = et.getText().toString();

        Page page = new Page(currentJournal.getId(), title);

        pViewModel.insert(page);
    }

    @Override
    public void onDialogNegativeClick(AppCompatDialogFragment dialog) {
    }

    private void openCreateDialog() {
        try {
            DialogFragment newFragment = new CreateNewPageDialog();
            newFragment.show(getSupportFragmentManager(), "Create new page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
