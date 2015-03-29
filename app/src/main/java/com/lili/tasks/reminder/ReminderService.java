package com.lili.tasks.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.lili.tasks.R;
import com.lili.tasks.TaskEditActivity;
import com.lili.tasks.data.TaskProvider;

/**
 * Created by Lili on 22.3.2015 г..
 */
public class ReminderService extends WakeReminderIntentService {
    public ReminderService() {
        super("ReminderService");
    }

    @Override
    void doReminderWork(Intent intent) {
        Long rowId = intent.getExtras()
                .getLong(TaskProvider.COLUMN_ROWID);
        // Status bar notification Code Goes here.
        NotificationManager mgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, TaskEditActivity.class);
        notificationIntent.putExtra(TaskProvider.COLUMN_ROWID, rowId);
        PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification note = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.notify_new_task_title))
                .setContentText(getString(R.string.notify_new_task_message))
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentIntent(pi)
                .setTicker(getString(R.string.notify_new_task_message))
                .setWhen(System.currentTimeMillis())
                .build();

        note.defaults |= Notification.DEFAULT_SOUND;
        note.flags |= Notification.FLAG_AUTO_CANCEL;
        // An issue can occur if the user enters more than 2,147,483,647 tasks (the maximum int value).
        // Unlikely, but good to note.
        int id = (int) ((long) rowId);
        mgr.notify(id, note);
    }
}
