package com.lmu.pmg.sdiapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.graphics.Typeface;
import android.widget.Spinner;
import android.widget.TextView;

import com.lmu.pmg.sdiapp.Interfaces.OnGetCountListener;
import com.lmu.pmg.sdiapp.Interfaces.OnGetDataListener;
import com.lmu.pmg.sdiapp.notificationFromClient.NotificationClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private static final int MENU_NOTIFICATION = 1;

    private SharedPreferencesManager sharedPrefs;
    private ArrayAdapter adapterLanguage;
    private Spinner languageSelectSpinner;
    private List<String> availableLanguages_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.util.Log.d("##", "APP START");

        Button categoryButton;
        Button statisticsButton;
        Button settingsButton;
        Button quitButton;

        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/CartoGothicStd.ttf");
        ((TextView) this.findViewById(R.id.introtext)).setTypeface(face);

        sharedPrefs = SharedPreferencesManager.getInstance(this);
        setDefaultSharedPreferences();

        setupLanguageSpinner();

        /**
         * START BUTTON
         * TODO: Check if the user has started the game sometime before, if not, go to difficulty
         * activity, otherwise go to categories activity
         */
        categoryButton = (Button) findViewById(R.id.button_start);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, CategoryActivity.class );
                startActivity(intent);
            }
        });



        /**
         * STATISTICS BUTTON
         */
        statisticsButton = (Button) findViewById(R.id.button_statistics);
        statisticsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this,  StatisticsActivity.class );
                startActivity(intent);
            }
        });


        /**
         * LANGUAGE DISPLAY
         */
        setupLanguages();
        setupDialogsForCurrentLanguage();
        Log.d("##", "create main");
    }

    private void setupLanguageSpinner(){
        //setup spinner with adapter
        languageSelectSpinner = (Spinner) findViewById(R.id.spinner_language_select);
        HashSet<String> availableLanguages_hashes = (HashSet<String>) sharedPrefs.getStringSet(SharedPreferencesManager.KEY_ACTIVE_LANGUAGES, new HashSet<String>());
        availableLanguages_array = new ArrayList<>(availableLanguages_hashes);
        adapterLanguage = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, availableLanguages_array);
        languageSelectSpinner.setAdapter(adapterLanguage);
        languageSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String activeLanguage = parent.getItemAtPosition(position).toString();
                sharedPrefs.setPreference(SharedPreferencesManager.KEY_ACTIVE_LANGUAGE, activeLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // preselect last language
        String lastLanguage = sharedPrefs.getString(SharedPreferencesManager.KEY_ACTIVE_LANGUAGE, getString(R.string.defaultLanguage));
        for(int i=0; i<availableLanguages_array.size(); i++){
            if(availableLanguages_array.get(i).equals(lastLanguage)){
                languageSelectSpinner.setSelection(i);
                break;
            }
        }

        adapterLanguage.notifyDataSetChanged();
    }

    private void setDefaultSharedPreferences(){

        if (sharedPrefs.getString(SharedPreferencesManager.KEY_ACTIVE_DIFFICULTY, "none").equals("none")) {
            Log.d("##", "Set default difficulty");
            sharedPrefs.setPreference(SharedPreferencesManager.KEY_ACTIVE_DIFFICULTY, getString(R.string.defaultDifficulty));
        }
        if (sharedPrefs.getString(SharedPreferencesManager.KEY_ACTIVE_LANGUAGE, "none").equals("none")) {
            Log.d("##", "Set default language");
            sharedPrefs.setPreference(SharedPreferencesManager.KEY_ACTIVE_LANGUAGE, getString(R.string.defaultLanguage));
        }

    }

    @Override
    public void onResume() {
        /*
        TextView view_activeLanguage = (TextView) findViewById(R.id.active_language);
        String activeLanguage = sharedPrefs.getString(SharedPreferencesManager.KEY_ACTIVE_LANGUAGE,
                getString(R.string.defaultLanguage));
        view_activeLanguage.setText(activeLanguage);
        */
        super.onResume();
    }

    private void setupDialogsForCurrentLanguage(){
        DBHelper dbHelper = new DBHelper();
        final String currentLanguage = sharedPrefs.getString(SharedPreferencesManager.KEY_ACTIVE_LANGUAGE,getResources().getString(R.string.defaultLanguage));

        dbHelper.getDialogMapByLanguage(currentLanguage, new OnGetCountListener() {
            @Override
            public void onSuccess(Set<String> dialogSet) {
                //sharedPrefs.setPreference(SharedPreferencesManager.KEY_DIALOGS_PER_LANGUAGE+currentLanguage, new HashSet<String>());
                if(!sharedPrefs.containsKey(SharedPreferencesManager.KEY_DIALOGS_PER_LANGUAGE+currentLanguage)){
                    sharedPrefs.setPreference(SharedPreferencesManager.KEY_DIALOGS_PER_LANGUAGE+currentLanguage, dialogSet);
                }else{
                    Set<String> oldDialogs = sharedPrefs.getStringSet(SharedPreferencesManager.KEY_DIALOGS_PER_LANGUAGE+currentLanguage,new HashSet<String>());
                    Set<String> newDialogSet = generateNewSet(dialogSet, oldDialogs);
                    sharedPrefs.setPreference(SharedPreferencesManager.KEY_DIALOGS_PER_LANGUAGE+currentLanguage, newDialogSet);
                }
            }
        });
    }

    private Set<String>  generateNewSet(Set<String> dialogSet, Set<String> oldDialogs) {
        Set<String> newDialogSet = new HashSet<>();
        for(String dialog:dialogSet){
            boolean exists = false;
            for(String oldDialog:oldDialogs){
                if(oldDialog.substring(0, oldDialog.length()-3).equals(dialog.substring(0, dialog.length()-3))){
                    exists = true;
                    newDialogSet.add(oldDialog);
                }
            }
            if(!exists){
                newDialogSet.add(dialog);
            }
        }
        return newDialogSet;
    }

    /**
     * Sets up languages from DB into the app settings
     */
    private void setupLanguages() {

        DBHelper dbHelper = new DBHelper();

        dbHelper.getLanguages(new OnGetDataListener() {
            @Override
            public void onSuccess(ArrayList<ListItem> data) {

                Set<String> availableLanguages = new HashSet<>();

                for (int i = 0; i < data.size(); i++) {
                    String name = data.get(i).getName();
                    availableLanguages.add(name);
                    if( ! availableLanguages_array.contains(name)) {
                        availableLanguages_array.add(name);
                    }
                }

                sharedPrefs.setPreference(SharedPreferencesManager.KEY_ACTIVE_LANGUAGES, availableLanguages);
                adapterLanguage.notifyDataSetChanged();
/*
                String activeLanguage = sharedPrefs.getString(SharedPreferencesManager.KEY_ACTIVE_LANGUAGE, getString(R.string.defaultLanguage));

                if (availableLanguages.contains(activeLanguage)) {
                    findViewById(R.id.progressBarLanguage).setVisibility(View.GONE);
                }
                */
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, MENU_NOTIFICATION, MENU_NOTIFICATION, getResources().getString(R.string.menu_notification));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_NOTIFICATION:
                Intent i = new Intent(MainActivity.this, NotificationClient.class);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
