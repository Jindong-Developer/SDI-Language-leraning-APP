package com.lmu.pmg.sdiapp.Interfaces;

import com.lmu.pmg.sdiapp.ChatLine;
import com.lmu.pmg.sdiapp.ChatLogic;
import com.lmu.pmg.sdiapp.ChatState;

/**
 * Created by Jonas on 23.01.2017.
 */
public interface IChatList {

    /**
     * Displays the next line in the chat
     * @param lineWithBlanks Question/Answer with blanks already cut out
     * @param qADistinguish is it the Question or the Answer?
     */
    void showNextLine (ChatLine lineWithBlanks, ChatState qADistinguish);

    /**
     * Shows feedback for user input
     * @param correctness If the input was correct, incorrect or had a typo
     */
    void showInputFeedback (ChatLogic.AnswerCorrectness correctness);


    /**
     * Game is over.
     * @param points Score of the user
     */
    void onGameOver (int points);
}
