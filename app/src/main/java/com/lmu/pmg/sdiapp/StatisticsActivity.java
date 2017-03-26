package com.lmu.pmg.sdiapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.Set;

public class StatisticsActivity extends AppCompatActivity {

    private SharedPreferencesManager manager;
    private ProgressBar dialogProgressBar;
    private TextView dialogProcessText;
    private RatingBar scoreBar;
    private TextView perfectScoreText;
    private TextView currentPointsText;
    private ImageView perfectScoreImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.statistics_title);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        manager = SharedPreferencesManager.getInstanceIfExists();
        dialogProgressBar = (ProgressBar)findViewById(R.id.progressBar3);
        dialogProcessText = (TextView)findViewById(R.id.textView2);
        scoreBar = (RatingBar)findViewById(R.id.ratingBar);
        perfectScoreText = (TextView)findViewById(R.id.textView5);
        currentPointsText = (TextView)findViewById(R.id.currentPoints);
        perfectScoreImage = (ImageView)findViewById(R.id.imageView);
        setDialogCompletedPercentage();
        setScorePercentage();
        setPerfectRounds();
    }

    private void setPerfectRounds(){
        int perfectScores = manager.getInt(SharedPreferencesManager.KEY_PERFECT_ROUNDS, 0);
        if(perfectScores > 0){
            perfectScoreText.setText(String.valueOf(perfectScores));
            perfectScoreImage.setImageAlpha(255);
        }else{
            perfectScoreImage.setImageAlpha(100);
        }
    }

    private void setScorePercentage(){
        int overallPoints = manager.getInt(SharedPreferencesManager.KEY_OVERALL_SCORE, 1);
        int userPoints = manager.getInt(SharedPreferencesManager.KEY_USER_SCORE, 0);
        currentPointsText.setText("Aktuelle Punktzahl: " + String.valueOf(userPoints));
        double percentage = 100 * userPoints / overallPoints;
        scoreBar.setProgress((int)percentage);
    }

    private void setDialogCompletedPercentage(){
        String activeLanguage = manager.getString(SharedPreferencesManager.KEY_ACTIVE_LANGUAGE,
                getString(R.string.defaultLanguage));
        Set<String> dialogSet = manager.getStringSet(SharedPreferencesManager.KEY_DIALOGS_PER_LANGUAGE+activeLanguage, new HashSet<String>());
        int completedCount = 0;
        String completedValue = "[1]";
        for(String dialog:dialogSet){
            String result = dialog.substring(dialog.length() - 3);
            if(result.equals(completedValue)){
                completedCount++;
            }
        }
        double percentage = 0.0;
        if(dialogSet.size()>0) {
            percentage = 100 * completedCount / dialogSet.size();
        }
        dialogProgressBar.setProgress((int)percentage);
        dialogProcessText.setText(String.valueOf(percentage)+"%");
    }
}
