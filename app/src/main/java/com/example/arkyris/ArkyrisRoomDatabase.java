package com.example.arkyris;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ArkeItem.class}, version = 1, exportSchema = false)
public abstract class ArkyrisRoomDatabase extends RoomDatabase {

    // abstract getting method for Dao
    public abstract EntryDao entryDao();

    // make volatile to ensure atomic access to variable, prevent
    // concurrency issues (volatile makes state true across all threads)
    private static volatile ArkyrisRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // create ArkyrisRoomDatabase as singleton so only one database at a time
    public static ArkyrisRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WordRoomDatabase.class) {
                if (INSTANCE == null) {
                    // databases here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ArkyrisRoomDatabase.class, "arkyris_database")
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

            // Placeholder to test changing colours of entries
            Integer[] colourArray = {R.color.red, R.color.blue, R.color.green};

            databaseWriteExecutor.execute(() -> {
                // Populate database in the background
                EntryDao dao = INSTANCE.entryDao();

                // if we have no words, create initial list
                if (dao.getAnyItem().length < 1) {
                    for (int i = 0; i <= colourArray.length - 1; i++) {
                        ArkeItem entry = new ArkeItem(colourArray[i], "31/03/2021", "21:21", 1);
                        dao.insert(entry);
                    }
                }
            });
        }

    };

}
