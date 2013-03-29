package com.bigpupdev.synodroid.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;

public class ConfirmDialog {
    public Runnable ans_true = null;
    public Runnable ans_false = null;

    public static Runnable empty = new Runnable(){public void run() {}};
    // Dialog. --------------------------------------------------------------

    public boolean Confirm(FragmentActivity act, String Title, String ConfirmText,
            String CancelBtn, String OkBtn, Runnable aProcedure, Runnable bProcedure) {
        ans_true = aProcedure;
        ans_false= bProcedure;
        AlertDialog dialog = new AlertDialog.Builder(act).create();
        dialog.setTitle(Title);
        dialog.setMessage(ConfirmText);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, OkBtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                         ans_true.run();
                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, CancelBtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        ans_false.run();
                    }
                });
        dialog.show();
        return true;
    }
}