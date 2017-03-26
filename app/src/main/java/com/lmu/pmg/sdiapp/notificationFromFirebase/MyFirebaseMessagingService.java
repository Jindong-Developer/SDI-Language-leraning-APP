package com.lmu.pmg.sdiapp.notificationFromFirebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lmu.pmg.sdiapp.MainActivity;
import com.lmu.pmg.sdiapp.R;

/**
 * Created by ostdong on 18/02/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        final int Noti_id=628;

        if (remoteMessage.getData().size() > 0) {
            broadcastMessage(remoteMessage.getData().toString());
        }

        if (remoteMessage.getNotification() != null) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setDefaults(Notification.DEFAULT_ALL);

            // specify notification：icon, title, content
            builder.setSmallIcon(R.drawable.sdi_logo);
            builder.setContentTitle("SDI Update");
            builder.setContentText(remoteMessage.getNotification().getBody());

            // start the SDI APP
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Noti_id, builder.build());

            //Todo List update the project
        }

    }

    private void broadcastMessage(String message) {

    }
}
