package com.lmu.pmg.sdiapp;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ChatEndActivity extends AppCompatActivity {

    private static final String PERFECT_SCORE_KEY = "perfect_score";

    private static final int POINTS_CORRECT = 10;

    private RatingBar ratingBar;
    private TextView maxItems;
    private TextView correctItems;
    private ImageView perfectImage;
    private TextView perfectText;

    private SharedPreferencesManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.chatend_title);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_end);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        manager = SharedPreferencesManager.getInstanceIfExists();

        ratingBar = (RatingBar)findViewById(R.id.percentBar);
        maxItems = (TextView)findViewById(R.id.textView7);
        correctItems = (TextView)findViewById(R.id.textView5);
        perfectImage = (ImageView)findViewById(R.id.imageView);
        perfectText = (TextView)findViewById(R.id.perfectText);

        setCorrectAnswers();
        setScorePercentage();
        setPerfectView();

        ((Button)findViewById(R.id.button_toMainmenu)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ChatEndActivity.this,  MainActivity.class );
                startActivity(intent);
            }
        });
        ((Button)findViewById(R.id.button_toChatSelection)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ChatEndActivity.this,  CategoryActivity.class );
                startActivity(intent);
            }
        });
        ((Button)findViewById(R.id.button_backToChat)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void setPerfectView(){
        boolean isPerfect = getIntent().getBooleanExtra(PERFECT_SCORE_KEY, false);
        if(isPerfect){
            perfectImage.setVisibility(View.VISIBLE);
            perfectText.setVisibility(View.VISIBLE);
        }else{
            perfectImage.setVisibility(View.INVISIBLE);
            perfectText.setVisibility(View.INVISIBLE);
        }
    }

    private void setCorrectAnswers(){
        int correctAnswers = manager.getInt(SharedPreferencesManager.KEY_CORRECT_ANSWERS,0);
        double overallTasks = manager.getInt(SharedPreferencesManager.KEY_CURRENT_OVERALL_SCORE,0) / POINTS_CORRECT;
        maxItems.setText(String.valueOf((int)overallTasks));
        correctItems.setText(String.valueOf(correctAnswers));
    }

    private void setScorePercentage(){
        int overallPoints = manager.getInt(SharedPreferencesManager.KEY_CURRENT_OVERALL_SCORE,0);
        int userPoints = manager.getInt(SharedPreferencesManager.KEY_CURRENT_USER_SCORE, 0);
        int percentage = (100 * userPoints / overallPoints);
        ratingBar.setMax(100);
        ratingBar.setProgress(percentage);
    }
}
