package com.comp6442.todo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * this class extends SQLiteOpenHelper.
 * it will set the database for every item
 */
public class TodoItemDatabase extends SQLiteOpenHelper {
    static String tableName = "item_list";
    static String itemID = "id";
    static String itemTitle = "title";
    static String itemDate = "created_date";
    static String itemTime = "created_time";
    static String itemBody = "item_body";
    static String itemLocation = "item_location";
    static String reminderDate = "reminder_date";
    static String reminderTime = "reminder_time";
    static String completed = "completed";

    TodoItemDatabase(@Nullable Context context) {
        super(context, "database", null, 1);
    }

    /**
     *
     * @param sqliteDb the sql database name. we will save the new item on it. in this class we will use sql to create the table
     */
    @Override
    public void onCreate(SQLiteDatabase sqliteDb) {
        sqliteDb.execSQL("CREATE TABLE " + tableName + " (" + itemID + " INTEGER  PRIMARY KEY AUTOINCREMENT, "
                + itemTitle + " VARCHAR(35), " + itemDate + " DATE," + itemTime + " TIME, " + itemBody + " TEXT, " + itemLocation
                + " VARCHAR(20), " + reminderDate + " DATE, " + reminderTime + " TIME," + completed + " BOOLEAN)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     *
     * @param itemId the item id on this table.
     * @return boolean value. if this item is in the database, it will return true, or it will be false.
     */
    boolean doesItemExist(int itemId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + itemID + " = " + itemId, null);
        boolean itemExists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return itemExists;
    }

}
