package com.lmu.pmg.sdiapp.notificationFromClient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.lmu.pmg.sdiapp.R;
import com.lmu.pmg.sdiapp.SharedPreferencesManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.lmu.pmg.sdiapp.SharedPreferencesManager.KEY_ALARM_LIST;

public class NotificationClient extends AppCompatActivity {

    private ListView lvNotification;
    private Button addNotification;

    private final int RESULT_OK = 1;

    NotificationAdapter adapter;
    private ArrayList<TimeConstants> notificationsArray = new ArrayList();

    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //specify the name of this activity and back arrow
        setTitle(R.string.notification_title);
        getSupportActionBar().setHomeButtonEnabled(true);

        //get Alarm Service
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        //add a notification reminder
        addNotification = (Button) findViewById(R.id.addNotification);
        addNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationClient.this, DayTimePicker.class);
                startActivityForResult(intent, RESULT_OK);

                //addNotification();
            }
        });

        //specify the listview
        adapter = new NotificationAdapter(this, R.layout.notification_item, notificationsArray);
        lvNotification = (ListView) findViewById(R.id.lvNotification);
        readSaveNotificationList();
        lvNotification.setAdapter(adapter);

        lvNotification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(NotificationClient.this, R.style.MyDialogTheme)
                        .setTitle(R.string.operation)
                        .setMessage("Möchtest Du diese Erinnerung löschen?").setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNotificationitem(position);
                    }
                }).setNegativeButton(R.string.cancel, null).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            int hour = data.getExtras().getInt("hour");
            int minute = data.getExtras().getInt("minute");
            boolean[] weekday = data.getExtras().getBooleanArray("weekday");
            addNotification(hour, minute, weekday);
        }
    }

    //add and delete the Notification from listview and Alarm Manager
    private void addNotification(int hour, int minutes, boolean[] weekday) {
        Calendar calaendar = Calendar.getInstance();
        calaendar.set(Calendar.HOUR_OF_DAY, hour);
        calaendar.set(Calendar.MINUTE, minutes);

        calaendar.set(Calendar.SECOND, 0);
        calaendar.set(Calendar.MILLISECOND, 0);

        String weekdays = " ";
        if (weekday[0] == true) weekdays += "Montag, ";
        if (weekday[1] == true) weekdays += "Dienstag, ";
        if (weekday[2] == true) weekdays += "Mittwoch, ";
        if (weekday[3] == true) weekdays += "Donnerstag, ";
        if (weekday[4] == true) weekdays += "Freitag, ";
        if (weekday[5] == true) weekdays += "Samstag, ";
        if (weekday[6] == true) weekdays += "Sonntag, ";

        weekdays = weekdays.substring(0, weekdays.length()-2);

        TimeConstants timeConstant = new TimeConstants(calaendar.getTimeInMillis(), weekdays);
        int weektoday = calaendar.get(Calendar.DAY_OF_WEEK)-1;

        boolean weekdaychooseflag=true;

        for (int i = 0; i < 7; i++) {
            if (weekday[i] == true) {

                weekdaychooseflag=false;

                long starttime = timeConstant.getTime();

                int weekdayseting = i + 1;

                if (weekdayseting >= weektoday) starttime = timeConstant.getTime() + (weekdayseting- weektoday) * 24 * 60 * 60 * 1000;
                else starttime = timeConstant.getTime() + (7 - weektoday + weekdayseting) * 24 * 60 * 60 * 1000;

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, starttime
                        , 7 * 24 * 60 * 60 * 1000, PendingIntent
                                .getBroadcast(getApplicationContext(),
                                        timeConstant.getId() * (1 + i), new Intent(getApplicationContext(), AlarmReceiver.class), 0));
            }
        }
        if (weekdaychooseflag==true){ Toast.makeText(getApplicationContext(),"Kein Tag ausgewählt",Toast.LENGTH_LONG).show();}
        else {notificationsArray.add(timeConstant);
            adapter.notifyDataSetChanged();}
        saveNotificationList();
    }

    private void deleteNotificationitem(int position) {
        TimeConstants timeConstant = adapter.getItem(position);
        notificationsArray.remove(timeConstant);
        adapter.notifyDataSetChanged();
        saveNotificationList();

        for (int i = 0; i < 7; i++) {
           // if (PendingIntent.getBroadcast(getApplicationContext(),
           //         timeConstant.getId() * (1 + i), new Intent(this, AlarmReceiver.class), 0) != null)
                alarmManager.cancel(PendingIntent.getBroadcast(getApplicationContext(),
                        timeConstant.getId() * (1 + i), new Intent(this, AlarmReceiver.class), 0));
        }
    }

    //read and save the notification time persistently
    private void saveNotificationList() {
        SharedPreferencesManager manager = SharedPreferencesManager.getInstanceIfExists();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < adapter.getCount(); i++) {
            sb.append(adapter.getItem(i).getTime()).append(",");
            sb.append(adapter.getItem(i).getWeekdays()).append(";");
        }
        if (sb.length() > 1) {
            String content = sb.toString().substring(0, sb.length() - 1);
            manager.setPreference(SharedPreferencesManager.KEY_ALARM_LIST, content);

            System.out.println(content);
        } else {
            manager.setPreference(SharedPreferencesManager.KEY_ALARM_LIST, "");
        }
    }

    private void readSaveNotificationList() {
        SharedPreferencesManager manager = SharedPreferencesManager.getInstanceIfExists();
        String content = manager.getString(KEY_ALARM_LIST, null);

        if (content != null) {
            String[] timeStrings = content.split(";");
            for (String string : timeStrings) {
                String[] timeandweekday = string.split(",");
                if(!timeandweekday[0].equals("") && !timeandweekday[1].equals("")) {
                    notificationsArray.add(new TimeConstants(Long.parseLong(timeandweekday[0]), timeandweekday[1]));
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}
