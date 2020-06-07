package com.vo1d.journalmanager.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.vo1d.journalmanager.R;

import java.util.Objects;

public class CreationDialog extends DialogFragment {
    private DialogListener mListener;
    private int titleId;

    public CreationDialog(int titleId) {
        this.titleId = titleId;
    }

    public void setDialogListener(DialogListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(titleId)
                .setView(View.inflate(requireActivity(), R.layout.dialog_create, null))
                .setPositiveButton(R.string.dialog_ok, (dialog, id) -> mListener.onDialogPositiveClick(this))
                .setNegativeButton(R.string.dialog_cancel, (dialog, id) -> mListener.onDialogNegativeClick(this));
        AlertDialog d = builder.create();
        try {
            d.setOnShowListener(dialog -> {
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                EditText et = d.findViewById(R.id.title);

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
        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }
}
