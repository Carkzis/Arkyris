package com.example.arkyris;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IrisFragment extends Fragment {

    private static final String LOG_TAG = IrisFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private IrisListAdapter mAdapter;
    private int mColourName;
    EntryService entryService;
    List<IrisEntryItem> entriesList = new ArrayList<IrisEntryItem>();
    private String mAccountName;

    private boolean cacheOnce = true;

    // all activity interactions are with the WordViewModel only
    private IrisViewModel mIrisViewModel;

    // Random colours
    private static final String[] mColourArray = {"red", "pink", "purple", "deep_purple",
            "indigo", "blue", "light_blue", "cyan", "teal", "green",
            "light_green", "lime", "yellow", "amber", "orange", "deep_orange",
            "brown", "grey", "blue_grey", "pink_dark"};

    public IrisFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment IrisFragment.
     */
    public static IrisFragment newInstance() {
        IrisFragment fragment = new IrisFragment();
        Bundle args = new Bundle();
        // Currently no arguments here.
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Currently no arguments here.
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshEntriesList();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_iris, container, false);
        entryService = APIUtils.getEntryService();

        // associated the ViewModel with the controller, this persists through config changes
        mIrisViewModel = ViewModelProviders.of(this).get(IrisViewModel.class);

        // Retrieve account name held in SharedPreferences
        mIrisViewModel.getAccountName().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String username) {
                // update cached copy of words in adapter
                mAccountName = username;
            }
        });

        final FloatingActionButton fab = rootView.findViewById(R.id.iris_fab);
        fab.setOnClickListener(view -> {
            // this will add the colour in the circle to the user's diary
            makePublicAlert();
        });

        final ImageView image = rootView.findViewById(R.id.chosen_colour);
        mColourName = changeColour();
        image.setColorFilter(mColourName);

        image.setOnClickListener(view -> {
            // get a random colour, to initially display to the user in the color picker dialogue.
            mColourName = changeColour();
            // This will open a dialogue to enter a colour.
            openColourPickerDialogue(image);
        });

        // Get a handler for the RecyclerView
        mRecyclerView = rootView.findViewById(R.id.iris_recyclerview);
        // Create an adapter and supply the data
        mAdapter = new IrisListAdapter(getActivity());
        // Connect adapter to RecyclerView
        mRecyclerView.setAdapter(mAdapter);
        // Give RecyclerView a LayoutManager, make it horizontal
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.HORIZONTAL,
                false);
        mRecyclerView.setLayoutManager(layoutManager);



        // This will load the items from the database
        Call<List<IrisEntryItem>> call = entryService.getPrivateEntries(mAccountName);
        call.enqueue(new Callback<List<IrisEntryItem>>() {
             @Override
             public void onResponse(Call<List<IrisEntryItem>> call, Response<List<IrisEntryItem>> response) {
                 if (response.isSuccessful()) {
                     Log.e(LOG_TAG, "Entries called.");
                     entriesList = response.body();
                     mAdapter.setEntries(entriesList);
                     rootView.findViewById(R.id.loading_indicator).setVisibility(View.GONE);
                     //mSwipeRefreshLayout.setEnabled(true);

                     // refresh the local cache for Arke
                     refreshIrisCache();
                 }
             }

             @Override
             public void onFailure(Call<List<IrisEntryItem>> call, Throwable t) {

                 // TODO: need to load the cache instead
                 Log.e(LOG_TAG, t.getMessage());
                 //mConnectionError.setVisibility(View.VISIBLE);
                 rootView.findViewById(R.id.loading_indicator).setVisibility(View.GONE);
                 //mSwipeRefreshLayout.setEnabled(true);

                 // an observer sees when the data is changed while the activity is open,
                 // and updates the data in the adapter
                 mIrisViewModel.getAllEntries().observe(getActivity(), new Observer<List<IrisEntryItem>>() {
                     @Override
                     public void onChanged(List<IrisEntryItem> entries) {
                         // update cached copy of words in adapter
                         // hacky way to only refresh adapter initially,
                         // TODO: amend architecture so all requests are from
                         if (cacheOnce == true) {
                             mAdapter.setEntries(entries);
                             cacheOnce = false;
                         }
                     }

                 });
             }
         });

        // delete an item on long click
        mAdapter.setOnItemClickListener((v, position) -> {
            IrisEntryItem entryItem = mAdapter.getEntryAtPosition(position);

            // call method to decide what to do with entry
            deleteOrPublicAlert(entryItem);
        });

        // Inflate the layout for this fragment
        return rootView;

    }

    public int changeColour() {
        Random random = new Random();
        // pick a random colour (using an index)
        String colourName = mColourArray[random.nextInt(20)];
        // get resource identifier
        int colourResourceName = getResources().getIdentifier(colourName, "color",
                getActivity().getApplicationContext().getPackageName()); // look up the string colorName in the
        // "color" resources
        // there are separate ints for both names and the values
        int colourRes = ContextCompat.getColor(getActivity(), colourResourceName);
        return colourRes;
    }

    /**
     * This will open the colour picker dialogue, to allow us to add a new entry
     * with our chosen colour.
     */
    public void openColourPickerDialogue(ImageView image) {

        ColorPickerDialogBuilder
                .with(getActivity(), R.style.ColorPickerDialogTheme)
                .setTitle("How do you feel today?")
                .initialColor(mColourName)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .showAlphaSlider(false)
                .setOnColorSelectedListener(selectedColor -> {
                    // toast their decision, currently hexadecimal so it's just a bit silly
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            "Feeling a bit... " + Integer.toHexString(selectedColor) + "?",
                            Toast.LENGTH_SHORT).show();
                })
                // what to do if they choose the colour
                .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                    // amend the colour to the chosen one
                    mColourName = selectedColor;
                    image.setColorFilter(mColourName);
                })
                // otherwise exit
                .setNegativeButton("cancel", (dialog, which) -> {
                })
                .build()
                .show();

    }

    /**
     * Asks if you want to make a new entry public (to go to the Arke fragment that
     * has everyone's entries as well as the private fragment), or keep it private in the Iris
     * fragment, or cancel entry.
     */
    public void makePublicAlert() {
        String[] choices = {
                getString(R.string.iris_dialogue_yes),
                getString(R.string.iris_dialogue_no),
                getString(R.string.iris_dialogue_cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Make your colour public?");
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getString(R.string.iris_dialogue_yes).equals(choices[which])) {
                    addRemoteEntry(1);
                } else if (getString(R.string.iris_dialogue_no).equals(choices[which])) {
                    addRemoteEntry(0);
                } else {
                    // Do nothing
                }
            }
        });
        builder.show();
    }

    public void deleteOrPublicAlert(IrisEntryItem entryItem) {
        // initial choices
        String [] choices = {
                "Make this entry public?",
                "Delete this entry?",
                "Cancel, do nothing."};
        // change the text for the first choice depending on whether the
        // item is currently public or private
        if (entryItem.getIsPublic() == 1) {
            choices[0] = "Make this entry private?";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("What would you like to do?");
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("Make this entry public?".equals(choices[which])) {
                    // if entry private, make public
                    deleteOrPublicRemoteEntry(entryItem, "public", 1);
                } else if ("Make this entry private?".equals(choices[which])) {
                    // if entry public, make private
                    deleteOrPublicRemoteEntry(entryItem, "public", 0);
                } else if ("Delete this entry?".equals(choices[which])) {
                    // delete entry
                    // TODO: create an extra alert for deletion
                    deleteOrPublicRemoteEntry(entryItem, "deleted", 1);
                } else {
                    // Do nothing
                }
            }
        });
        builder.show();
    }

    /**
     * Delete an entry, or toggle it between public and private depending on arguments passed.
     * @param entryItem
     * @param deleteOrPublic string to decide which field is being updated
     * @param toggle this is 0 for making an entry private, or 1 for deleting or making entry public
     */
    public void deleteOrPublicRemoteEntry(IrisEntryItem entryItem, String deleteOrPublic, int toggle) {
        // this will pass change the flag of an item to deleted, so it will stop
        // being requested from REST
        HashMap<String, String> updateEntry = new HashMap<String, String>();
        updateEntry.put(deleteOrPublic, String.valueOf(toggle));
        Call<IrisEntryItem> call = entryService.updatePublic(entryItem.getRemoteId(), updateEntry);
        call.enqueue(new Callback<IrisEntryItem>() {
            @Override
            public void onResponse(Call<IrisEntryItem> call, Response<IrisEntryItem> response) {
                if (response.isSuccessful()) {
                    if (deleteOrPublic == "deleted") {
                        Toast.makeText(getActivity(),
                                "Entry deleted!",
                                Toast.LENGTH_SHORT).show();
                    } else if (toggle == 0) {
                        Toast.makeText(getActivity(),
                                "Entry has been made private!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(),
                                "Entry has been made public!",
                                Toast.LENGTH_SHORT).show();
                    }
                    refreshEntriesList();

                    // refresh after everything has been done
                    refreshIrisCache();
                }
            }

            @Override
            public void onFailure(Call<IrisEntryItem> call, Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                displayConnectionErrorMessage();
            }

        });
    }


    /**
     * This will update the local database using the remote database
     */
    public void refreshIrisCache() {
        // truncate table
        mIrisViewModel.deleteAll();

        Call<List<IrisEntryItem>> call = entryService.getPrivateEntries(mAccountName);
        call.enqueue(new Callback<List<IrisEntryItem>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<IrisEntryItem>> call, Response<List<IrisEntryItem>> response) {
                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Entries called.");
                    entriesList = response.body();
                    //Collections.reverse(entriesList);
                    for (IrisEntryItem entry: entriesList) {
                        Log.e(LOG_TAG, String.valueOf(entry.getId()));
                        IrisEntryItem entryItem = new IrisEntryItem(
                                entry.getRemoteId(),
                                entry.getDateTime(),
                                entry.getColour(),
                                entry.getIsPublic()
                        );
                        mIrisViewModel.insert(entryItem);
                    }

                    // smooth scroll to position
                    mRecyclerView.smoothScrollToPosition(0);

                }
            }

            /**
             * Show a connection error toast.
             * @param call
             * @param t
             */
            @Override
            public void onFailure(Call<List<IrisEntryItem>> call, Throwable t) {
                Log.e(LOG_TAG, t.getMessage());
                displayConnectionErrorMessage();
            }
        });
    }

    /**
     * Add colour to the backend postgreSQL database
     */
    public void addRemoteEntry(int isPublic) {

        IrisEntryItem entry = new IrisEntryItem(mAccountName, mColourName, isPublic);
        Call<IrisEntryItem> call = entryService.addEntry(entry);
        call.enqueue(new Callback<IrisEntryItem>() {
            @Override
            public void onResponse(Call<IrisEntryItem> call, Response<IrisEntryItem> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(),
                            "Entry added!",
                            Toast.LENGTH_SHORT).show();
                    refreshEntriesList();
                    refreshIrisCache();
                }
            }

            @Override
            public void onFailure(Call<IrisEntryItem> call, Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                displayConnectionErrorMessage();
            }

        });
    }

    /**
     * This method refreshes the recycler view
     */
    public void refreshEntriesList() {

        Call<List<IrisEntryItem>> call = entryService.getPrivateEntries(mAccountName);
        call.enqueue(new Callback<List<IrisEntryItem>>() {
            @Override
            public void onResponse(Call<List<IrisEntryItem>> call, Response<List<IrisEntryItem>> response) {
                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Entries called.");
                    entriesList = response.body();
                    mAdapter.setEntries(entriesList);
                    // smooth scroll to position
                    mRecyclerView.smoothScrollToPosition(0);

                    // refresh the cache
                    refreshIrisCache();
                }
            }

            /**
             * If the entriesList contains items, it means items are showing,
             * but there is a new error on refreshing, so a Toast is shown.
             * Otherwise, the page was already blank from the start,
             * so a connection error message is shown.
             * @param call
             * @param t
             */
            @Override
            public void onFailure(Call<List<IrisEntryItem>> call, Throwable t) {
                Log.e(LOG_TAG, t.getMessage());
                displayConnectionErrorMessage();
            }

        });

    }

    public void displayConnectionErrorMessage() {
        Toast.makeText(getActivity(),
                "Connection error...",
                Toast.LENGTH_SHORT).show();
    }

}