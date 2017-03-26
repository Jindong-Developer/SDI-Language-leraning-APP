package com.lmu.pmg.sdiapp;

import android.text.Html;
import android.text.Spanned;

import java.util.List;

/**
 * Created by Iris on 08.01.2017.
 */

public class ChatLine {

    private String person;
    private String text;
    private Spanned spannedText;


    public ChatLine(String person, String text) {
        this.person = person;
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setText(Spanned text){
        spannedText = text;
    }

    public String getText() {
        return text;
    }

    public Spanned getSpannedText() {
        if(spannedText == null){
            return Html.fromHtml(text);

        }
        else return spannedText;
    }

    public String getPerson() {
        return person;
    }

}
