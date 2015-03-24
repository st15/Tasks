package com.lili.tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.lili.tasks.data.TaskProvider;


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
    public void onBackPressed() {
        showConfirmClosingDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                showConfirmClosingDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showConfirmClosingDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("Do you want to go back without saving?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                TaskEditActivity.this.finish();
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create().show();
    }

    @Override
    public void onFinishEditing() {
        finish();
    }
}
