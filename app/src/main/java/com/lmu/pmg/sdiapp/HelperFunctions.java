package com.lmu.pmg.sdiapp;

import android.os.Build;

import java.util.Locale;

/**
 * Created by Q399980 on 17.01.2017.
 */
public class HelperFunctions {


    public static Locale getLocaleFromLanguageString (String languageString){
        String[] splittedTag = getLanguageTagFromString(languageString).split("-");
        Locale locale;
        if(splittedTag.length == 1) {
            locale = new Locale(splittedTag[0]);
        } else {
            locale = new Locale(splittedTag[0], splittedTag[1]);
        }
        return  locale;
    }


    public static String getLanguageTagFromString (String languageString) {

        switch (languageString.toLowerCase()) {
            case "english":
            case "englisch":
                return "en-US";
            case "italienisch":
                return "it-IT";
            case "russisch":
                return "ru-RU";
            case "español":
            case "espanol":
            case "spanisch":
                return "es-ES";
            case "arabic":
            case "arabisch":
                return "ar-EG";
            case "czech":
            case "tschechisch":
                return "cs";
            case "francais":
            case "französisch":
                return "fr-FR";
            case "holländisch":
            case "niederländisch":
                return "nl-NL";
            case "deutsch":
                return "de-DE";
            case "japanisch":
                return "ja";
            case "lateinisch":
                return "la";
            case "polnisch":
                return "pl";
            case "türkisch":
                return "tr";
            default:
                return "en-US";
        }


    }

    /**
     * Calculates the minimum number of edits needed to transform one string into the other.
     * --> Compares two strings and returns how similar these are. 0 is similar, the higher the more different.
     *
     * Copied from wikibooks:
     * https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
     * @param lhs String to compare
     * @param rhs Other String to compare
     * @return Levenshtein distance
     */
    public static int levenshteinDistance (CharSequence lhs, CharSequence rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        // the array of distances
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < len0; i++) cost[i] = i;

        // dynamically computing the array of distances

        // transformation cost for each letter in s1
        for (int j = 1; j < len1; j++) {
            // initial cost of skipping prefix in String s1
            newcost[0] = j;

            // transformation cost for each letter in s0
            for(int i = 1; i < len0; i++) {
                // matching current letters in both strings
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation
                int cost_replace = cost[i - 1] + match;
                int cost_insert  = cost[i] + 1;
                int cost_delete  = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            // swap cost/newcost arrays
            int[] swap = cost; cost = newcost; newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[len0 - 1];
    }
}
