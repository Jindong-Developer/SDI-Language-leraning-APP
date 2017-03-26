package com.lmu.pmg.sdiapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    public static final String TAG = CategoryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.category_title);
        setContentView(R.layout.activity_category);

    }
}
