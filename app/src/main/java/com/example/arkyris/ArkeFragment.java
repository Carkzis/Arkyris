package com.example.arkyris;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArkeFragment extends Fragment {

    private static final String LOG_TAG = ArkeFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ArkeListAdapter mAdapter;
    private int mColourName;
    EntryService entryService;
    List<ArkeEntryItem> entriesList = new ArrayList<ArkeEntryItem>();
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // textview for when there is a connection error
    private TextView mConnectionError;

    // all activity interactions are with the WordViewModel only
    private IrisViewModel mIrisViewModel;

    // Placeholder to test changing colours of entries
    private static final String[] mColourArray = {"red", "pink", "purple", "deep_purple",
            "indigo", "blue", "light_blue", "cyan", "teal", "green",
            "light_green", "lime", "yellow", "amber", "orange", "deep_orange",
            "brown", "grey", "blue_grey", "pink_dark"};

    public ArkeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ArkeFragment.
     */
    public static ArkeFragment newInstance() {
        ArkeFragment fragment = new ArkeFragment();
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

    /**
     * Note to self: for fragments, treat this as onCreate.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_arke, container, false);

        entryService = APIUtils.getEntryService();

        final FloatingActionButton fab = rootView.findViewById(R.id.arke_fab);
        fab.setOnClickListener(view -> {
            // get a random colour, to initially display to the user in the color picker dialogue.
            mColourName = changeColour();
            // This will open a dialogue to enter a colour.
            openColourPickerDialogue();

        });

        // connection error text view
        mConnectionError = rootView.findViewById(R.id.connection_error);
        // swipe refresh widget
        mSwipeRefreshLayout = rootView.findViewById(R.id.arke_swipe);
        // disable swipe refresh until page loaded
        mSwipeRefreshLayout.setEnabled(false);
        // Get a handler for the RecyclerView
        mRecyclerView = rootView.findViewById(R.id.arke_recyclerview);
        // Create an adapter and supply the data
        mAdapter = new ArkeListAdapter(getActivity());
        // Connect adapter to RecyclerView
        mRecyclerView.setAdapter(mAdapter);
        // Give RecyclerView a LayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // This will load the items from the database
        Call<List<ArkeEntryItem>> call = entryService.getPublicEntries();
        call.enqueue(new Callback<List<ArkeEntryItem>>() {
            @Override
            public void onResponse(Call<List<ArkeEntryItem>> call, Response<List<ArkeEntryItem>> response) {
                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Entries called.");
                    entriesList = response.body();
                    mAdapter.setEntries(entriesList);
                    fab.setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.loading_indicator).setVisibility(View.GONE);
                    mSwipeRefreshLayout.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<List<ArkeEntryItem>> call, Throwable t) {
                Log.e(LOG_TAG, t.getMessage());
                mConnectionError.setVisibility(View.VISIBLE);
                fab.setVisibility(View.INVISIBLE);
                rootView.findViewById(R.id.loading_indicator).setVisibility(View.GONE);
                mSwipeRefreshLayout.setEnabled(true);
            }
        });

        // associated the ViewModel with the controller, this persists through config changes
        mIrisViewModel = ViewModelProviders.of(this).get(IrisViewModel.class);

        /**
         * Refresh the fragment on swiping down
         */
        // removed the icon spinner as using a progress icon
        mSwipeRefreshLayout.setProgressViewEndTarget(false, 0);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshEntriesList();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    /**
     * Method for generating a random color
     * @return
     */
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
    public void openColourPickerDialogue() {

        ColorPickerDialogBuilder
                .with(getActivity(), R.style.ColorPickerDialogTheme)
                .setTitle("Tell the world how you feel!")
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
                    addRemoteEntry();
                })
                // otherwise exit
                .setNegativeButton("cancel", (dialog, which) -> {
                })
                .build()
                .show();

    }

    /**
     * This will update the local database using the remote database
     */
    public void refreshLocalDatabase() {
        // truncate table
        mIrisViewModel.deleteAll();

        // TODO: this should only update the local database with the user's entries
        Call<List<ArkeEntryItem>> call = entryService.getEntries();
        call.enqueue(new Callback<List<ArkeEntryItem>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<ArkeEntryItem>> call, Response<List<ArkeEntryItem>> response) {
                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Entries called.");
                    entriesList = response.body();
                    //Collections.reverse(entriesList);
                    for (ArkeEntryItem entry : entriesList) {
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
                public void onFailure(Call<List<ArkeEntryItem>> call, Throwable t) {
                    Log.e(LOG_TAG, t.getMessage());
                    Toast.makeText(getActivity(),
                            "Connection error...",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

    /**
     * Add colour to the backend postgreSQL database
     */
    public void addRemoteEntry() {
        // TODO: This will assign a member to the entry
        ArkeEntryItem entry = new ArkeEntryItem("Carkzis", mColourName, 1);
        Call<ArkeEntryItem> call = entryService.addEntry(entry);
        call.enqueue(new Callback<ArkeEntryItem>() {
            @Override
            public void onResponse(Call<ArkeEntryItem> call, Response<ArkeEntryItem> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(),
                            "Entry added!",
                            Toast.LENGTH_SHORT).show();
                    refreshEntriesList();
                    refreshLocalDatabase();
                }
            }

            @Override
            public void onFailure(Call<ArkeEntryItem> call, Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                Toast.makeText(getActivity(),
                        "Connection error...",
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    /**
     * This method refreshes the recycler view
     */
    public void refreshEntriesList() {

        // show the loading indicator
        getActivity().findViewById(R.id.loading_indicator).setVisibility(View.VISIBLE);
        // set this to gone, whether or not it is already set as such
        mConnectionError.setVisibility(View.GONE);
        // disable refresh layout until loading completed
        mSwipeRefreshLayout.setEnabled(false);

        Call<List<ArkeEntryItem>> call = entryService.getPublicEntries();
        call.enqueue(new Callback<List<ArkeEntryItem>>() {
            @Override
            public void onResponse(Call<List<ArkeEntryItem>> call, Response<List<ArkeEntryItem>> response) {
                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Entries called.");
                    mConnectionError.setVisibility(View.GONE);
                    getActivity().findViewById(R.id.arke_fab).setVisibility(View.VISIBLE);
                    entriesList = response.body();
                    mAdapter.setEntries(entriesList);
                    getActivity().findViewById(R.id.loading_indicator).setVisibility(View.GONE);
                    // smooth scroll to position
                    mRecyclerView.smoothScrollToPosition(0);
                    mSwipeRefreshLayout.setEnabled(true);
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
            public void onFailure(Call<List<ArkeEntryItem>> call, Throwable t) {
                Log.e(LOG_TAG, t.getMessage());
                getActivity().findViewById(R.id.loading_indicator).setVisibility(View.GONE);
                mSwipeRefreshLayout.setEnabled(true);
                if (entriesList.size() > 0) {
                    Toast.makeText(getActivity(),
                            "Connection error...",
                            Toast.LENGTH_SHORT).show();
                } else {
                    mConnectionError.setVisibility(View.VISIBLE);
                }
            }
        });

    }

}