package com.lili.tasks.reminder;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Created by Lili on 22.3.2015 г..
 */
public abstract class WakeReminderIntentService extends IntentService {
    public static final String LOCK_NAME_STATIC = "com.lili.tasks.reminder.Static";
    private static PowerManager.WakeLock lockStatic = null;

    public WakeReminderIntentService(String name) {
        super(name);
    }

    public static void acquireStaticLock(Context context) {
        getLock(context).acquire();
    }

    synchronized private static PowerManager.WakeLock
    getLock(Context context) {
        if (lockStatic == null) {
            PowerManager
                    mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    LOCK_NAME_STATIC);
        }
        return (lockStatic);
    }

    abstract void doReminderWork(Intent intent);

    @Override
    final protected void onHandleIntent(Intent intent) {
        try {
            doReminderWork(intent);
        } finally {
            getLock(this).release();
        }
    }
}
