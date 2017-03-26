package com.lmu.pmg.sdiapp;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Iris on 08.01.2017.
 */

class ChatInstance implements Serializable{

    public static final String TAG = ListFragment.class.getSimpleName();
    private static final String BLANK = " _____ ";
    static final String GAP_PLACEHOLDER = "[]";

    private String question;
    private String answer;
    private List<String> gapsQ;
    private List<String> gapsA;
    private boolean questionSolved = false;
    private boolean answerSolved = false;
    private boolean didMakeTypoQuestion = false;
    private boolean didMakeTypoAnswer = false;

    ChatInstance(String question, String answer, List<String> gapsQuestion, List<String> gapsAnswer){

        this.question = question;
        this.answer = answer;
        this.gapsQ = gapsQuestion;
        this.gapsA = gapsAnswer;
    }

    String getAnswer() {

        if(gapsA.size() ==0) return "";
        return answer.replace(GAP_PLACEHOLDER, gapsA.get(0));
    }

    List<String> getGapsQuestion() {
        return gapsQ;
    }

    List<String> getGapsAnswer() {
        return gapsA;
    }

    String getQuestion() {
        if(gapsQ.size() ==0) return "";
        return question.replace(GAP_PLACEHOLDER, gapsQ.get(0));
    }

    String getAnswerWithBlanks (){
        return insertBlanks(answer);
    }

    String getQuestionWithBlanks (){
        return insertBlanks(question);
    }

    boolean questionHasBlanks () { return question.contains(GAP_PLACEHOLDER);}

    boolean answerHasBlanks () { return answer.contains(GAP_PLACEHOLDER);}

    boolean questionExists () { return !question.isEmpty(); }

    boolean answerExists () { return !answer.isEmpty(); }

    public void questionIsSolved (){
        questionSolved = true;
    }

    public void answerIsSolved () {
        answerSolved = true;
    }

    public String getHintQuestion (int hintNum) { return getSubstringOrAll(gapsQ.get(0), hintNum); }

    public String getHintAnswer (int hintNum) { return getSubstringOrAll(gapsA.get(0), hintNum); }

    private String getSubstringOrAll(String string, int index){
        if(string.length() >= index){
            return string.substring(0, index);
        }
        else return string;
    }
    /**
     * Returns if user did make a typo on input
     * @param state question or answer
     * @return true if typo happened
     */
    boolean getDidMakeTypo (ChatState state) {
        if (state == ChatState.Question) return didMakeTypoQuestion;
        else return didMakeTypoAnswer;
    }

    void setDidMakeTypo (ChatState state) {
        if (state == ChatState.Question) didMakeTypoQuestion = true;
        else didMakeTypoAnswer = true;
    }

    ChatState isQuestionOrAnswer (){
        if(questionSolved) return ChatState.Answer;
        else return ChatState.Question;
    }

    public boolean chatInstanceIsSolved (){
        return questionSolved && answerSolved;
    }


    /**
     * Replaces a given word in a sentence with blank spaces
     * @param fullSentence Whole sentence with words to cut out
     * @return formatted String with blank space instead of word
     */
    //private String insertBlanks (String fullSentence, List<String> gaps){
    private String insertBlanks (String fullSentence){
        return fullSentence.replace(GAP_PLACEHOLDER, BLANK);
        /*
        String[] blankWords = gaps.toArray(new String[gaps.size()]);

        // FIXME dirty hack
        String regex = blankWords[0] + "|" + blankWords[0].toUpperCase().charAt(0) + blankWords[0].substring(1); // "/(?" +
        if(blankWords.length > 1){
            for (int i=1; i < blankWords.length; i++) {
                regex += "|" + blankWords[i];
            }
        }
        //regex += ")/g"; // should this be case insensitive? (/i)
        //regex += ")";
        String[] sentenceParts = fullSentence.split(regex, Pattern.CASE_INSENSITIVE);

        String formatted = sentenceParts[0];
        if(sentenceParts.length <= 1) {
            Log.e(TAG, "No blanks inserted, splitting String not successful: " + fullSentence + " split regex is: " + regex);
        }
        for(int i=1; i<sentenceParts.length; i++){
            formatted += BLANK + sentenceParts[i];
        }
        // TODO check if formatting works correct
        Log.d(TAG, "Formatted String is: " + formatted + ". Regex is: " + regex);
        return formatted;
        */
    }
}
