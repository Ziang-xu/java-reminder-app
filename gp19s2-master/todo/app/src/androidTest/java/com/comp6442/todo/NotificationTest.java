package com.comp6442.todo;

import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class NotificationTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.comp6442.todo", appContext.getPackageName());
    }

    @Test
    public void notificationHelperTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        NotificationHelper notificationHelper = new NotificationHelper();
        notificationHelper.setBuilder(appContext,"new one","test this");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, App.CHANNEL_ID)
                .setContentTitle("new one")
                .setContentText("test this")
                ;
        assertEquals(false, notificationHelper.getBuilder().equals(null));
    }
}
