package com.lili.tasks;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lili.tasks.data.TaskProvider;
import com.lili.tasks.reminder.ReminderManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TaskEditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TaskEditFragment extends Fragment implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";
    //
    // Dialog Constants
    //
    static final String YEAR = "year";
    static final String MONTH = "month";
    static final String DAY = "day";
    static final String HOUR = "hour";
    static final String MINS = "mins";
    static final String CALENDAR = "calendar";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "kk:mm";

    private Calendar mCalendar;
    private EditText mTitleText;
    private EditText mBodyText;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mConfirmButton;
    private long mRowId;

    private OnFragmentInteractionListener mListener;

    public TaskEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mRowId = arguments.getLong(TaskProvider.COLUMN_ROWID);
        }
        // If we're restoring state from a previous activity, restore the
        // previous date as well, otherwise use now
        if (savedInstanceState != null
                && savedInstanceState.containsKey(CALENDAR)) {
            mCalendar = (Calendar) savedInstanceState.getSerializable(CALENDAR);
        } else {
            mCalendar = Calendar.getInstance();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the calendar instance in case the user changed it
        outState.putSerializable(CALENDAR, mCalendar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_task_edit, container, false);
        mTitleText = (EditText) v.findViewById(R.id.title);
        mBodyText = (EditText) v.findViewById(R.id.body);
        mDateButton = (Button) v.findViewById(R.id.task_date);
        mTimeButton = (Button) v.findViewById(R.id.task_time);
        mConfirmButton = (Button) v.findViewById(R.id.confirm);

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(TaskProvider.COLUMN_ROWID, mRowId);
                values.put(TaskProvider.COLUMN_TITLE, mTitleText.getText()
                        .toString());
                values.put(TaskProvider.COLUMN_BODY, mBodyText.getText()
                        .toString());
                values.put(TaskProvider.COLUMN_DATE_TIME,
                        mCalendar.getTimeInMillis());
                if (mRowId == 0) {
                    // add new task
                    Uri itemUri = getActivity().getContentResolver().insert(
                            TaskProvider.CONTENT_URI, values);

                    mRowId = ContentUris.parseId(itemUri);
                } else {
                    // edit task
                    int count = getActivity().getContentResolver().update(
                            ContentUris.withAppendedId(
                                    TaskProvider.CONTENT_URI, mRowId),
                            values, null, null);
                    if (count != 1)
                        throw new IllegalStateException("Unable to update "
                                + mRowId);
                }
                Toast.makeText(getActivity(),
                        getString(R.string.toast_task_saved),
                        Toast.LENGTH_SHORT).show();
                mListener.onFinishEditing();

                new ReminderManager(getActivity()).setReminder(mRowId,
                        mCalendar);
            }
        });

        setHasOptionsMenu(true);

        // load data
        if (mRowId == 0) {
            // This is a new task - add defaults from preferences if set.

            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            String defaultTitleKey = getString(R.string.pref_task_title_key);
            String defaultTimeKey = getString(R.string.pref_default_time_from_now_key);

            String defaultTitle = prefs.getString(defaultTitleKey, null);
            String defaultTime = prefs.getString(defaultTimeKey, null);

            if (defaultTitle != null)
                mTitleText.setText(defaultTitle);

            if (defaultTime != null && defaultTime.length() > 0)
                mCalendar.add(Calendar.MINUTE, Integer.parseInt(defaultTime));

            updateButtons();

        } else {

            // Fire off a background loader to retrieve the data from the
            // database
            getLoaderManager().initLoader(0, null, this);

        }
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_task_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // delete task button
            case R.id.action_delete:
                if (mRowId != 0) {
                    // if editing already existing task -> delete task
                    int count = getActivity().getContentResolver().delete(
                            ContentUris.withAppendedId(TaskProvider.CONTENT_URI,
                                    mRowId), null, null);

                    if (count == 1)
                        Toast.makeText(getActivity(), R.string.toast_task_deleted,
                                Toast.LENGTH_SHORT).show();
                    else
                        throw new IllegalStateException("Unable to delete " + mRowId);
                }

                // let the listener know that the editing is finished
                mListener.onFinishEditing();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = new DatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, mCalendar.get(Calendar.YEAR));
        args.putInt(MONTH, mCalendar.get(Calendar.MONTH));
        args.putInt(DAY, mCalendar.get(Calendar.DAY_OF_MONTH));
        newFragment.setArguments(args);
        newFragment.show(ft, "datePicker");
    }

    private void showTimePicker() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = new TimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(HOUR, mCalendar.get(Calendar.HOUR_OF_DAY));
        args.putInt(MINS, mCalendar.get(Calendar.MINUTE));
        newFragment.setArguments(args);
        newFragment.show(ft, "timePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateButtons();
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute);
        updateButtons();
    }

    private void updateButtons() {
        // Set the time button text
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        String timeForButton = timeFormat.format(mCalendar.getTime());
        mTimeButton.setText(timeForButton);
        // Set the date button text
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String dateForButton = dateFormat.format(mCalendar.getTime());
        mDateButton.setText(dateForButton);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return new CursorLoader(getActivity(), ContentUris.withAppendedId(
                TaskProvider.CONTENT_URI, mRowId),
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor reminder) {
        // Close this fragment down if the item we're editing was deleted
        if (reminder.getCount() == 0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    TaskEditFragment.this.mListener.onFinishEditing();
                }
            });
            return;
        }

        mTitleText.setText(reminder.getString(reminder
                .getColumnIndexOrThrow(TaskProvider.COLUMN_TITLE)));
        mBodyText.setText(reminder.getString(reminder
                .getColumnIndexOrThrow(TaskProvider.COLUMN_BODY)));

        // Get the date from the database
        Long dateInMillis = reminder.getLong(reminder
                .getColumnIndexOrThrow(TaskProvider.COLUMN_DATE_TIME));
        Date date = new Date(dateInMillis);
        mCalendar.setTime(date);

        updateButtons();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // nothing to reset for this fragment
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        public void onFinishEditing();
    }

}
