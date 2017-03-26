package com.lmu.pmg.sdiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Iris on 08.01.2017.
 */

public class ChatListAdapter extends ArrayAdapter<ChatLine> {

public static final String TAG = ListAdapter.class.getSimpleName();

private final LayoutInflater inflator;

public ChatListAdapter(Context context, int textViewPerson, int textViewText, ArrayList<ChatLine> originalList ) {

        super( context, textViewPerson, textViewText, originalList );
            inflator = LayoutInflater.from( context );
        }

        @Override
        public View getView(int id, View convertView, ViewGroup parent ) {

                ViewHolder holder;

                if ( convertView == null ) {

                convertView = inflator.inflate( R.layout.chat_fragment, parent, false );

                holder = new ViewHolder();
                holder.chatName = (TextView) convertView.findViewById( R.id.chatName );
                holder.chatText = (TextView) convertView.findViewById( R.id.chatText );

                convertView.setTag( holder );

                } else {

                holder = (ViewHolder) convertView.getTag();
                }

                ChatLine item = getItem( id );

                holder.chatName.setText( item.getPerson() );
                holder.chatText.setText( item.getSpannedText() );

                if(holder.chatName.getText().equals(ChatLogic.PERSON_QUESTION)) {
                        convertView.setBackgroundResource(R.drawable.speech_bubble_incoming);
                } else {
                        convertView.setBackgroundResource(R.drawable.speech_bubble_outgoing);
                }

                return convertView;
        }

        static class ViewHolder {

            TextView chatName;
            TextView chatText;
        }
}
