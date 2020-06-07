package com.vo1d.journalmanager.ui.journal;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.vo1d.journalmanager.ui.ConfirmationDialog;
import com.vo1d.journalmanager.ui.CreationDialog;

import java.util.Objects;

public class JournalActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE =
            "com.vo1d.journalmanager.ui.journal.EXTRA_TITLE";
    public static final String EXTRA_ID =
            "com.vo1d.journalmanager.ui.journal.EXTRA_ID";
    private static final int DELETE_JOURNAL = 0;
    private static final int DELETE_PAGE = 1;

    Toolbar toolbar;
    TextInputEditText journalTitle;
    FloatingActionButton fab;
    Menu menu;
    TabLayout tabLayout;
    ViewPager2 vp;
    PagesAdapter adapter;
    LinearLayout tabStrip;
    View chosenTab;

    MenuItem save;

    Resources resources;
    Intent data;

    PagesViewModel pViewModel;
    Journal currentJournal;

    Page currentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        resources = getResources();

        journalTitle = findViewById(R.id.title);
        fab = findViewById(R.id.fab);
        tabLayout = findViewById(R.id.tabs);
        vp = findViewById(R.id.view_pager);
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

        tabStrip = (LinearLayout) tabLayout.getChildAt(0);

        pViewModel = new ViewModelProvider(this, new PagesViewModelFactory(this.getApplication(), id)).get(PagesViewModel.class);

        adapter = new PagesAdapter();

        pViewModel.getAllPages().observe(this, list -> {
            adapter.submitList(list);
            for (int i = 0; i < tabStrip.getChildCount(); i++) {
                registerForContextMenu(tabStrip.getChildAt(i));
                tabStrip.getChildAt(i).setTag(list.get(i));
            }
        });

        vp.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, vp, (tab, position) ->
                tab.setText(
                        Objects.requireNonNull(pViewModel.getAllPages().getValue()).get(position).getTitle()
                )
        ).attach();

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
                    save.setEnabled(false);
                } else if (!s.toString().trim().isEmpty()) {
                    save.setEnabled(true);
                } else {
                    save.setEnabled(false);
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_context, menu);

        currentTag = (Page) v.getTag();
        chosenTab = v;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_page:
                openConfirmationDialog(DELETE_PAGE);
                return true;
            case R.id.rename_page:
                //TODO: add code
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_journal, menu);

        this.menu = toolbar.getMenu();
        save = this.menu.findItem(R.id.save_journal);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_page:
                openCreationDialog();
                return true;
            case R.id.save_journal:
                saveJournal();
                String mes = resources.getString(R.string.journal_update_success_message, currentJournal.getTitle());
                Snackbar.make(vp, mes, Snackbar.LENGTH_LONG).show();
                return true;
            case R.id.delete_journal:
                openConfirmationDialog(DELETE_JOURNAL);
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

    private void openCreationDialog() {
        CreationDialog creationDialog = new CreationDialog(R.string.create_page_dialog_title);

        creationDialog.setDialogListener(new CreationDialog.DialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                EditText et = Objects.requireNonNull(dialog.getDialog()).findViewById(R.id.title);

                String title = et.getText().toString();

                Page page = new Page(currentJournal.getId(), title);

                pViewModel.insert(page);
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                dialog.dismiss();
            }
        });

        creationDialog.show(getSupportFragmentManager(), "Create new page");
    }

    private void openConfirmationDialog(int confirmationType) {
        if (confirmationType == DELETE_JOURNAL) {
            ConfirmationDialog confirmationDialog = new ConfirmationDialog(R.string.delete_this_journal_confirm_title, R.string.delete_journal_confirm_message);

            confirmationDialog.setDialogListener(new ConfirmationDialog.DialogListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    deleteJournal();
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });

            confirmationDialog.show(getSupportFragmentManager(), "Delete journal confirmation");
        } else if (confirmationType == DELETE_PAGE) {

            ConfirmationDialog confirmationDialog = new ConfirmationDialog(R.string.delete_page_confirm_title, R.string.delete_page_confirm_message);

            confirmationDialog.setDialogListener(new ConfirmationDialog.DialogListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    unregisterForContextMenu(chosenTab);
                    pViewModel.delete(currentTag);
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });

            confirmationDialog.show(getSupportFragmentManager(), "Delete page confirmation");
        }
    }
}
