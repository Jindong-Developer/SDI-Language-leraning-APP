package com.lmu.pmg.sdiapp;

import android.support.annotation.Nullable;

import com.lmu.pmg.sdiapp.Interfaces.IChatList;
import com.lmu.pmg.sdiapp.Interfaces.IChatLogic;

import java.util.List;

/**
 * Created by Jonas on 23.01.2017.
 */
public class ChatLogic implements IChatLogic {

    public enum AnswerCorrectness {
        Correct,
        Typo,
        False
    }
    private static final String TAG = ChatLogic.class.getSimpleName();

    /** Max lev. distance to count as a typo. See {@link HelperFunctions#levenshteinDistance}*/
    private static final int LEVENSHTEIN_MAX = 2;

    private static final int POINTS_CORRECT = 10;
    private static final int POINTS_TYPO = 7;
    private static final int COST_HINT = 3;
    private static final int MAX_HINTS_PER_WORD = POINTS_CORRECT/COST_HINT;
    private int usedHints = 0;
    private ChatState chatState = ChatState.Answer; // will be set to Question initially to start with Question
    private List<ChatInstance> chatInstanceList;
    private ChatInstance currentQA;
    private SharedPreferencesManager manager;

    static final String PERSON_QUESTION = "Person";
    private static final String PERSON_ANSWER = "Me";

    private IChatList chatList;
    private int points;



    // private constructor to prevent false init
    ChatLogic(IChatList chatList, List<ChatInstance> chatInstanceList, int currentPoints){
        manager = SharedPreferencesManager.getInstanceIfExists();
        resetCurrentPoints();
        this.chatInstanceList = chatInstanceList;
        this.chatList = chatList;
        points = currentPoints;
    }

    @Override
    public void start (){
        showNextLine();
    }

    private void incrementChatState(){
        if(chatState == ChatState.Answer) chatState = ChatState.Question;
        else chatState = ChatState.Answer;
    }

    private void showNextLine (){
        //chatState = chatState.getNextState();
        incrementChatState();
        usedHints = 0;

        if(chatState == ChatState.Answer){

            if( ! currentQA.answerExists()){
                // no answer, just show next line
                showNextLine();
            }
            else {
                String answerSentence = currentQA.getAnswerWithBlanks();


                ChatLine chatLineAnswer = new ChatLine(PERSON_ANSWER, answerSentence);

                chatList.showNextLine(chatLineAnswer, ChatState.Answer);

                if( ! currentQA.answerHasBlanks()){
                    // no blanks, so show next line instantly
                    showNextLine();
                }
            }
        }
        else {
            if (chatInstanceList.size() == 0) {
                //TODO game is over. Do game over stuff.
                chatList.onGameOver(0); // TODO calc points
                Log.d(TAG, "Game is over");
                return;
            }

            currentQA = chatInstanceList.get(0);

            if( ! currentQA.questionExists()){
                // no question, just show next line
                showNextLine();
            }
            else {
                String questionSentence = currentQA.getQuestionWithBlanks();
                chatInstanceList.remove(0); // remove used item

                ChatLine chatLineQuestion = new ChatLine(PERSON_QUESTION, questionSentence);

                chatList.showNextLine(chatLineQuestion, ChatState.Question);

                if( ! currentQA.questionHasBlanks()){
                    // no blanks, so show next line instantly
                    showNextLine();
                }
            }
        }
    }

    /**
     * Checks if user input is correct, false or has a typo (Levenshtein distance <= {@link #LEVENSHTEIN_MAX})
     * @param input user input
     * @param chatState true if input is made on a Question or Answer
     * @return if Answer is correct, false or has a typo
     */
    private AnswerCorrectness checkIfInputIsCorrect (String input, ChatState chatState, boolean isTextInput){

        input = input.trim();
        List<String> gaps;
        if(chatState == ChatState.Answer) {
            gaps = currentQA.getGapsAnswer();
        }
        else {
            gaps = currentQA.getGapsQuestion();
        }

        for (String blank: gaps) {

            if(isTextInput){
                if (blank.equals(input)){ return AnswerCorrectness.Correct; }
                else if (HelperFunctions.levenshteinDistance(blank, input) <= LEVENSHTEIN_MAX) {
                    return AnswerCorrectness.Typo;
                }
            }
            else { //could be a whole sentence, thats why we check only for .contains()
                if(input.contains(blank)){
                    return AnswerCorrectness.Correct;
                }
            }
        }
        // no match/typo match found
        return AnswerCorrectness.False;
    }

    /**
     * User has filled out blank correctly.
     * Fills out according blank field with solution.
     * May show next field if current is completed.
     * Handles scoring
     */
    private void onCorrectInput (){

        chatList.showInputFeedback(AnswerCorrectness.Correct);
        if(currentQA.getDidMakeTypo(chatState)){
            points += POINTS_TYPO;
            updateStatisticAndShowNextLine(POINTS_TYPO);
        }
        else {
            points += POINTS_CORRECT;
            updateCorrectAnswers();
            updateStatisticAndShowNextLine(POINTS_CORRECT);
        }

    }

