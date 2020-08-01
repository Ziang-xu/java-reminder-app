package com.comp6442.todo;

import androidx.annotation.NonNull;

/**
 * this class is the todo item class
 * this class give the every item should contain nine properties.
 */
public class TodoItem {
    private int itemId;
    private String itemTitle;
    private String itemBody;
    private String createdDate;
    private String createdTime;
    private String itemLocation;
    private String reminderDate;
    private String reminderTime;
    private boolean itemCompleted;

    int getItemId() {
        return itemId;
    }

    void setItemId(int itemId) {
        this.itemId = itemId;
    }

    String getItemTitle() {
        return itemTitle;
    }

    void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    String getCreatedDate() {
        return createdDate;
    }

    void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    String getCreatedTime() {
        return createdTime;
    }

    void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    String getItemLocation() {
        return itemLocation;
    }

    void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }

    String getReminderDate() {
        return reminderDate;
    }

    void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    String getReminderTime() {
        return reminderTime;
    }

    void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    boolean isItemCompleted() {
        return itemCompleted;
    }

    void setItemCompleted(boolean itemCompleted) {
        this.itemCompleted = itemCompleted;
    }

    String getItemBody() {
        return itemBody;
    }

    void setItemBody(String itemBody) {
        this.itemBody = itemBody;
    }

    /**
     *
     * @return the string of item.
     */
    @NonNull
    @Override
    public String toString() {
        return "item's id is " + itemId + ", item's title is " + itemTitle + ", item body is " + itemBody
                + ", item's created date is "
                + createdDate + ", item's created time is " + createdTime + ", item's location is "
                + itemLocation + ", item's reminder date is " + reminderDate + ", item's reminder time is "
                + reminderTime + ", the item completed situation: " + itemCompleted + ".";
    }
}
