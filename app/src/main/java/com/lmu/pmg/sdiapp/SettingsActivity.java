package com.lmu.pmg.sdiapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.lmu.pmg.sdiapp.notificationFromClient.NotificationClient;

import static com.lmu.pmg.sdiapp.R.id.notification;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Alex on 22.01.2017.
 */
public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    ArrayAdapter<CharSequence> adapterLanguage;
    Spinner languageSpinner;
    Button notification_Button;
    SharedPreferencesManager sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Einstellungen");

        sharedPrefs = SharedPreferencesManager.getInstanceIfExists();

        findViewById(R.id.language_spinner).setVisibility(View.INVISIBLE);

        //Get Languages into a dropdown list
        languageSpinner = (Spinner) findViewById(R.id.language_spinner);
        languageSpinner.setOnItemSelectedListener(this);
        HashSet<String> availableLanguages_hashes = (HashSet<String>) sharedPrefs.getStringSet(SharedPreferencesManager.KEY_ACTIVE_LANGUAGES, new HashSet<String>());
        ArrayList<String> availableLanguages_array = new ArrayList<>();
        availableLanguages_array.addAll(availableLanguages_hashes);
        adapterLanguage = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, availableLanguages_array);
        languageSpinner.setAdapter(adapterLanguage);

        notification_Button= (Button) findViewById(notification);
        notification_Button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, NotificationClient.class );
                startActivity(intent);
            }
        });
        languageSpinner.setSelection(adapterLanguage.getPosition(sharedPrefs.getString(SharedPreferencesManager.KEY_ACTIVE_LANGUAGE, "default")));
        languageSpinner.setVisibility(View.VISIBLE);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;

         if(spinner.getId() == R.id.language_spinner)
        {
            Log.d("Selected Language", "" + parent.getItemAtPosition(position));
            sharedPrefs.setPreference(SharedPreferencesManager.KEY_ACTIVE_LANGUAGE,""+parent.getItemAtPosition(position));

            ChatActivity.setLanguage(parent.getItemAtPosition(position).toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }
}
