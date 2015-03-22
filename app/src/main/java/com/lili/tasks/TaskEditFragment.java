package com.lili.tasks;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TaskEditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TaskEditFragment extends Fragment implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

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
        mDateButton = (Button) v.findViewById(R.id.reminder_date);
        mTimeButton = (Button) v.findViewById(R.id.reminder_time);
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
                    Uri itemUri = getActivity().getContentResolver().insert(
                            TaskProvider.CONTENT_URI, values);
                    mRowId = ContentUris.parseId(itemUri);
                } else {
                    int count = getActivity().getContentResolver().update(
                            ContentUris.withAppendedId(
                                    TaskProvider.CONTENT_URI, mRowId),
                            values, null, null);
                    if (count != 1)
                        throw new IllegalStateException("Unable to update "
                                + mRowId);
                }
                Toast.makeText(getActivity(),
                        getString(R.string.task_saved_message),
                        Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });


        return v;

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
