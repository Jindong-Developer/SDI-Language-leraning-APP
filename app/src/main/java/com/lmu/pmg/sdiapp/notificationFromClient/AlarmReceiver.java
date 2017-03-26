package com.lmu.pmg.sdiapp.notificationFromClient;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;


import com.lmu.pmg.sdiapp.MainActivity;
import com.lmu.pmg.sdiapp.R;


/**
 * Created by ostdong on 16/02/2017.
 */

//process the alarm event
public class AlarmReceiver extends BroadcastReceiver {

    private final static int Noti_id = 637;

    @Override
    public void onReceive(Context context, Intent arg1) {
        System.out.println("It works!");

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(PendingIntent.getBroadcast(context, getResultCode(), new Intent(context, AlarmReceiver.class), 0));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setDefaults(Notification.DEFAULT_ALL);
        ;

        // specify notification：icon, title, content
        builder.setSmallIcon(R.drawable.sdi_logo);
        builder.setContentTitle("SDI Erinnerung");
        builder.setContentText("Fremdsprache lernen");

        // start the SDI APP
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Noti_id, builder.build());
    }
}
