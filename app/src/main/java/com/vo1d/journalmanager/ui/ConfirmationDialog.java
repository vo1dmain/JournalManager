package com.vo1d.journalmanager.ui;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.vo1d.journalmanager.R;

public class ConfirmationDialog extends DialogFragment {
    private final int TYPE_ID = 0;
    private final int TYPE_STRING = 1;

    private ConfirmationDialog.DialogListener mListener;
    private int titleId;
    private int messageId;
    private String message;

    private int messageType;

    public ConfirmationDialog(int titleId, int messageId) {
        this.titleId = titleId;
        this.messageId = messageId;
        this.messageType = TYPE_ID;
    }

    public ConfirmationDialog(int titleId, String message) {
        this.titleId = titleId;
        this.message = message;
        this.messageType = TYPE_STRING;
    }

    public void setDialogListener(DialogListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(titleId)
                .setPositiveButton(R.string.dialog_ok, (dialog, id) -> mListener.onDialogPositiveClick(this))
                .setNegativeButton(R.string.dialog_cancel, (dialog, id) -> mListener.onDialogNegativeClick(this));

        if (messageType == TYPE_ID) {
            builder.setMessage(messageId);
        } else if (messageType == TYPE_STRING) {
            builder.setMessage(message);
        }

        return builder.create();
    }

    public interface DialogListener {
        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }
}