    private void updateCorrectAnswers(){
        int correctAnswers = manager.getInt(SharedPreferencesManager.KEY_CORRECT_ANSWERS,0);
        manager.setPreference(SharedPreferencesManager.KEY_CORRECT_ANSWERS, correctAnswers+1);
    }

    private void updateStatisticAndShowNextLine(int gainedPoints){
        updateOverallScore(gainedPoints);
        updateCurrentScore(gainedPoints);
        showNextLine();
    }

    private void updateOverallScore(int gainedPoints){
        int overallPoints = manager.getInt(SharedPreferencesManager.KEY_OVERALL_SCORE,0);
        int userPoints = manager.getInt(SharedPreferencesManager.KEY_USER_SCORE, 0);
        manager.setPreference(SharedPreferencesManager.KEY_OVERALL_SCORE, overallPoints+POINTS_CORRECT);
        manager.setPreference(SharedPreferencesManager.KEY_USER_SCORE, userPoints+gainedPoints);
    }

    private void updateCurrentScore(int gainedPoints){
        int overallPoints = manager.getInt(SharedPreferencesManager.KEY_CURRENT_OVERALL_SCORE,0);
        int userPoints = manager.getInt(SharedPreferencesManager.KEY_CURRENT_USER_SCORE, 0);
        manager.setPreference(SharedPreferencesManager.KEY_CURRENT_OVERALL_SCORE, overallPoints+POINTS_CORRECT);
        manager.setPreference(SharedPreferencesManager.KEY_CURRENT_USER_SCORE, userPoints+gainedPoints);
    }

    private void resetCurrentPoints(){
        manager.setPreference(SharedPreferencesManager.KEY_CURRENT_OVERALL_SCORE, 0);
        manager.setPreference(SharedPreferencesManager.KEY_CURRENT_USER_SCORE, 0);
        manager.setPreference(SharedPreferencesManager.KEY_CORRECT_ANSWERS, 0);
    }

    /**
     * User has made a typo.
     * Gives hint that input was close to correct.
     * Handles typo scoring
     */
    private void onTypoInput(){
        currentQA.setDidMakeTypo(chatState);
        chatList.showInputFeedback(AnswerCorrectness.Typo);
    }
    /**
     * User has made a false input.
     * Gives feedback on false input
     * May show next field if current is completed.
     * Handles false scoring
     */
    private void onFalseInput(){
        chatList.showInputFeedback(AnswerCorrectness.False);
        updateStatisticAndShowNextLine(0);
        // no points
    }
    /////////////////////////////////
    ////// INTERFACE FUNCTIONS //////
    /////////////////////////////////

    @Override
    public void onUserInput(String input, boolean isTextInput) {
        //ChatState chatState = currentQA.isQuestionOrAnswer();
        AnswerCorrectness result = checkIfInputIsCorrect(input, chatState, isTextInput);

        switch (result){
            case Correct:
                Log.d(TAG, "input CORRECT");
                onCorrectInput();
                break;
            case Typo:
                Log.d(TAG, "input TYPO");
                if (currentQA.getDidMakeTypo(chatState)){
                    onFalseInput();
                }
                else {
                    currentQA.setDidMakeTypo(chatState);
                    onTypoInput();
                }
                break;
            case False:
                Log.d(TAG, "input INCORRECT");
                onFalseInput();
                break;
        }
    }

    @Override
    public String getSolutionSentence(ChatState chatState) {

        if(chatState == ChatState.Answer) return currentQA.getAnswer();

        else return currentQA.getQuestion();
    }

    @Override
    public String getCurrentSolutionSentence() {
        return getSolutionSentence(chatState);
    }

    @Override
    public String getSolutionWord(ChatState chatState) {
        if(currentQA.getGapsAnswer().size() ==0)  return "";

        if(chatState == ChatState.Answer) return currentQA.getGapsAnswer().get(0);

        else return currentQA.getGapsQuestion().get(0);
    }

    @Override
    public String getCurrentSolutionWord() {
        return getSolutionWord(chatState);
    }

    @Override
    @Nullable
    public String getCurrentHint() {
        if(getRemainingHintsForCurrentWord() > 0) {
            onHintBought();
            if (chatState == ChatState.Answer) {
                return currentQA.getHintAnswer(usedHints);
            } else {
                return currentQA.getHintQuestion(usedHints);
            }
        }
        else return null;
    }

    private void onHintBought() {
        if(getRemainingHintsForCurrentWord() > 0) {
            points -= COST_HINT;
            usedHints++;
        }
    }

    @Override
    public int getPoints (){
        return points;
    }

    @Override
    public int getRemainingHintsForCurrentWord (){
        return MAX_HINTS_PER_WORD - usedHints;
    }

    @Override
    public int getUsedHintsForCurrentWord (){
        return usedHints;
    }
}
