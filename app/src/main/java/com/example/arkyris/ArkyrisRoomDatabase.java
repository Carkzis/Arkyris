package com.example.arkyris;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {IrisEntryItem.class, ArkeEntryItem.class}, version = 12, exportSchema = false)
public abstract class ArkyrisRoomDatabase extends RoomDatabase {

    // abstract getting method for Dao
    public abstract IrisEntryDao irisEntryDao();
    public abstract ArkeEntryDao arkeEntryDao();

    // make volatile to ensure atomic access to variable, prevent
    // concurrency issues (volatile makes state true across all threads)
    private static volatile ArkyrisRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // create ArkyrisRoomDatabase as singleton so only one database at a time
    public static ArkyrisRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ArkyrisRoomDatabase.class) {
                if (INSTANCE == null) {
                    // databases here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ArkyrisRoomDatabase.class, "arkyris_database")
                            // wipe and rebuild instead of migrating if no
                            // migration object
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

            databaseWriteExecutor.execute(() -> {
                // Populate database in the background
                IrisEntryDao dao = INSTANCE.irisEntryDao();

            });

            databaseWriteExecutor.execute(() -> {
                // Populate database in the background
                ArkeEntryDao dao = INSTANCE.arkeEntryDao();

            });

        }

    };

}
