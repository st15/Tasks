package com.lili.tasks;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Lili on 22.3.2015 г..
 * <p/>
 * FragmentTransaction ft = getFragmentManager().beginTransaction();
 * DialogFragment newFragment = new AlertDialogFragment();
 * newFragment.show(ft, "alertDialog");
 */
public class AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to save the task?")
                .setTitle("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Perform some action such as saving the item
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
