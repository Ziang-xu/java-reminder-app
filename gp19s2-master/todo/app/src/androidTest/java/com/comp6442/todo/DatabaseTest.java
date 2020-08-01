package com.comp6442.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.comp6442.todo.TodoItemDatabase.reminderDate;
import static com.comp6442.todo.TodoItemDatabase.reminderTime;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.comp6442.todo", appContext.getPackageName());

        TodoItemDatabase todoItemDatabase = new TodoItemDatabase(appContext);
        SQLiteDatabase database = todoItemDatabase.getWritableDatabase();
        ContentValues todoItemContentValues = new ContentValues();
        todoItemContentValues.put(TodoItemDatabase.itemTitle, "new item");
        todoItemContentValues.put(TodoItemDatabase.itemBody, "a new item for test");
        todoItemContentValues.put(TodoItemDatabase.itemDate, "1");
        todoItemContentValues.put(TodoItemDatabase.itemTime, "2");
        todoItemContentValues.put(TodoItemDatabase.reminderDate, "3");
        todoItemContentValues.put(TodoItemDatabase.reminderTime, "4");
        todoItemContentValues.put(TodoItemDatabase.itemLocation, "5");
        todoItemContentValues.put(TodoItemDatabase.completed, "yes");
        long itemId = database.insert(TodoItemDatabase.tableName, null, todoItemContentValues);
        database.close();
        SQLiteDatabase db = todoItemDatabase.getWritableDatabase();
        Assert.assertNotNull(db);
        assertEquals(true,todoItemDatabase.doesItemExist(1));
        assertEquals(false,todoItemDatabase.doesItemExist(2));
    }
}
