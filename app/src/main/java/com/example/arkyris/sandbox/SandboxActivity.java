package com.example.arkyris.sandbox;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arkyris.R;
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

        // can swipe items to delete the items
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    // note: we aren't doing anything with the onMove here
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        // get position of adapter, so we delete the correct item
                        int position = viewHolder.getAdapterPosition();
                        Word myWord = adapter.getWordAtPosition(position); // as defined in adapter
                        Toast.makeText(SandboxActivity.this, "Deleting " +
                                myWord.getWord(), Toast.LENGTH_LONG).show();
                        // Delete the word, remember the ViewModel is what interacts with database
                        mWordViewModel.deleteWord(myWord);
                    }
                }
        );
        helper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_sandbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.clear_data) {
            // Add a toast just for confirmation
            Toast.makeText(this, "Clearing the data...",
                    Toast.LENGTH_SHORT).show();

            // Delete the existing data
            mWordViewModel.deleteAll();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
