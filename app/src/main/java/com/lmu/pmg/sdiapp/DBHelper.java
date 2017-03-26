package com.lmu.pmg.sdiapp;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lmu.pmg.sdiapp.Interfaces.OnGetCountListener;
import com.lmu.pmg.sdiapp.Interfaces.OnGetDataListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Iris on 08.01.2017.
 */

public class DBHelper {

    private FirebaseDatabase database;
    private DatabaseReference ref;
    private static final String TAG = DBHelper.class.getSimpleName();

    public DBHelper(){
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
    }

    public void getLanguages(final OnGetDataListener listener){
        final ArrayList<ListItem> languageList = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot languages: dataSnapshot.getChildren()) {
                    String name = languages.getKey();
                    ListItem item = new ListItem(name, new ArrayList<ChatInstance>());
                    languageList.add(item);
                }
                listener.onSuccess(languageList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getDialogMapByLanguage(final String language, final OnGetCountListener listener){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String notCompletedYet = "[0]";
                Set<String> dialogSet = new HashSet<String>();
                for (DataSnapshot languages: dataSnapshot.getChildren()) {
                    if(language.equals(languages.getKey())){
                        for (DataSnapshot categories: languages.getChildren()) {
                            for (DataSnapshot levels: categories.getChildren()) {
                                for (DataSnapshot dialogs: levels.getChildren()) {
                                    dialogSet.add(dialogs.getKey()+notCompletedYet);
                                }
                            }
                        }
                    }
                }
                listener.onSuccess(dialogSet);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getCategoriesByLanguage(final String language, final OnGetDataListener listener){
        final ArrayList<ListItem> categoryList = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot languages: dataSnapshot.getChildren()) {
                    if(languages.getKey().equals(language)){
                        getChildElements(languages, categoryList);
                    }
                }
                listener.onSuccess(categoryList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getLevelsByCategoryAndLanguage(final String language, final String category, final OnGetDataListener listener){
        final ArrayList<ListItem> levelList = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot languages: dataSnapshot.getChildren()) {
                    if(languages.getKey().equals(language)){
                        for (DataSnapshot categories: languages.getChildren()) {
                            if(categories.getKey().equals(category)) {
                                getChildElements(categories, levelList);
                            }
                        }
                    }
                }
                listener.onSuccess(levelList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getDialogsByLanguageCategoryAndLevel(final String language, final String category, final String level, final OnGetDataListener listener){
        final ArrayList<ListItem> chatList = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot languages: dataSnapshot.getChildren()) {
                    if(languages.getKey().equals(language)){
                        for (DataSnapshot categories: languages.getChildren()) {
                            if(categories.getKey().equals(category)) {
                                for (DataSnapshot levels: categories.getChildren()) {
                                    if(levels.getKey().equals(level)) {
                                        for (DataSnapshot dialogs: levels.getChildren()) {
                                            getDataForEachElement(dialogs, chatList);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                listener.onSuccess(chatList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getChildElements(DataSnapshot parent, ArrayList<ListItem> list){
        for (DataSnapshot val:parent.getChildren()){
            ListItem item = new ListItem(val.getKey(), new ArrayList<ChatInstance>());
            list.add(item);
        }
    }

    private void getDataForEachElement(DataSnapshot levels, ArrayList<ListItem> list) {
        System.out.print("test");
        String name = levels.getKey();
        ArrayList<ChatInstance> chatList = new ArrayList<ChatInstance>();
        generateChatInstances(levels, chatList);
        ListItem listItem = new ListItem(name, chatList);
        list.add(listItem);
    }

    private void generateChatInstances(DataSnapshot categories, ArrayList<ChatInstance> chatList) {
        for (DataSnapshot val:categories.getChildren()){
            String answer = (String)val.child("answer").getValue();
            String question = (String)val.child("question").getValue();

            Object objA = val.child("blanksA").getValue();
            List<String> gapsA = getArrayOfObject(objA);

            Object objB = val.child("blanksB").getValue();
            List<String> gapsB = getArrayOfObject(objB);

            ChatInstance chatInstance = new ChatInstance(question, answer, gapsA, gapsB);
            chatList.add(chatInstance);
        }
    }

    /**
     * Inserts Object into new ArrayList
     * @param object can be of class ArrayList or String
     * @return List with object inserted
     */
    private ArrayList<String> getArrayOfObject (Object object){
        ArrayList<String> gapsToReturn = new ArrayList<>();
        if(!object.equals("")) {
            if(object.getClass() == ArrayList.class) {
                ArrayList<String> gaps = (ArrayList<String>) object;
                gapsToReturn.addAll(gaps);
            }
            else if(object.getClass() == String.class){
                gapsToReturn.add((String) object);
            }
            else Log.e(TAG, "Datatype of object not handled ("+ object.getClass().getSimpleName() +")");
        }
        return gapsToReturn;
    }

}
