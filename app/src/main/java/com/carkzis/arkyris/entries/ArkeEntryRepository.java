package com.carkzis.arkyris.entries;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.carkzis.arkyris.APIUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for dealing with the communication between the app and
 * the Django Rest Framework, dealing purely with public entries.
 */
public class ArkeEntryRepository {

    private static final String LOG_TAG = ArkeEntryRepository.class.getSimpleName();

    // Add member variables for DAO and list of words.
    private final ArkeEntryDao mArkeEntryDao;
    private final EntryService mEntryService = APIUtils.getEntryService();
    private final SharedPreferences mPreferences;

    private final MutableLiveData<String> mLoadingOutcome;
    private final MutableLiveData<Boolean> mEntryAdded;
    private final MutableLiveData<String> mAccountName;

    private List<ArkeEntryItem> mEntriesList = new ArrayList<>();
    private final LiveData<List<ArkeEntryItem>> mPublicEntries;

    /**
     * Constructor to get handle to database and initialise member variables.
     */
    ArkeEntryRepository(Application application) {
        ArkyrisRoomDatabase db = ArkyrisRoomDatabase.getDatabase(application);
        mArkeEntryDao = db.arkeEntryDao();
        mPublicEntries = mArkeEntryDao.getAllPublicEntries();

        // Initialise variables for getting account name of user currently logged in.
        mPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        mAccountName = new MutableLiveData<>();
        mLoadingOutcome = new MutableLiveData<>();
        mEntryAdded = new MutableLiveData<>();
    }

    /**
     * Wrapper method for returning cached entries as LiveData, as Room executes
     * queries on a separate thread. Items displayed to the UI are always retrieved from Room.
     */
    LiveData<List<ArkeEntryItem>> getAllPublicEntries() {
        return mPublicEntries;
    }

    /**
     * Wrapper method for inserting an entry into the local database.
     */
    public void insert(ArkeEntryItem entry) {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> mArkeEntryDao.insert(entry));
    }

    /**
     * Wrapper method for inserting all entries retrieved from the remote database
     * into the local Room database.
     */
    public void insertAll(List<ArkeEntryItem> entries) {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> {
            mArkeEntryDao.deleteAll();
            mArkeEntryDao.insertAll(entries);
            /*
             Delay method to prevent loading complete value reaching the
             fragment until all the items/recycler view has been updated
             */
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mLoadingOutcome.postValue("complete");
        });
    }

    /**
     * Wrapper methods for returning the username from the Shared Preferences.
     */
    public MutableLiveData<String> getAccountName() {
        String username = mPreferences.getString("username", null);
        mAccountName.postValue(username);
        return mAccountName;
    }

    /**
     * Wrapper methods for returning LiveData.
     */
    public MutableLiveData<String> getLoadingOutcome() {
        return mLoadingOutcome;
    }
    public MutableLiveData<Boolean> getEntryAdded() {
        return mEntryAdded;
    }

    /**
     * Method for refreshing the data held in the arke_entry_table.
     */
    public void refreshArkeCache() {
        // This will load the items from the remote database.
        Call<List<ArkeEntryItem>> call = mEntryService.getPublicEntries();
        call.enqueue(new Callback<List<ArkeEntryItem>>() {
            /**
             * Method called when a response is received.
             */
            @Override
            public void onResponse(@NotNull Call<List<ArkeEntryItem>> call,
                                   @NotNull Response<List<ArkeEntryItem>> response) {
                if (response.isSuccessful()) {
                    mEntriesList = response.body();
                    Log.e(LOG_TAG, String.valueOf(response.body()));
                    // This will overwrite entries already there, and add new entries.
                    insertAll(mEntriesList);
                }
            }

            /**
             * Method called when there is no response from the server.
             */
            @Override
            public void onFailure(@NotNull Call<List<ArkeEntryItem>> call, @NotNull Throwable t) {
                Log.e(LOG_TAG, t.getMessage());
                mLoadingOutcome.postValue("error");
            }
        });
    }

    /**
     * Add colour entry to the backend postgreSQL database. It will always be set as public (1)
     * when done from the ArkeFragment.
     */
    public void addRemoteEntry(int colour) {
        ArkeEntryItem entry = new ArkeEntryItem(mAccountName.getValue(), colour, 1);
        Call<ArkeEntryItem> call = mEntryService.addEntry(entry);
        call.enqueue(new Callback<ArkeEntryItem>() {
            /**
             * Method called when a response is received.
             */
            @Override
            public void onResponse(@NotNull Call<ArkeEntryItem> call,
                                   @NotNull Response<ArkeEntryItem> response) {
                if (response.isSuccessful()) {
                    mEntryAdded.postValue(true);
                    refreshArkeCache();
                }
            }

            /**
             * Method called when there is no response from the server.
             */
            @Override
            public void onFailure(@NotNull Call<ArkeEntryItem> call,
                                  @NotNull Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                mLoadingOutcome.postValue("error");
            }
        });
    }

}
