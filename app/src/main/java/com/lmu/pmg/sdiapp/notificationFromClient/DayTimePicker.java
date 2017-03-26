package com.lmu.pmg.sdiapp.notificationFromClient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lmu.pmg.sdiapp.MainActivity;
import com.lmu.pmg.sdiapp.R;
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;
import com.touchboarder.weekdaysbuttons.WeekdaysDataSource;
import com.touchboarder.weekdaysbuttons.WeekdaysDrawableProvider;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DayTimePicker extends AppCompatActivity {

    private Button saveNotification;
    private TimePicker timePicker;
    private final int RESULT_OK = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_time_picker);
        setTitle(R.string.notification_title);

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        int timeHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int timeMinute = Calendar.getInstance().get(Calendar.MINUTE);
        if (Build.VERSION.SDK_INT >= 23) {
            timePicker.setMinute(timeMinute);
            timePicker.setHour(timeHour);
        } else {
            timePicker.setCurrentMinute(timeMinute);
            timePicker.setCurrentHour(timeHour);
        }
        final boolean[] weekday = new boolean[]{true, true, true, true, true, true, true};

        WeekdaysDataSource wds = new WeekdaysDataSource(this, R.id.weekdays_stub)
                .setUnselectedColor(Color.TRANSPARENT)
                .setTextColorUnselectedRes(R.color.colorAccent)
                .setDrawableType(WeekdaysDrawableProvider.MW_ROUND_RECT)
                .setTextColorSelected(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
                .setSelectedColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setLocale(Locale.GERMAN)
                .setNumberOfLetters(2)
                .start(
                new WeekdaysDataSource.Callback() {
                    @Override
                    public void onWeekdaysItemClicked(int attachId,WeekdaysDataItem item) {
                        if(item.getCalendarDayId()==Calendar.MONDAY)
                            weekday[0] = item.isSelected();
                        if(item.getCalendarDayId()==Calendar.TUESDAY)
                            weekday[1] = item.isSelected();
                        if(item.getCalendarDayId()==Calendar.WEDNESDAY)
                            weekday[2] = item.isSelected();
                        if(item.getCalendarDayId()==Calendar.THURSDAY)
                            weekday[3] = item.isSelected();
                        if(item.getCalendarDayId()==Calendar.FRIDAY)
                            weekday[4] = item.isSelected();
                        if(item.getCalendarDayId()==Calendar.SATURDAY)
                            weekday[5] = item.isSelected();
                        if(item.getCalendarDayId()==Calendar.SUNDAY)
                            weekday[6] = item.isSelected();
                    }

                    @Override
                    public void onWeekdaysSelected(int attachId,ArrayList<WeekdaysDataItem> items) {

                    }

                }
        );
        wds.selectAll(true);

        saveNotification = (Button) findViewById(R.id.saveNotification);
        saveNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour = 0;
                int minute = 0;


                if (Build.VERSION.SDK_INT >= 23) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                } else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }


                boolean allEmpty = true;
                for (boolean checked :
                        weekday) {
                    if (checked){
                        allEmpty = false;
                    }
                }

                if(allEmpty){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.choose_day), Toast.LENGTH_SHORT).show();
                }else {

                    Intent intent = new Intent();
                    intent.putExtra("hour", hour);
                    intent.putExtra("minute", minute);
                    intent.putExtra("weekday", weekday);

                    DayTimePicker.this.setResult(RESULT_OK, intent);
                    DayTimePicker.this.finish();
                }
            }

        });


    }
}
