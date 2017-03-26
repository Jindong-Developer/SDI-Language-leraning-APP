package com.lmu.pmg.sdiapp.Interfaces;

import android.support.annotation.Nullable;

import com.lmu.pmg.sdiapp.ChatState;

/**
 * Created by Jonas on 23.01.2017.
 */
public interface IChatLogic {

    /**
     * Starts the question/ answer dialog
     */
    void start ();

    /**
     * Checks if user input
     * @param input input of user
     * @param isKeyboardInput true for keyboard input, false for voice input
     */
    void onUserInput(String input, boolean isKeyboardInput);

    /**
     * Gets the solution (whole sentence)
     * @param chatState question or answer?
     * @return Full sentence of solution
     */
    String getSolutionSentence (ChatState chatState);

    /**
     * Chat state independent implementation of {@link #getSolutionSentence(ChatState)}
     * @return solution sentence of current question or answer
     */
    String getCurrentSolutionSentence ();

    /**
     * Gets the solution (single word)
     * If there are multiple solution words the first gets retrieved
     * @param chatState question or answer?
     * @return Solution word
     */
    String getSolutionWord (ChatState chatState);

    /**
     * Chat state independent implementation of {@link #getSolutionWord(ChatState)}
     * @return Solution word
     */
    String getCurrentSolutionWord ();

    /**
     * Gets the hint of the current searched word
     * Be careful to check for {@link #getRemainingHintsForCurrentWord()} first
     * will return null string if no hints are allowed anymore
     * @return hint: substring of solution word with length {@link #getUsedHintsForCurrentWord()}
     */
    @Nullable
    String getCurrentHint ();

    /**
     * Get current points of user
     * @return points user scored
     */
    int getPoints ();

    /**
     * Get remaining hints count for word
     */
    int getRemainingHintsForCurrentWord ();

    /**
     * Get already used hints count for word
     */
    int getUsedHintsForCurrentWord ();

}
