package com.lmu.pmg.sdiapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.List;

public class DialogSelectionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_selection);
        setTitle(R.string.dialo_selection_title);

        // Set up difficulty dropdown menu
        Spinner difficultySpinner = (Spinner) findViewById(R.id.difficulty_spinner);
        difficultySpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        SharedPreferencesManager appSettings = SharedPreferencesManager.getInstance(this);
        Spinner spinner = (Spinner) parent;
        if (count == 0) {
            count++;
            return;
        }

        if (spinner.getId() == R.id.difficulty_spinner) {
            android.util.Log.d("Selected Difficulty", "" + parent.getItemAtPosition(position));
            appSettings.setPreference(SharedPreferencesManager.KEY_ACTIVE_DIFFICULTY, parent.getItemAtPosition(position)+"");

            FragmentManager fm = getSupportFragmentManager();
            List<Fragment> fragments = fm.getFragments();
            ListFragment currentFragment = (ListFragment) fragments.get(fragments.size() - 1);

            if (currentFragment != null) {
                currentFragment.updateFragmentData(parent.getItemAtPosition(position)+"");
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }

}
