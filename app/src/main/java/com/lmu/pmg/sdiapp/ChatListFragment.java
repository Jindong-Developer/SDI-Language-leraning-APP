package com.lmu.pmg.sdiapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.lmu.pmg.sdiapp.Interfaces.IChatList;
import com.lmu.pmg.sdiapp.Interfaces.IChatLogic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Iris on 08.01.2017.
 */

public class ChatListFragment extends ListFragment implements AdapterView.OnItemClickListener, IChatList {

    public static final String TAG = ChatListFragment.class.getSimpleName();

    private static final String TITLE_KEY = "title";
    private static final String PERFECT_SCORE_KEY = "perfect_score";

    private String COLOR_FALSE;
    private String COLOR_CORRECT;

    private ArrayList<ChatLine> listData = new ArrayList<ChatLine>(); // list which gets displayed

    private ChatListAdapter adapter;
    private IChatLogic chatLogic;

    private EditText chatInput;
    private FloatingActionButton buttonSend;
    private FloatingActionButton buttonHint;
    Context context;
    private InputMethodManager imm;
    private TextToSpeech textToSpeech;
    private static boolean isTTS;
    private String title = "";
    ListView listView;
    private SharedPreferencesManager manager;
    private boolean isPerfect = false;
    private boolean gameEnded = false;



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        COLOR_FALSE = getResources().getString(0+R.color.corporate_red).substring(3);
        COLOR_CORRECT = getResources().getString(0+R.color.corporate_green).substring(3);

        List<ChatInstance> chatList = (ArrayList<ChatInstance>)getActivity().getIntent().getSerializableExtra("chatList");
        //generateChatList(list);
        // showNextLine(); // now triggered by ChatLogic
        title = getActivity().getIntent().getStringExtra(TITLE_KEY);
        setupListView();
        //chatLogic = ChatLogic.getInstance(this, chatList);

        SharedPreferencesManager preferencesManager = SharedPreferencesManager.getInstanceIfExists();
        isTTS = preferencesManager.getBoolean(SharedPreferencesManager.KEY_MUTE, true);

        int points = preferencesManager.getInt(SharedPreferencesManager.KEY_POINTS, 0);
        chatLogic = new ChatLogic(this, chatList, points);
        chatLogic.start();

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        chatInput = (EditText) getActivity().findViewById(R.id.chat_input);

