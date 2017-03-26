package com.lmu.pmg.sdiapp;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Iris on 08.01.2017.
 */

public class ListItem {

    private String name;
    private ArrayList<ChatInstance> chatList;

    public ListItem(String name, ArrayList<ChatInstance> chatList){

        this.name = name;
        this.chatList = chatList;
    }

    public String getName() {
        return name;
    }

    public ArrayList<ChatInstance> getChatList() {
        return chatList;
    }
}
