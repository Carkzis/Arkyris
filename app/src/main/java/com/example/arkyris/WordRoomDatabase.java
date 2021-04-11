package com.example.arkyris;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Word.class}, version = 1, exportSchema = false)
public abstract class WordRoomDatabase extends RoomDatabase {

    // Need an abstract getter method for each @Dao
    public abstract WordDao wordDao();

    // make volatile to ensure atomic access to variable, prevent
    // concurrency issues (volatile makes state true across all threads)
    private static volatile WordRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // create WordRoomDatabase as singleton so only one database at a time
    public static WordRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WordRoomDatabase.class) {
                if (INSTANCE == null) {
                    // databases here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WordRoomDatabase.class, "word_database")
                            // wipe and rebuild instead of migrating if no
                            // migration object
                            // TODO: look into migrations
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            String[] words = {"0", "500", "1000"};

            databaseWriteExecutor.execute(() -> {
                // Populate database in the background
                WordDao dao = INSTANCE.wordDao();

                // if we have no words, create initial list
                if (dao.getAnyWord().length < 1) {
                    for (int i = 0; i <= words.length - 1; i++) {
                        Word word = new Word(words[i]);
                        dao.insert(word);
                    }
                }
            });
        }

    };
}
