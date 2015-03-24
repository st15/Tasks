package com.lili.tasks.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.lili.tasks.data.TaskProvider;

import java.util.Calendar;

/**
 * Created by Lili on 22.3.2015 г..
 */
public class OnBootReceiver extends BroadcastReceiver {
    private static final String TAG = OnBootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        ReminderManager reminderMgr = new ReminderManager(context);
        Cursor cursor = context.getContentResolver().query(
                TaskProvider.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int rowIdColumnIndex = cursor
                    .getColumnIndex(TaskProvider.COLUMN_ROWID);
            int dateTimeColumnIndex = cursor
                    .getColumnIndex(TaskProvider.COLUMN_DATE_TIME);
            while (cursor.isAfterLast() == false) {
                long rowId = cursor.getLong(rowIdColumnIndex);
                long dateTime = cursor.getLong(dateTimeColumnIndex);
                Calendar cal = Calendar.getInstance();
                cal.setTime(new java.util.Date(dateTime));
                reminderMgr.setReminder(rowId, cal);
                cursor.moveToNext();
            }
            cursor.close();
        }
    }
}
