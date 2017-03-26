package com.lmu.pmg.sdiapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.Set;

/**
 * Created by Jonny on 20.02.2017.
 */

public class SharedPreferencesManager {

    private static final String PREFERNECES = "preferences";

    /**
     * key for text to speach mute
     */
    public static final String KEY_MUTE = "mute";

    /**
     * key for score
     */
    public static final String KEY_POINTS = "points";

    /**
     * key for last set difficulty
     */
    public static final String KEY_ACTIVE_DIFFICULTY = "active_difficlty";

    /**
     * key for last selected language
     */
    public static final String KEY_ACTIVE_LANGUAGE = "active_language";

    /**
     * key for all(???) languages
     */
    public static final String KEY_ACTIVE_LANGUAGES = "active_languages";

    /**
     * key for all alarms
     */
    public static final String KEY_ALARM_LIST = "Notification_List";

    /**
     * keys for statistics
     */
    public static final String KEY_DIALOGS_PER_LANGUAGE = "dialogs_language_";
    public static final String KEY_OVERALL_SCORE = "overall_score";
    public static final String KEY_USER_SCORE = "user_score";
    public static final String KEY_CURRENT_OVERALL_SCORE = "current_overall_score";
    public static final String KEY_CURRENT_USER_SCORE = "current_user_score";
    public static final String KEY_PERFECT_ROUNDS = "perfect_rounds";
    public static final String KEY_CORRECT_ANSWERS = "correct_answers";

    /**
     * key for toast flag (for keyboard language check)
     */
    public static final String KEY_TOAST_FLAG = "TOAST_FLAG";


    private static SharedPreferencesManager instance;

    private SharedPreferences manager;

    public static SharedPreferencesManager getInstance (Context context){
        if(instance == null){
            instance = new SharedPreferencesManager(context);
        }
        return instance;
    }

    @Nullable
    public static SharedPreferencesManager getInstanceIfExists () {
        return instance;
    }

    private SharedPreferencesManager (Context context) {
        manager = context.getSharedPreferences(PREFERNECES, Context.MODE_PRIVATE);
    }

    public String getString(String key, String defaultValue){
        return manager.getString(key, defaultValue);
    }

    public boolean containsKey(String key){
        return manager.contains(key);
    }

    public int getInt(String key, int defaultValue){
        return manager.getInt(key, defaultValue);
    }
    public boolean getBoolean(String key, boolean defaultValue){
        return manager.getBoolean(key, defaultValue);
    }

    public Set<String> getStringSet(String key, Set<String> defaultValue){
        return manager.getStringSet(key, defaultValue);
    }

    public void setPreference (String key, String value){
        SharedPreferences.Editor editor = manager.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setPreference (String key, boolean value){
        SharedPreferences.Editor editor = manager.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public void setPreference (String key, Set<String> value){
        SharedPreferences.Editor editor = manager.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public void setPreference (String key, int value){
        SharedPreferences.Editor editor = manager.edit();
        editor.putInt(key, value);
        editor.apply();
    }

}
