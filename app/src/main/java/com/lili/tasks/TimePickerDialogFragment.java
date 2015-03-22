package com.lili.tasks;


import android.app.Dialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerDialogFragment extends DialogFragment {


    public TimePickerDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Bundle args = getArguments();
        TimePickerDialog.OnTimeSetListener listener = (TimePickerDialog.OnTimeSetListener) getFragmentManager()
                .findFragmentByTag(
                        TaskEditFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        return new TimePickerDialog(getActivity(), listener,
                args.getInt(TaskEditFragment.HOUR),
                args.getInt(TaskEditFragment.MINS), false);
    }
}
