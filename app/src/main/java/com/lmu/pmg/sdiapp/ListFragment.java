package com.lmu.pmg.sdiapp;

/**
 * Created by Iris on 08.01.2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.lmu.pmg.sdiapp.Interfaces.OnGetDataListener;

import java.util.ArrayList;

public class ListFragment extends android.support.v4.app.ListFragment implements AdapterView.OnItemClickListener{

    public static final String TAG = ListFragment.class.getSimpleName();
    private static final String LANGUAGE_KEY = "language";
    private static final String LEVEL_KEY = "level";
    private static final String CATEGORY_KEY = "category";
    private static final String TITLE_KEY = "title";
    private ArrayList<ListItem> listData = new ArrayList<ListItem>();
    private DBHelper dbHelper;
    private String language = "";
    private String level = "";
    private String category = "";

    @Override
    public void onActivityCreated( Bundle savedInstanceState ) {

        super.onActivityCreated( savedInstanceState );
        dbHelper = new DBHelper();
        fillList();
    }

    private void fillList(){
        Activity activity = getActivity();
        SharedPreferencesManager manager = SharedPreferencesManager.getInstance(getActivity());
        if(activity instanceof LanguageActivity){
            dbHelper.getLanguages(new OnGetDataListener() {
                @Override
                public void onSuccess(ArrayList<ListItem> data) {
                    listData = data;
                    setupListView();
                }

            });
        }else if (activity instanceof CategoryActivity){

            if(manager == null){
                Log.e(TAG, "ERROR getting SharedPreferencesManager instance");
            }

            String activeLanguage = manager.getString(SharedPreferencesManager.KEY_ACTIVE_LANGUAGE,
                    getString(R.string.defaultLanguage));
            language = activeLanguage;

            dbHelper.getCategoriesByLanguage(activeLanguage, new OnGetDataListener() {
                @Override
                public void onSuccess(ArrayList<ListItem> data) {
                    listData = data;
                    setupListView();

                }

            });
        }else if (activity instanceof LevelActivity){
            language = getActivity().getIntent().getStringExtra(LANGUAGE_KEY);
            category = getActivity().getIntent().getStringExtra(CATEGORY_KEY);
            dbHelper.getLevelsByCategoryAndLanguage(language,category, new OnGetDataListener() {
                @Override
                public void onSuccess(ArrayList<ListItem> data) {
                    listData = data;
                    setupListView();
                }
            });
        }else if (activity instanceof DialogSelectionActivity){
            language = getActivity().getIntent().getStringExtra(LANGUAGE_KEY);
            category = getActivity().getIntent().getStringExtra(CATEGORY_KEY);
            level = getActivity().getIntent().getStringExtra(LEVEL_KEY);

            final String activeDifficulty = manager.getString(SharedPreferencesManager.KEY_ACTIVE_DIFFICULTY,
                    getString(R.string.defaultDifficulty));


            dbHelper.getLevelsByCategoryAndLanguage(language, category, new OnGetDataListener() {
                @Override
                public void onSuccess(ArrayList<ListItem> data) {
                    ArrayList<String> availableLevels = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++) {
                        availableLevels.add(data.get(i).getName());
                    }
                    Spinner difficultySpinner = (Spinner) getActivity().findViewById(R.id.difficulty_spinner);
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter(getActivity(),
                            android.R.layout.simple_spinner_dropdown_item, availableLevels);
                    difficultySpinner.setAdapter(spinnerAdapter);

                    if (availableLevels.contains(activeDifficulty)) {
                        level = activeDifficulty;
                    }
                    else if (availableLevels.contains(Character.toUpperCase(activeDifficulty.charAt(0)) + activeDifficulty.substring(1))){
                        level = Character.toUpperCase(activeDifficulty.charAt(0)) + activeDifficulty.substring(1);
                    }
                    else if (availableLevels.size() != 0 || !availableLevels.contains(activeDifficulty)) {
                        level = availableLevels.get(0);
                        difficultySpinner.setSelection(spinnerAdapter.getPosition(level));
//                        Toast.makeText(getActivity(), getString(R.string.toast_difficulty_not_available, activeDifficulty), Toast.LENGTH_LONG).show();
                    }

                    difficultySpinner.setSelection(spinnerAdapter.getPosition(level));
                    getDialogues(language, category, level);
                }
            });
        }
    }

    private void getDialogues(String language, String category, String level) {
        this.dbHelper.getDialogsByLanguageCategoryAndLevel(this.language, this.category, this.level, new OnGetDataListener() {
            @Override
            public void onSuccess(ArrayList<ListItem> data) {
                listData = data;
                setupListView();
            }
        });
    }

    private ListView setupListView() {
        ListAdapter adapter = new ListAdapter( this.getContext(), R.id.fragmentName, listData );

        setListAdapter( adapter );
        ListView listView = getListView();
        listView.setBackgroundColor( Color.WHITE );
        listView.setOnItemClickListener(this);
        return listView;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
        ListItem item = listData.get( position);

        createIntents(item);
    }

    private void createIntents(ListItem item){
        Activity activity = getActivity();
        if(activity instanceof LanguageActivity){
            Intent intent = new Intent( getContext(), CategoryActivity.class );
            intent.putExtra(LANGUAGE_KEY, item.getName());
            startActivity(intent);
        }else if (activity instanceof CategoryActivity){
//            Intent intent = new Intent( getContext(), LevelActivity.class );

            Intent intent = new Intent( getContext(), DialogSelectionActivity.class );
            intent.putExtra(LANGUAGE_KEY, language);
            intent.putExtra(CATEGORY_KEY, item.getName());
            startActivity(intent);

        }else if (activity instanceof LevelActivity){
            Intent intent = new Intent( getContext(), DialogSelectionActivity.class );
            intent.putExtra(LEVEL_KEY, item.getName());
            intent.putExtra(LANGUAGE_KEY, language);
            intent.putExtra(CATEGORY_KEY, category);
            startActivity(intent);

        }else if (activity instanceof DialogSelectionActivity){
            Intent intent = new Intent( getContext(), ChatActivity.class );
            intent.putExtra("chatList", item.getChatList());
            intent.putExtra(TITLE_KEY, item.getName());
            startActivity(intent);
        }
    }

    public void updateFragmentData(String newLevel) {

        SharedPreferencesManager manager = SharedPreferencesManager.getInstanceIfExists();

        language = getActivity().getIntent().getStringExtra(LANGUAGE_KEY);
        category = getActivity().getIntent().getStringExtra(CATEGORY_KEY);
        level = newLevel;

        final String activeDifficulty = manager.getString(SharedPreferencesManager.KEY_ACTIVE_DIFFICULTY,
                getString(R.string.defaultDifficulty));

        dbHelper.getLevelsByCategoryAndLanguage(language, category, new OnGetDataListener() {
            @Override
            public void onSuccess(ArrayList<ListItem> data) {
                ArrayList<String> availableLevels = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    availableLevels.add(data.get(i).getName());
                }
                Spinner difficultySpinner = (Spinner) getActivity().findViewById(R.id.difficulty_spinner);
                ArrayAdapter<String> spinnerAdapter = (ArrayAdapter)difficultySpinner.getAdapter();

                if (availableLevels.contains(activeDifficulty)) {
                    level = activeDifficulty;
                }
                else if (availableLevels.contains(Character.toUpperCase(activeDifficulty.charAt(0)) + activeDifficulty.substring(1))){
                    level = Character.toUpperCase(activeDifficulty.charAt(0)) + activeDifficulty.substring(1);
                }
                else if (availableLevels.size() != 0 || !availableLevels.contains(activeDifficulty)) {
                    level = availableLevels.get(0);
                    difficultySpinner.setSelection(spinnerAdapter.getPosition(level));

//                  Toast.makeText(getActivity(), getString(R.string.toast_difficulty_not_available, activeDifficulty), Toast.LENGTH_LONG).show();
                }
                difficultySpinner.setSelection(spinnerAdapter.getPosition(level));
                getDialogues(language, category, level);
            }
        });
    }
}
