package com.vo1d.journalmanager;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.SelectionTracker.SelectionObserver;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.vo1d.journalmanager.data.Journal;
import com.vo1d.journalmanager.data.JournalsViewModel;
import com.vo1d.journalmanager.data.Page;
import com.vo1d.journalmanager.ui.ConfirmationDialog;
import com.vo1d.journalmanager.ui.CreationDialog;
import com.vo1d.journalmanager.ui.journal.JournalActivity;
import com.vo1d.journalmanager.ui.main.JournalDetailsLookup;
import com.vo1d.journalmanager.ui.main.JournalKeyProvider;
import com.vo1d.journalmanager.ui.main.MainAdapter;

import java.util.List;
import java.util.Objects;


public class MainActivity
        extends AppCompatActivity {

    public static final int OPEN_JOURNAL_REQUEST = 1;
    public static final int RESULT_DELETE = 2;
    private static final int DELETE_ALL = 0;
    private static final int DELETE_SELECTED = 1;

    private Resources resources;
    private JournalsViewModel viewModel;

    private MainAdapter adapter;
    private SelectionTracker<Long> tracker;

    private ActionMode actionMode;
    private RecyclerView recyclerView;
    private ExtendedFloatingActionButton buttonCreate;
    private TextView nothingFoundTextView;
    private TextView listIsEmptyTextView;
    private MenuItem deleteAll;

    private int visibility;

    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_main_selection, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(resources.getString(R.string.selected_count, tracker.getSelection().size()));
            buttonCreate.setVisibility(View.GONE);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.select_all:
                    tracker.setItemsSelected(adapter.getAllIds(), true);
                    return true;
                case R.id.delete_selected:
                    openConfirmationDialog(DELETE_SELECTED);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            tracker.clearSelection();
            viewModel.clearSelection();
            actionMode = null;
            buttonCreate.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            resources = getResources();
            adapter = new MainAdapter();
            viewModel = new ViewModelProvider(this).get(JournalsViewModel.class);

            recyclerView = findViewById(R.id.recycler_view);
            buttonCreate = findViewById(R.id.button_create_journal);
            nothingFoundTextView = findViewById(R.id.nothing_found_message);
            listIsEmptyTextView = findViewById(R.id.list_is_empty);

            LinearLayoutManager llm = new LinearLayoutManager(this);

            recyclerView.setLayoutManager(llm);
            recyclerView.setAdapter(adapter);

            recyclerView.setHasFixedSize(true);

            buttonCreate.setOnClickListener(view -> openCreationDialog());

            adapter.setOnItemClickListener(journal -> {
                Intent intent = new Intent(MainActivity.this, JournalActivity.class);
                intent.putExtra(JournalActivity.EXTRA_TITLE, journal.getTitle());
                intent.putExtra(JournalActivity.EXTRA_ID, journal.getId());

                tracker.clearSelection();

                startActivityForResult(intent, OPEN_JOURNAL_REQUEST);
            });

            adapter.setOnSelectionChangedListener((journal, isChecked) -> {
                if (isChecked) {
                    viewModel.addToSelection(journal);
                } else {
                    viewModel.removeFromSelection(journal);
                }
            });

            viewModel.getAllJournals().observe(this, adapter::submitList);

            viewModel.getAllJournals().observe(this, journals -> {
                if (journals.size() == 0) {
                    if (actionMode != null) {
                        actionMode.finish();
                    }
                    recyclerView.setVisibility(View.GONE);
                    listIsEmptyTextView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    listIsEmptyTextView.setVisibility(View.GONE);
                }
            });

            tracker = new SelectionTracker.Builder<>(
                    "selectedJournalsTrackerId",
                    recyclerView,
                    new JournalKeyProvider(adapter, recyclerView),
                    new JournalDetailsLookup(recyclerView),
                    StorageStrategy.createLongStorage()
            )
                    .withSelectionPredicate(SelectionPredicates.createSelectAnything())
                    .build();

            tracker.addObserver(new SelectionObserver<Long>() {
                @Override
                public void onSelectionChanged() {
                    if (tracker.hasSelection()) {
                        if (actionMode == null) {
                            actionMode = startSupportActionMode(callback);
                        } else {
                            actionMode.setTitle(resources.getString(R.string.selected_count, tracker.getSelection().size()));
                        }
                    } else if (!tracker.hasSelection()) {
                        if (actionMode != null) {
                            actionMode.finish();
                        }
                    }
                }
            });

            adapter.setTracker(tracker);

            viewModel.getSelectedJournals().observe(this, list -> {
                if (list.isEmpty()) {
                    tracker.clearSelection();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == OPEN_JOURNAL_REQUEST) {
            if (resultCode == RESULT_DELETE) {
                assert data != null;

                String title = data.getStringExtra(JournalActivity.EXTRA_TITLE);
                int id = data.getIntExtra(JournalActivity.EXTRA_ID, -1);

                if (id == -1) {
                    String mes = resources.getString(R.string.journal_delete_failure_message, title);
                    Snackbar.make(recyclerView, mes, Snackbar.LENGTH_LONG).show();
                    return;
                }

                Journal journal = new Journal("");
                journal.setId(id);

                viewModel.delete(journal);

                String mes = resources.getString(R.string.journal_delete_success_message, title);
                Snackbar.make(recyclerView, mes, Snackbar.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                assert data != null;
                String title = data.getStringExtra(JournalActivity.EXTRA_TITLE);
                String mes = resources.getString(R.string.journal_update_failure_message, title);
                Snackbar.make(recyclerView, mes, Snackbar.LENGTH_LONG).show();
                return;
            } else if (resultCode == RESULT_OK) {
                assert data != null;
                Journal j = new Journal(data.getStringExtra(JournalActivity.EXTRA_TITLE));
                j.setId(data.getIntExtra(JournalActivity.EXTRA_ID, -1));

                viewModel.update(j);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.search_action);
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                viewModel.getAllJournals().removeObserver(adapter::submitList);
                buttonCreate.setVisibility(View.GONE);
                visibility = listIsEmptyTextView.getVisibility();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                viewModel.getAllJournals().observe(MainActivity.this, adapter::submitList);
                buttonCreate.setVisibility(View.VISIBLE);
                nothingFoundTextView.setVisibility(View.GONE);
                return true;
            }
        });

        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    adapter.submitList(viewModel.getAllJournals().getValue());
                    nothingFoundTextView.setVisibility(View.GONE);
                    listIsEmptyTextView.setVisibility(visibility);
                } else {
                    List<Journal> filteredList = viewModel.getFilteredJournals(newText);
                    adapter.submitList(filteredList);
                    listIsEmptyTextView.setVisibility(View.GONE);
                    if (filteredList.size() == 0) {
                        nothingFoundTextView.setVisibility(View.VISIBLE);
                    } else {
                        nothingFoundTextView.setVisibility(View.GONE);
                    }
                }
                return true;
            }
        });

        deleteAll = menu.findItem(R.id.delete_all_journals);

        viewModel.getAllJournals().observe(this, journals -> {
            if (journals.size() == 0) {
                deleteAll.setEnabled(false);
            } else {
                deleteAll.setEnabled(true);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_journals:
                openConfirmationDialog(DELETE_ALL);
                return true;
            case R.id.show_about:
                //TODO: add about page
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tracker.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        tracker.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    private void creationDialogPositiveClick(@NonNull DialogFragment dialog) {
        EditText et = Objects.requireNonNull(dialog.getDialog()).findViewById(R.id.new_element_title);

        String title = et.getText().toString();

        Journal journal = new Journal(title);
        int id = (int) viewModel.insert(journal);

        Page page = new Page(id, resources.getString(R.string.page_one_default_title));

        viewModel.insertPage(page);

        String mes = resources.getString(R.string.journal_create_success_message, title);
        Snackbar.make(recyclerView, mes, Snackbar.LENGTH_LONG).show();
    }

    private void openCreationDialog() {
        CreationDialog creationDialog = new CreationDialog();

        creationDialog.setDialogListener(new CreationDialog.DialogListener() {

            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                creationDialogPositiveClick(dialog);
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                dialog.dismiss();
            }
        });

        creationDialog.show(getSupportFragmentManager(), "Create new journal");
    }

    private void openConfirmationDialog(int confirmationType) {
        if (confirmationType == DELETE_ALL) {
            ConfirmationDialog confirmationDialog = new ConfirmationDialog(R.string.delete_journals_confirm_title, R.string.delete_all_confirm_message);

            confirmationDialog.setDialogListener(new ConfirmationDialog.DialogListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    viewModel.deleteAllJournals();
                    Snackbar.make(recyclerView, R.string.all_journals_deleted_message, Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });

            confirmationDialog.show(getSupportFragmentManager(), "Delete selected confirmation");
        } else if (confirmationType == DELETE_SELECTED) {
            int count = tracker.getSelection().size();

            String message = resources.getString(R.string.delete_selected_confirm_message, count);

            ConfirmationDialog confirmationDialog = new ConfirmationDialog(R.string.delete_journals_confirm_title, message);

            String snackbarMes;
            if (count == 1) {
                String title = Objects.requireNonNull(viewModel.getSelectedJournals().getValue()).get(0).getTitle();
                snackbarMes = resources.getString(R.string.journal_delete_success_message, title);
            } else if (count == Objects.requireNonNull(viewModel.getAllJournals().getValue()).size()) {
                snackbarMes = resources.getString(R.string.all_journals_deleted_message);
            } else {
                snackbarMes = resources.getString(R.string.selected_journals_delete_message, count);
            }

            confirmationDialog.setDialogListener(new ConfirmationDialog.DialogListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    viewModel.deleteSelectedJournals();
                    actionMode.finish();
                    Snackbar.make(recyclerView, snackbarMes, Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });

            confirmationDialog.show(getSupportFragmentManager(), "Delete selected confirmation");
        }
    }
}