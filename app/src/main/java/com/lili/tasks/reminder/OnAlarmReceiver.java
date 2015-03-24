package com.lili.tasks.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lili.tasks.data.TaskProvider;

/**
 * Created by Lili on 22.3.2015 г..
 */
public class OnAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long rowid =
                intent.getExtras().getLong(TaskProvider.COLUMN_ROWID);
        WakeReminderIntentService.acquireStaticLock(context);
        Intent i = new Intent(context, ReminderService.class);
        i.putExtra(TaskProvider.COLUMN_ROWID, rowid);
        context.startService(i);
    }
}
