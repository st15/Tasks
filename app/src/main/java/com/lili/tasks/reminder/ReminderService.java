package com.lili.tasks.reminder;

import android.content.Intent;

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
    }
}
