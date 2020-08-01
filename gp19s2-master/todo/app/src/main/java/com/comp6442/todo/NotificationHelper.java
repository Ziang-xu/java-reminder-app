package com.comp6442.todo;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

class NotificationHelper {
    private NotificationCompat.Builder builder;

    /**
     *
     * @param context the context send to the notification, it will help to set up the notification.
     * @param title the notification's title
     * @param content the notification's content
     */
    void setBuilder(Context context, String title, String content) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        builder = new NotificationCompat.Builder(context, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    NotificationCompat.Builder getBuilder() {
        return builder;
    }


}