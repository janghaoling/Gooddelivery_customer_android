package com.gooddelivery.user.fcm;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.gooddelivery.user.R;
import com.gooddelivery.user.activities.SplashActivity;
import com.gooddelivery.user.helper.GlobalData;
import com.gooddelivery.user.helper.SharedHelper;
import com.gooddelivery.user.models.NotificationData;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.Serializable;
import java.util.Arrays;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    NotificationData customdata;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null) {
            Log.d(TAG, "From: " + remoteMessage.getFrom());
            Log.d(TAG, "Notification Message Body: " + remoteMessage.getData());

            if (customdata != null ) {
               // Log.d(TAG, "CustomData" + remoteMessage.getData().get("custom"));
                customdata = new Gson().fromJson(remoteMessage.getData().get("custom"), NotificationData.class);

            }
            //Calling method to generate notification
            sendNotification(remoteMessage.getData().get("message"));
        } else {
            Log.d(TAG, "FCM Notification failed");
        }
    }

    private void sendNotification(String messageBody) {
        Log.d(TAG, "messageBody " + messageBody);
        GlobalData.access_token = SharedHelper.getKey(getApplicationContext(), "access_token");
        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);

        if (customdata != null && customdata.getCustomData().get(0).getOrderId() != null) {

            GlobalData.ORDER_STATUS = Arrays.asList("ORDERED", "RECEIVED", "ASSIGNED", "PROCESSING", "REACHED", "PICKEDUP", "ARRIVED", "COMPLETED");
            intent.putExtra("customdata", (Serializable) customdata);
            intent.putExtra("order_staus", "ongoing");

        } else {
            intent.putExtra("order_staus", "dispute");


        }
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Notification", messageBody);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "PUSH");
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(messageBody);

        long when = System.currentTimeMillis();         // notification time

        String CHANNEL_ID = "my_channel_01";    // The id of the channel.
        CharSequence name = "Channel human readable title";// The user-visible name of the channel.
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            importance = NotificationManager.IMPORTANCE_HIGH;

        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(getString(R.string.app_name)).
                setWhen(when)
                //                .setAutoCancel(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentIntent(pendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(inboxStyle)
                .setWhen(when)
                .setSmallIcon(getNotificationIcon(mBuilder))
                .setContentText(messageBody)
                .setChannelId(CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .build();

        NotificationManager notificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.NotificationChannel mChannel = new android.app.NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(0, notification);
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            return R.mipmap.ic_launcher;
        } else return R.mipmap.ic_launcher;
    }

}
