package com.lmu.pmg.sdiapp.notificationFromClient;

import java.util.Calendar;

import com.lmu.pmg.sdiapp.R;


/**
 * Created by ostdong on 15/02/2017.
 */

//Define a model that corresponds to the notification time
public class TimeConstants {

    public long time = 0;
    public String weekdays = "";
    public String timelabel = "";
    public Calendar date;

    public TimeConstants(long Time, String weekdays) {
        this.time = Time;
        this.weekdays = weekdays;

        date = Calendar.getInstance();
        date.setTimeInMillis(time);

        timelabel = date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.HOUR_OF_DAY);


        timelabel = String.format("Uhrzeit: %02d:%02d" + weekdays,
                date.get(Calendar.HOUR_OF_DAY),
                date.get(Calendar.MINUTE));

    }

    public long getTime() {
        return time;
    }

    public String getWeekdays() {
        return weekdays;
    }

    public String getTimelebel() {
        return timelabel;
    }

    public int getId() {
        return (int) (getTime() / 1000 / 60);
    }

    @Override
    public String toString() {
        return getTimelebel();
    }

}
