package com.lili.tasks.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Lili on 22.3.2015 г..
 */
public class TaskProvider extends ContentProvider {

    // Database Columns
    public static final String COLUMN_ROWID = "_id";
    public static final String COLUMN_DATE_TIME = "reminder_date_time"; //"task_date_time";
    public static final String COLUMN_BODY = "body";
    public static final String COLUMN_TITLE = "title";
    // MIME types used for searching words or looking up a single definition
    public static final String TASKS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.com.lili.tasks.tasks";
    public static final String TASK_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.com.lili.tasks.task";
    private static final String TAG = TaskProvider.class.getName();
    // Database Related Constants
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "reminders"; //"tasks";
    private static final String DATABASE_CREATE = "create table "
            + DATABASE_TABLE + " (" + COLUMN_ROWID
            + " integer primary key autoincrement, " + COLUMN_TITLE
            + " text not null, " + COLUMN_BODY + " text not null, "
            + COLUMN_DATE_TIME + " integer not null);";
    // UriMatcher stuff
    private static final int LIST_TASK = 0;
    private static final int ITEM_TASK = 1;
    // Content Provider Uri and Authority
    public static String AUTHORITY = "com.lili.tasks.data.TaskProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/task");
    
    private static final UriMatcher sURIMatcher = buildUriMatcher();

    private SQLiteDatabase mDb;

    /**
     * Builds up a UriMatcher for search suggestion and shortcut refresh
     * queries.
     */
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        Log.d(TAG, "matcher: " + matcher);
        Log.d(TAG, "AUTHORITY: " + AUTHORITY);
        matcher.addURI(AUTHORITY, "task", LIST_TASK);
        matcher.addURI(AUTHORITY, "task/#", ITEM_TASK);
        return matcher;
    }

    /**
     * Initialize the content provider on startup.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        mDb = new DatabaseHelper(getContext()).getWritableDatabase();
        return true;
    }

    /**
     * @param uri the URI to query.
     * @return a MIME type string, or {@code null} if there is no type.
     */
    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case LIST_TASK:
                return TASKS_MIME_TYPE;
            case ITEM_TASK:
                return TASK_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    /**
     * @param uri           The URI to query. This will be the full URI sent by the client;
     *                      if the client is requesting a specific record, the URI will end in a record number
     *                      that the implementation should parse and add to a WHERE or HAVING clause, specifying
     *                      that _id value.
     * @param projection    Ignored. The list of columns to put into the cursor. If
     *                      {@code null} all columns are included.
     * @param selection     Ignored. A selection criteria to apply when filtering rows.
     *                      If {@code null} then all rows are included.
     * @param selectionArgs Ignored. You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the selection.
     *                      The values will be bound as Strings.
     * @param sortOrder     Ignored. How the rows in the cursor should be sorted.
     *                      If {@code null} then the provider is free to define the sort order.
     * @return a Cursor or {@code null}.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String[] projectionColumns = new String[]{TaskProvider.COLUMN_ROWID,
                TaskProvider.COLUMN_TITLE, TaskProvider.COLUMN_BODY,
                TaskProvider.COLUMN_DATE_TIME};
        // Use the UriMatcher to see the query type and format the
        // db query accordingly
        Cursor c;
        switch (sURIMatcher.match(uri)) {
            case LIST_TASK:
                c = mDb.query(TaskProvider.DATABASE_TABLE, projectionColumns, null,
                        null, null, null, null);
                break;
            case ITEM_TASK:
                c = mDb.query(TaskProvider.DATABASE_TABLE, projectionColumns,
                        TaskProvider.COLUMN_ROWID + "=?", new String[]{Long
                                .toString(ContentUris.parseId(uri))},
                        null, null, null, null);
                if (c != null && c.getCount() > 0) {
                    c.moveToFirst();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    /**
     * @param uri    The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The URI for the newly inserted item.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        values.remove(TaskProvider.COLUMN_ROWID);
        long id = mDb.insertOrThrow(TaskProvider.DATABASE_TABLE, null,
                values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * @param uri           The full URI to query, including a row ID (if a specific record is requested).
     * @param selection     Ignored.
     * @param selectionArgs Ignored.
     * @return The number of rows affected.
     * @throws android.database.SQLException
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = mDb.delete(TaskProvider.DATABASE_TABLE,
                TaskProvider.COLUMN_ROWID + "=?",
                new String[]{Long.toString(ContentUris.parseId(uri))});
        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * @param uri           The URI to query. This can potentially have a record ID if this
     *                      is an update request for a specific record.
     * @param values        A set of column_name/value pairs to update in the database.
     *                      This must not be {@code null}.
     * @param selection     Ignored.
     * @param selectionArgs Ignored.
     * @return the number of rows affected.
     */
    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        int count = mDb.update(TaskProvider.DATABASE_TABLE, values,
                COLUMN_ROWID + "=?",
                new String[]{Long.toString(ContentUris.parseId(uri))});
        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {

            throw new UnsupportedOperationException();
        }
    }
}
