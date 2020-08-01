package com.comp6442.todo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;


@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView todoItemListView;
    private Button addNewTodoItemButton;
    private Intent addTodoItemIntent;
    private List<TodoItem> todoItemList;
    private TodoItemDatabase todoItemDatabase;
    private ReminderAdapter todoItemAdapter;
    private Button showLocationButton;
    private Button showCompletedButton;
    private boolean showCompletedTodoItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the variables, button and other views.
        todoItemListView = findViewById(R.id.todoItemsListView);
        showLocationButton = findViewById(R.id.showLocationButton);
        addNewTodoItemButton = findViewById(R.id.addTodoItemButton);
        showCompletedButton = findViewById((R.id.showCompletedButton));

        addTodoItemIntent = new Intent(this, AddReminderActivity.class);

        addNewTodoItemButton.setOnClickListener((e) -> {
            startActivity(addTodoItemIntent);
        });

        Intent intent_location = new Intent(this, MyLocationMap.class);
        showLocationButton.setOnClickListener((e) -> {
            startActivity(intent_location);
        });

        showCompletedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCompletedTodoItems = !showCompletedTodoItems; // flip the value
                if (showCompletedTodoItems) {
                    showCompletedButton.setText("Hide Completed");
                } else {
                    showCompletedButton.setText("Show Completed");
                }
                refreshTodoListItems();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshTodoListItems();
    }

    /**
     *this function will get all infromation from the database. then it will use the reminderAdapter to show every item on the listView.
     */
    private void refreshTodoListItems() {
        todoItemList = new LinkedList<>();
        todoItemDatabase = new TodoItemDatabase(this);
        SQLiteDatabase dbSQL = todoItemDatabase.getReadableDatabase();
        Cursor cursor = dbSQL.rawQuery("SELECT * FROM item_list", null);
        if (cursor.moveToFirst()) {
            TodoItem todoItem;
            do {
                boolean isCompleted = cursor.getInt(cursor.getColumnIndex(TodoItemDatabase.completed)) > 0;
                if (showCompletedTodoItems || (showCompletedTodoItems == isCompleted)) {
                    todoItem = new TodoItem();
                    todoItem.setItemId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(TodoItemDatabase.itemID))));
                    todoItem.setItemTitle(cursor.getString(cursor.getColumnIndex(TodoItemDatabase.itemTitle)));
                    todoItem.setItemLocation(cursor.getString(cursor.getColumnIndex(TodoItemDatabase.itemLocation)));
                    todoItem.setCreatedDate(cursor.getString(cursor.getColumnIndex(TodoItemDatabase.itemDate)));
                    todoItem.setCreatedTime(cursor.getString(cursor.getColumnIndex(TodoItemDatabase.itemTime)));
                    todoItem.setReminderDate(cursor.getString(cursor.getColumnIndex(TodoItemDatabase.reminderDate)));
                    todoItem.setReminderTime(cursor.getString(cursor.getColumnIndex(TodoItemDatabase.reminderTime)));
                    todoItem.setItemBody(cursor.getString(cursor.getColumnIndex(TodoItemDatabase.itemBody)));
                    todoItem.setItemCompleted(isCompleted);
                    todoItemList.add(todoItem);
                }
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        todoItemDatabase.close();
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        todoItemAdapter = new ReminderAdapter(todoItemList, inflater, R.layout.item);
        todoItemListView.setAdapter(todoItemAdapter);

        //set up the click listener
        todoItemListView.setOnItemClickListener(this);
        todoItemListView.setOnItemLongClickListener(this);
        todoItemAdapter.notifyDataSetChanged();
    }

    /**
     *
     * @param adapterView the adapter view of the listView
     * @param view the main activity view (default)
     * @param position the item's id in the list view
     * @param id the item id on the database
     */

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        long viewId = view.getId();
        TodoItem todoItem = (TodoItem) todoItemListView.getItemAtPosition(position);

        //if this item has been completed, it will be hided from the listView.
        if (viewId == R.id.itemCompletedSwitch) {
            Switch itemCompletedSwitch = (Switch) view.findViewById(R.id.itemCompletedSwitch);
            SQLiteDatabase database = todoItemDatabase.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TodoItemDatabase.completed, itemCompletedSwitch.isChecked());
            database.update(TodoItemDatabase.tableName, contentValues, TodoItemDatabase.itemID + "=" + todoItem.getItemId(), null);
            database.close();
            refreshTodoListItems();
        } else {
            //get the item's infromation and put them into one bundle. create a intent for edit activity, use putExtra() to transfer the bundke to the edit activity.
            Intent intent_edit_view = new Intent();
            intent_edit_view.setClass(MainActivity.this, EditReminderActivity.class);
            Bundle bundle = new Bundle();
            if (todoItem != null) {
                bundle.putInt(TodoItemDatabase.itemID, todoItem.getItemId());
                bundle.putString(TodoItemDatabase.itemTitle, todoItem.getItemTitle().trim());
                bundle.putString(TodoItemDatabase.itemBody, todoItem.getItemBody().trim());
                bundle.putString(TodoItemDatabase.itemDate, todoItem.getCreatedDate().trim());
                bundle.putString(TodoItemDatabase.itemTime, todoItem.getCreatedTime().trim());
                if (todoItem.getReminderDate() != null) {
                    bundle.putString(TodoItemDatabase.reminderDate, todoItem.getReminderDate().trim());
                }
                if (todoItem.getReminderTime() != null) {
                    bundle.putString(TodoItemDatabase.reminderTime, todoItem.getReminderTime().trim());
                }
                bundle.putBoolean(TodoItemDatabase.completed, todoItem.isItemCompleted());
                bundle.putString(TodoItemDatabase.completed, todoItem.getItemLocation());
            }
            intent_edit_view.putExtra(TodoItemDatabase.tableName, bundle);

            this.startActivity(intent_edit_view);
        }
    }

    /**
     *
     * @param adapterView the adapter view of the listView
     * @param view the main activity view (default)
     * @param position the item's id in the list view
     * @param l the item id on the database
     * @return ture if there is a long clicking.
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        TodoItem todoItem = (TodoItem) todoItemListView.getItemAtPosition(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to delete this todoItem?");
        builder.setTitle("Delete Todo");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            /**
             *
             * @param dialog create a dialog to reminder users to conform if delete or not.
             * @param index select the dialog type.
             */
            @Override
            public void onClick(DialogInterface dialog, int index) {
                SQLiteDatabase database = todoItemDatabase.getWritableDatabase();
                database.delete(TodoItemDatabase.tableName, TodoItemDatabase.itemID + "=?", new String[]{String.valueOf(todoItem.getItemId())});
                database.close();
                todoItemAdapter.removeItem(position);
                todoItemAdapter.notifyDataSetChanged();
                refreshTodoListItems();
                Toast.makeText(getBaseContext(), "Deleted Reminder", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
        return true;
    }




}
