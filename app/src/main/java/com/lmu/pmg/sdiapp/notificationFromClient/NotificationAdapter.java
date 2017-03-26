package com.lmu.pmg.sdiapp.notificationFromClient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lmu.pmg.sdiapp.R;

import java.util.List;

/**
 * Created by ostdong on 16/02/2017.
 */

//define my own listview for notification reminders
public class NotificationAdapter extends ArrayAdapter<TimeConstants> {

    private int resourceId;

    public NotificationAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TimeConstants timeConstants = (TimeConstants) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView dateTimeView = (TextView) view.findViewById(R.id.date_and_time);
        dateTimeView.setTextSize(15);
        dateTimeView.setText(timeConstants.toString());

        return view;
    }
}
