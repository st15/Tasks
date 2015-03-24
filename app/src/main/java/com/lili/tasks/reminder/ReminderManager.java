package com.lili.tasks.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lili.tasks.data.TaskProvider;

import java.util.Calendar;

/**
 * Created by Lili on 22.3.2015 г..
 */
public class ReminderManager {

    private Context mContext;
    private AlarmManager mAlarmManager;

    public ReminderManager(Context context) {
        mContext = context;
        mAlarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setReminder(Long taskId, Calendar when) {
        Intent i = new Intent(mContext, OnAlarmReceiver.class);
        i.putExtra(TaskProvider.COLUMN_ROWID, (long) taskId);
        PendingIntent pi =
                PendingIntent.getBroadcast(mContext, 0, i,
                        PendingIntent.FLAG_ONE_SHOT);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pi);
    }
}
