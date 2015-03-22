package com.lili.tasks;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;


public class TaskEditActivity extends ActionBarActivity
        implements TaskEditFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task_edit);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(
                TaskEditFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new TaskEditFragment();
            Bundle args = new Bundle();
            args.putLong(TaskProvider.COLUMN_ROWID, getIntent()
                    .getLongExtra(TaskProvider.COLUMN_ROWID, 0L));

            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.add(R.id.edit_container, fragment,
                    TaskEditFragment.DEFAULT_EDIT_FRAGMENT_TAG);
            transaction.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

}
