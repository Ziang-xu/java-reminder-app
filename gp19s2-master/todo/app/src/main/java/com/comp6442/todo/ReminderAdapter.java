package com.comp6442.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

/**
 * this class extends BaseAdapter. it will override four methods: getCount(),getItem(),getItemId() and getView()
 * this class is the list adapter. it will be used to put every item on the list view on the main activity.
 */
public class ReminderAdapter extends BaseAdapter {
    private List<TodoItem> todoItemList;
    private LayoutInflater inflater;
    private int layoutId;

    /**
     * it is the constructor.
     * @param todoItemList
     * @param inflater
     * @param layoutId
     */
    public ReminderAdapter(List<TodoItem> todoItemList, LayoutInflater inflater, int layoutId) {
        this.todoItemList = todoItemList;
        this.inflater = inflater;
        this.layoutId = layoutId;
    }

    /**
     *
     * @return the list size.
     */
    @Override
    public int getCount() {
        if (todoItemList == null)
            return 0;
        else return todoItemList.size();
    }

    /**
     *
     * @param i the item's id
     * @return the item's context
     */
    @Override
    public Object getItem(int i) {
        if (todoItemList != null && i > -1 && i < todoItemList.size())
            return todoItemList.get(i);
        else return null;
    }

    /**
     *
     * @param i the id of int type
     * @return the item id
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     *
     * @param position the item's position of the list
     * @param view the item view
     * @param parent the list view, contains with a lot of items
     * @return the list view
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        TextView itemTitleTextView;
        TextView itemLocationTextView;
        TextView itemCreationDateTimeTextView;
        TextView itemReminderTextView;
        TextView itemNotesTextView;
        Switch itemCompletedSwitch;

        if (view == null) {
            //set up the every textView and the viewgroup.
            view = inflater.inflate(R.layout.item, parent, false);

            itemTitleTextView = view.findViewById(R.id.itemTitleTextView);
            itemLocationTextView = view.findViewById(R.id.itemLocationTextView);
            itemCreationDateTimeTextView = view.findViewById(R.id.itemCreationDateTimeTextView);
            itemReminderTextView = view.findViewById(R.id.itemReminderDateTimeTextView);
            itemNotesTextView = view.findViewById(R.id.itemNotesTextView);
            itemCompletedSwitch = view.findViewById(R.id.itemCompletedSwitch);

            itemViews viewGroup1 = new itemViews();
            viewGroup1.itemTitleTextView = itemTitleTextView;
            viewGroup1.itemLocationTextView = itemLocationTextView;
            viewGroup1.itemCreationDateTimeTextView = itemCreationDateTimeTextView;
            viewGroup1.itemReminderTextView = itemReminderTextView;
            viewGroup1.itemNotesTextView = itemNotesTextView;
            viewGroup1.itemCompletedSwitch = itemCompletedSwitch;

            view.setTag(viewGroup1);
        } else {
            itemViews group = (ReminderAdapter.itemViews) view.getTag();
            itemTitleTextView = group.itemTitleTextView;
            itemLocationTextView = group.itemLocationTextView;
            itemCreationDateTimeTextView = group.itemCreationDateTimeTextView;
            itemReminderTextView = group.itemReminderTextView;
            itemNotesTextView = group.itemNotesTextView;
            itemCompletedSwitch = group.itemCompletedSwitch;
        }

        TodoItem todoItem = todoItemList.get(position);
        itemTitleTextView.setText(todoItem.getItemTitle());
        itemLocationTextView.setText(todoItem.getItemLocation());
        itemCreationDateTimeTextView.setText(todoItem.getCreatedDate() + " at " + todoItem.getCreatedTime());
        itemReminderTextView.setText(todoItem.getReminderDate() + " at " + todoItem.getReminderTime());
        itemNotesTextView.setText(todoItem.getItemBody());
        itemCompletedSwitch.setChecked(todoItem.isItemCompleted());

        itemCompletedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((ListView) parent).performItemClick(buttonView, position, 0);
            }
        });

        return view;
    }

    /**
     *
     * @param i the item id.
     * it will remove the id item from the list
     */
    void removeItem(int i) {
        if (todoItemList != null && i > -1 && i < todoItemList.size())
            todoItemList.remove(i);
    }

    /**
     * all the views of the item view.
     */
    class itemViews {
        TextView itemTitleTextView;
        TextView itemLocationTextView;
        TextView itemCreationDateTimeTextView;
        TextView itemReminderTextView;
        TextView itemNotesTextView;
        Switch itemCompletedSwitch;
    }
}
