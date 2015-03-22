package com.lili.tasks;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerDialogFragment extends DialogFragment {


    public DatePickerDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Bundle args = getArguments();
        Fragment editFragment = getFragmentManager()
                .findFragmentByTag(
                        TaskEditFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) editFragment;
        return new DatePickerDialog(getActivity(), listener,
                args.getInt(TaskEditFragment.YEAR),
                args.getInt(TaskEditFragment.MONTH),
                args.getInt(TaskEditFragment.DAY));
    }

}
