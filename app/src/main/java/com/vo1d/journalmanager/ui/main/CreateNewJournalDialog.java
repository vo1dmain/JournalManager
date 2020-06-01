package com.vo1d.journalmanager.ui.main;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.vo1d.journalmanager.R;

import java.util.Objects;

public class CreateNewJournalDialog extends AppCompatDialogFragment {
    private DialogListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(requireActivity().getLayoutInflater().inflate(R.layout.dialog_create_journal, null))
                .setTitle(R.string.create_journal_dialog_title)
                .setPositiveButton(R.string.create_journal_dialog_positive, (dialog, id) -> mListener.onDialogPositiveClick(this))
                .setNegativeButton(R.string.create_journal_dialog_negative, (dialog, id) -> mListener.onDialogNegativeClick(this));
        AlertDialog d = builder.create();
        try {
            d.setOnShowListener(dialog -> {
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                EditText et = d.findViewById(R.id.journal_title);

                assert et != null;
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!s.toString().trim().isEmpty()) {
                            d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        } else {
                            d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            });

            Objects.requireNonNull(d.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    public interface DialogListener {
        void onDialogPositiveClick(AppCompatDialogFragment dialog);

        void onDialogNegativeClick(AppCompatDialogFragment dialog);
    }
}
