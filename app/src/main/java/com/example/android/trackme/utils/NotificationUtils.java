package com.example.android.trackme.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.android.trackme.MainActivity;
import com.example.android.trackme.R;
import com.example.android.trackme.registerActivity;

/**
 * Created by tanujanuj on 19/10/17.
 */

public class NotificationUtils {
    private static final int MEETING_REMINDER_PENDING_INTENT_ID=3417;
    private static final int MEETING_REMINDER_NOTIFICATION_ID=1138;

    public static void remindUser(Context context,String string,String id){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.notification_icon1)
                .setLargeIcon(largeIcon(context))
                .setContentTitle((string))
                .setContentText(context.getString(R.string.charging_reminder_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.charging_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }


        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        /* WATER_REMINDER_NOTIFICATION_ID allows you to update or cancel the notification later on */
        notificationManager.notify(MEETING_REMINDER_NOTIFICATION_ID, notificationBuilder.build());

    }

    private static PendingIntent contentIntent(Context context){
        Intent startActivityIntent=new Intent(context, registerActivity.class);



        return PendingIntent.getActivity(
                context,
                MEETING_REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT

        );
    }

    private static Bitmap largeIcon(Context context){
        Resources resources=context.getResources();
        Bitmap largeIcon= BitmapFactory.decodeResource(resources, R.drawable.notification_icon1);
        return largeIcon;
    }
}
