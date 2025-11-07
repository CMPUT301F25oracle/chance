package com.example.chance.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Helper for showing alert and confirmation dialogs.
 */
public class DialogUtils {

    /**
     * Shows a simple alert dialog with an OK button.
     */
    public static void showAlert(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * Shows a confirmation dialog with custom actions.
     */
    public static void showConfirmDialog(Context context, String title, String message,
                                         String positiveText, DialogInterface.OnClickListener positiveAction,
                                         String negativeText, DialogInterface.OnClickListener negativeAction) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, positiveAction)
                .setNegativeButton(negativeText, negativeAction)
                .show();
    }

    /**
     * Shows an error dialog (common across activities).
     */
    public static void showError(Context context, String message) {
        showAlert(context, "Error", message);
    }
}
