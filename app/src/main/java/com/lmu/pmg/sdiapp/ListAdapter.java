package com.lmu.pmg.sdiapp;

/**
 * Created by Iris on 08.01.2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by julbr on 26.11.2016.
 */
public class ListAdapter extends ArrayAdapter<ListItem> {

    public static final String TAG = ListAdapter.class.getSimpleName();

    private final LayoutInflater inflator;

    public ListAdapter(Context context, int textViewResourceId, ArrayList<ListItem> originalList ) {

        super( context, textViewResourceId, originalList );
        inflator = LayoutInflater.from( context );
    }

    @Override
    public View getView(int id, View convertView, ViewGroup parent ) {

        ViewHolder holder;

        if ( convertView == null ) {

            convertView = inflator.inflate( R.layout.list_fragment, parent, false );

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById( R.id.fragmentName );

            convertView.setTag( holder );

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        ListItem item = getItem( id );

        holder.name.setText( item.getName() );

        return convertView;
    }

    static class ViewHolder {

        TextView name;
    }
}