        buttonSend = (FloatingActionButton) getActivity().findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String inputText = chatInput.getText().toString();
                onUserInput(inputText);
            }
        });

        buttonHint = (FloatingActionButton) getActivity().findViewById(R.id.button_hint);
        buttonHint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectIfDialog();
            }
        });

        context = getActivity();



        textToSpeech =new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    SharedPreferencesManager manager = SharedPreferencesManager.getInstance(context);
                    String lang = manager.getString(SharedPreferencesManager.KEY_ACTIVE_LANGUAGE, "Englisch");

                    textToSpeech.setLanguage(HelperFunctions.getLocaleFromLanguageString(lang));
                    Log.d(TAG, "TTS language is: " + HelperFunctions.getLocaleFromLanguageString(lang).toString());
                }
                else {
                    Log.e(TAG, "ERROR initializing text-to-speech");
                }
            }
        });

    }

    public void onVoiceInput (String input){
        String solution = chatLogic.getCurrentSolutionWord();
        if (solution.toLowerCase().equals(input.toLowerCase())){
            input = solution;
        }
        chatInput.setText(input);
    }

    public int getCurrentPoints (){
        return chatLogic.getPoints();
    }

    private void selectIfDialog(){

        if(chatLogic.getRemainingHintsForCurrentWord() > 0){
            if(chatLogic.getUsedHintsForCurrentWord() == 0){
                showHintDialog();
            }
            else {
                showNextHint();
            }
        }
    }

    private void updatePointsView(){
        int points = chatLogic.getPoints();
        ((ChatActivity) getActivity()).updatePoints(points);
    }

    private void showNextHint(){
        String hint = chatLogic.getCurrentHint();
        if(hint != null) {
            chatInput.setText(String.valueOf(hint));
            chatInput.setSelection(hint.length());
        }
        if(chatLogic.getRemainingHintsForCurrentWord() == 0){
            buttonHint.setAlpha(0.5f);
        }
        updatePointsView();
    }

    @Override
    public void onPause() {
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {

        int points = chatLogic.getPoints();
        SharedPreferencesManager preferencesManager = SharedPreferencesManager.getInstanceIfExists();
        preferencesManager.setPreference(SharedPreferencesManager.KEY_POINTS, points);
        preferencesManager.setPreference(SharedPreferencesManager.KEY_MUTE, isTTS);
        super.onDestroyView();
    }

    private void setupListView() {
        adapter = new ChatListAdapter(this.getContext(), R.id.chatName, R.id.chatText, listData);

        setListAdapter(adapter);
        listView = getListView();
        listView.setDivider(new ColorDrawable(0xEEEEEE));
        listView.setDividerHeight(16);
        listView.setOnItemClickListener(this);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setStackFromBottom(true);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        chatInput.requestFocus();
        imm.showSoftInput(chatInput, InputMethodManager.SHOW_IMPLICIT);

    }



    /**
     * Get next line, format with blanks and send to view.
     */
    @Override
    public void showNextLine(ChatLine lineWithBlanks, ChatState qADistinguish){

        listData.add(lineWithBlanks);

        // show changes
        adapter.notifyDataSetChanged();
        scrollListViewToBottom();
        if(buttonHint != null) { // was null at beginning somehow???
            buttonHint.setAlpha(1f);
        }
        updatePointsView();

    }

    private void scrollListViewToBottom() {
        getListView().post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                getListView().setSelection(getListView().getAdapter().getCount() - 1);
            }
        });
    }

    @Override
    public void showInputFeedback(ChatLogic.AnswerCorrectness correctness) {
        String updatedSentence = "";
        switch (correctness) {
            case False:
                chatInput.setText("");
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                updatedSentence = chatLogic.getCurrentSolutionSentence();

                String correctedWord = chatLogic.getCurrentSolutionWord();

                updateCorrectedSentence(updatedSentence, correctedWord, COLOR_FALSE);

                // wont work, because string conversion won't support the HTML tags anymore, they are just gone then
                // String correctedSentence = chatLogic.getCurrentSolutionSentence().replace(ChatInstance.GAP_PLACEHOLDER, falseColoredWord);
                //Log.d(TAG, "correctedSentence: " + correctedSentence);

                break;
            case Correct:
                chatInput.setText("");
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                updatedSentence = chatLogic.getCurrentSolutionSentence();
                String correctWord = chatLogic.getCurrentSolutionWord();
                updateCorrectedSentence(updatedSentence, correctWord, COLOR_CORRECT);
                break;
            case Typo:
                // show hint?
                Toast.makeText(getActivity(), R.string.typo_hint, Toast.LENGTH_LONG).show();

                break;
        }

    }

    private void updateCorrectedSentence (String solutionSentence, String highlightWord, String highlightRGBString){

        String correctWord = chatLogic.getCurrentSolutionWord();


        String[] sentenceParts = solutionSentence.split(correctWord);

        String highlightedWord = "<font color=\"#"+highlightRGBString+"\">" + correctWord + "</font>";
        String updatedSentence;
        if(sentenceParts.length == 2){
            updatedSentence = sentenceParts[0] + highlightedWord + sentenceParts[1];
        }
        else {
            updatedSentence = solutionSentence;
        }
        int lastIndex = listData.size() -1;
        Spanned falseColoredSentence = Html.fromHtml(updatedSentence);
        listData.get(lastIndex).setText(falseColoredSentence);
        adapter.notifyDataSetChanged();
        speak(falseColoredSentence.toString());

        chatInput.setText("");
    }

    private void speak (String toSpeak){
        if ( ! isTTS) return;

        if(Build.VERSION.SDK_INT >= 21){
            textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onGameOver(int points) {
        manager = SharedPreferencesManager.getInstanceIfExists();
        gameEnded = true;
        updateCompletedDialogs();
        checkPerfectResults();
        Toast.makeText(getActivity(), getString(R.string.chat_end_toast), Toast.LENGTH_LONG).show();

        Thread thread =  new Thread(){
            @Override
            public void run(){
                try {
                    synchronized(this){
                        wait(4000);
                    }
                }
                catch(InterruptedException ex){
                }
                finally {
                    Intent intent = new Intent(context, ChatEndActivity.class);
                    intent.putExtra(PERFECT_SCORE_KEY, isPerfect);
                    startActivity(intent);
                }
            }
        };
        thread.start();
    }

    private void checkPerfectResults(){
        int overallPoints = manager.getInt(SharedPreferencesManager.KEY_CURRENT_OVERALL_SCORE,0);
        int userPoints = manager.getInt(SharedPreferencesManager.KEY_CURRENT_USER_SCORE, 0);
        if(userPoints==overallPoints){
            int perfectScores = manager.getInt(SharedPreferencesManager.KEY_PERFECT_ROUNDS, 0);
            manager.setPreference(SharedPreferencesManager.KEY_PERFECT_ROUNDS, perfectScores+1);
            isPerfect = true;
        }
    }

    private void showFinishedDialog(){
        new AlertDialog.Builder(context)
                .setTitle("Glückwunsch!")
                .setMessage("Du hast den Dialog erfolgreich beendet und kannst dir jetzt deine Ergebnisse ansehen.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, ChatEndActivity.class);
                        intent.putExtra(PERFECT_SCORE_KEY, isPerfect);
                        startActivity(intent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showHintDialog(){
        new AlertDialog.Builder(context)
                .setTitle(getString(R.string.chat_hint_title))
                .setMessage(getString(R.string.chat_hint_text))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //hintPopup();
                        showNextHint();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_menu_help)
                .show();
    }

    private void updateCompletedDialogs(){
        String currentLanguage = manager.getString(SharedPreferencesManager.KEY_ACTIVE_LANGUAGE, getResources().getString(R.string.defaultLanguage));
        Set<String> dialogs = manager.getStringSet(SharedPreferencesManager.KEY_DIALOGS_PER_LANGUAGE+currentLanguage, new HashSet<String>());
        Set<String> newDialogs = generateNewDialog(dialogs);
        manager.setPreference(SharedPreferencesManager.KEY_DIALOGS_PER_LANGUAGE+currentLanguage, newDialogs);
    }

    private Set<String> generateNewDialog(Set<String> dialogs){
        Set<String> newDialogs = new HashSet<>();
        for(String dialog:dialogs){
            String dialogWithoutStatus = dialog.substring(0,dialog.length()-3);
            String newDialog = dialog;
            if(dialogWithoutStatus.equals(title)){
                newDialog = dialog.replace("[0]", "[1]");
            }
            newDialogs.add(newDialog);
        }
        return newDialogs;
    }

    /**
     * Mutes/Unmutes text-to-speech output
     * @param isMute true for mute
     */
    public void setMute (boolean isMute){
        isTTS = !isMute;
        if(textToSpeech.isSpeaking()){
            textToSpeech.stop();
        }
    }

    public boolean getMute (){
        return !isTTS;
    }


    /**
     * Handles user input and checks if game continues
     * @param input user input
     */
    public void onUserInput(String input){
        boolean isKeyboardInput = true; // TODO get if it's voice or keyboard input
        chatLogic.onUserInput(input, isKeyboardInput);
    }

    public void hintPopup(){
        if(chatLogic.getRemainingHintsForCurrentWord() > 0){
            String hint = chatLogic.getCurrentHint();
            if(hint != null) {
                chatInput.setText(String.valueOf(hint));
                chatInput.setSelection(hint.length());
            }
            if(chatLogic.getRemainingHintsForCurrentWord() == 0){
                buttonHint.setAlpha(0.5f);
            }
        }
    }

    @Override
    public void onResume() {

        if (gameEnded) {
            LinearLayout inputBar = (LinearLayout) getActivity().findViewById(R.id.linlay);
            if(inputBar.getChildCount() > 0)
                (inputBar).removeAllViews();

            Button toResultsButton = new Button(getActivity());
            toResultsButton.setLayoutParams(new FrameLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            toResultsButton.setText(getString(R.string.button_toResults));
            toResultsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatEndActivity.class);
                    intent.putExtra(PERFECT_SCORE_KEY, isPerfect);
                    startActivity(intent);
                }
            });
            inputBar.addView(toResultsButton);
        }
        super.onResume();
    }
/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_mute:

                isTTS = !isTTS;
                if(isTTS){
                    item.setIcon(R.drawable.speaker);
                }
                else {
                    item.setIcon(R.drawable.mute);
                }

                break;
            case R.id.menu_keyboard:
                startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
    */

}
