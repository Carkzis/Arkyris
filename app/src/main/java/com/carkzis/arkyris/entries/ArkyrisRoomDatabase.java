package com.carkzis.arkyris.entries;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Abstract class to store local database for the application.
 */
@Database(entities = {IrisEntryItem.class, ArkeEntryItem.class}, version = 14, exportSchema = false)
public abstract class ArkyrisRoomDatabase extends RoomDatabase {

    /**
     * Abstract getting methods for IrisEntryDao and ArkeEntryDao.
     */
    public abstract IrisEntryDao irisEntryDao();
    public abstract ArkeEntryDao arkeEntryDao();

    /**
     * Make volatile to ensure atomic access to variable, prevent concurrency issues
     * a volatile makes state true across all threads.
     */
    private static volatile ArkyrisRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * Create database as a singleton so only one database is created at a time.
     */
    public static ArkyrisRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ArkyrisRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Database(s) here.
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ArkyrisRoomDatabase.class, "arkyris_database")
                            // Wipe and rebuild instead of migrating if no migration object.
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Populates the Room database in the background.
     */
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                // Populate database in the background.
                INSTANCE.irisEntryDao();
            });
            databaseWriteExecutor.execute(() -> {
                // Populate database in the background.
                INSTANCE.arkeEntryDao();
            });
        }
    };
}