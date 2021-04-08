package com.example.arkyris;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Random;

public class SandboxActivity extends AppCompatActivity {

    private static final String LOG_TAG = SandboxActivity.class.getSimpleName();

    // all activity interactions are with the WordViewModel only
    private WordViewModel mWordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sandbox);

        // Initialise toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.sandbox_recyclerview);
        final WordListAdapter adapter = new WordListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // associated the ViewModel with the controller, this persists through config changes
        mWordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);

        // an observer sees when the data is changed while the activity is open,
        // and updates the data in the adapter
        mWordViewModel.getAllWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                // update cached copy of words in adapter
                adapter.setWords(words);
            }
        });

        final FloatingActionButton fab = findViewById(R.id.sandbox_fab);
        fab.setOnClickListener(view -> {
            // this will add the colour in the circle to the user's diary
            Random rand = new Random();
            String randInt = String.valueOf(rand.nextInt(1000));
            Word word = new Word(randInt);
            mWordViewModel.insert(word);
        });
    }


}
