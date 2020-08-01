package com.comp6442.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

/**
 * this class extends BroadcastReceiver class.
 */
public class NotificationAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationAlarmReceiver";

    static final String NOTIFICATION_ID = "notification_id";
    static final String NOTIFICATION_TITLE = "notification_title";
    static final String NOTIFICATION_BODY = "notification_body";

    /**
     *
     * @param context the activity's context to set up the alarm notification.
     * @param intent the transfer intent, from this intent, you can get the notification's title and body.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra(NOTIFICATION_ID, -1);

        // don't show a notification if the item has been deleted by the user
        TodoItemDatabase todoItemDatabase = new TodoItemDatabase(context.getApplicationContext());
        if(!todoItemDatabase.doesItemExist(notificationId)) {
            Log.d(TAG, "Todo item deleted by user, will not show notification");
            return;
        }

        String notificationTitle = intent.getStringExtra(NOTIFICATION_TITLE);
        String notificationBody = intent.getStringExtra(NOTIFICATION_BODY);

        //use the notificationHelper to set up a new notification.
        NotificationManagerCompat notificationManagerCompat  = NotificationManagerCompat.from(context);
        NotificationHelper notificationHelper = new NotificationHelper();
        notificationHelper.setBuilder(context,notificationTitle,notificationBody);
        notificationManagerCompat.notify(notificationId, notificationHelper.getBuilder().build());
    }
}
