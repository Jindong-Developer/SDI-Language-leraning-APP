package com.lmu.pmg.sdiapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.EditText;

import com.lmu.pmg.sdiapp.notificationFromClient.NotificationClient;

import java.util.ArrayList;
import java.util.Locale;


public class ChatActivity extends AppCompatActivity  {

    private static final int MENU_MUTE = 1;
    private static final int MENU_KEYBOARD = 2;


    protected static final int REQUEST_OK = 1;
    private boolean IsSpeech = false;
    //private static String speachLanguage ="";
    private String  language="";

    private Menu menu;
    private ChatListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setTitle(R.string.chat_title);

        FragmentManager fm = getSupportFragmentManager();
        fragment = (ChatListFragment)fm.findFragmentById(R.id.chat_list);
        /*
        Button check = (Button)findViewById(R.id.button_send);
        check.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText et = (EditText)findViewById(R.id.chat_input);
                String answer=et.getText().toString();
                /*
                if(IsSpeech)
                {
                    Toast.makeText(getApplicationContext(), "Speech:"+answer, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Text:"+answer, Toast.LENGTH_SHORT).show();
                }

                IsSpeech=false;
            }
        });

*/

        SharedPreferencesManager manager = SharedPreferencesManager.getInstanceIfExists();

        if ( ! Iskeyboardlanguagesame()){
            if ( ! manager.getBoolean(SharedPreferencesManager.KEY_TOAST_FLAG, false)){
                manager.setPreference(SharedPreferencesManager.KEY_TOAST_FLAG, true);
                Toast.makeText(getApplicationContext(),"Die Tastatursprache kann im Menü rechts oben angepasst werden.",Toast.LENGTH_LONG).show();
            }
        }

        language = manager.getString(SharedPreferencesManager.KEY_ACTIVE_LANGUAGE, "Englisch");

        ImageButton speech=(ImageButton)findViewById(R.id.speech);
        speech.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                IsSpeech = true;
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                String languageFormatted = HelperFunctions.getLanguageTagFromString(language);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageFormatted);
                try {
                    startActivityForResult(i, REQUEST_OK);//RecognizerIntent.LANGUAGE_MODEL_FREE_FORM  en-US zh-CN
                } catch (Exception e) {
                    Toast.makeText(ChatActivity.this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
                }
            }
        });

        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        ((ImageButton) findViewById(R.id.speech)).getDrawable().setColorFilter(porterDuffColorFilter);
        ((ImageButton) findViewById(R.id.speech)).setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * @deprecated shared preferences should handle this
     * @param language
     */
    @Deprecated
    public static void setLanguage (String language){
        //speachLanguage = language;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_OK  && resultCode==RESULT_OK) {
            ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            // findViewById(R.id.text1).val
            //Toast.makeText(getApplicationContext(), thingsYouSaid.get(0), Toast.LENGTH_SHORT).show();
            fragment.onVoiceInput(thingsYouSaid.get(0));
        }
    }

    public boolean Iskeyboardlanguagesame(){
        InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();
        String localeString = ims.getLocale();
        Locale locale = new Locale(localeString);
        String currentLanguage = locale.getDisplayLanguage();


        switch (currentLanguage) {
            case "en": currentLanguage ="en-US"; break;
            case "it":currentLanguage ="it_IT"; break;
            case "ru":currentLanguage ="ru_RU"; break;
            case "es":currentLanguage ="es_ES"; break;
            default: currentLanguage ="en-US"; break;
        }
        //Toast.makeText(getApplicationContext(),currentLanguage+"   "+language,Toast.LENGTH_LONG).show();

        return currentLanguage.equals(language);
    }


    public void updatePoints(int points){
        if(menu != null) {
            MenuItem item = menu.findItem(R.id.menu_points);
            if (item != null) {
                item.setTitle(String.valueOf(points));
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);
        this.menu = menu;

        // init points
        updatePoints(fragment.getCurrentPoints());

        // init mute symbol
        boolean isMuted = fragment.getMute();
        MenuItem item = menu.getItem(1);
        if(isMuted){
            item.setIcon(R.drawable.mute);
        }
        else {
            item.setIcon(R.drawable.speaker);
        }
        return true;
        /*
        menu.add(1, MENU_MUTE, MENU_MUTE, getResources().getString(R.string.menu_mute)).setIcon(R.drawable.mute);
        menu.add(2, MENU_KEYBOARD, MENU_KEYBOARD, getResources().getString(R.string.menu_keyboard));

        return super.onCreateOptionsMenu(menu);
        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_mute:
                boolean isMuted = fragment.getMute();

                fragment.setMute(!isMuted);
                if(!isMuted){
                    item.setIcon(R.drawable.mute);
                }
                else {
                    item.setIcon(R.drawable.speaker);
                }

                break;
            case R.id.menu_keyboard:
                startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
                break;
            case R.id.menu_points:
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
