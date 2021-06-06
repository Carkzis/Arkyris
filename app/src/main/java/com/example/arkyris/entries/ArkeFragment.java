package com.example.arkyris.entries;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.arkyris.R;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

public class ArkeFragment extends Fragment {

    private View mRootView;
    private FloatingActionButton mFab;
    private TextView mConnectionError;
    private ProgressBar mLoadingIndicator;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ArkeListAdapter mAdapter;

    // all activity interactions are with the WordViewModel only
    private IrisViewModel mIrisViewModel;
    private ArkeViewModel mArkeViewModel;

    private int mColourName;
    private int mEntryListSize;
    private boolean mInitialLoad = true;

    public ArkeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_arke, container, false);

        // associated the ViewModel with the controller
        // Note: IrisViewModel is shared between activities so Iris
        // can display whether or not there are any user entries on launching the app.
        mIrisViewModel = ViewModelProviders.of(requireActivity()).get(IrisViewModel.class);
        mArkeViewModel = ViewModelProviders.of(this).get(ArkeViewModel.class);

        mFab = mRootView.findViewById(R.id.arke_fab);
        // connection error text view
        mConnectionError = mRootView.findViewById(R.id.connection_error);
        // loading indicator
        mLoadingIndicator = mRootView.findViewById(R.id.loading_indicator);
        // swipe refresh widget
        mSwipeRefreshLayout = mRootView.findViewById(R.id.arke_swipe);
        // Get a handler for the RecyclerView
        mRecyclerView = mRootView.findViewById(R.id.arke_recyclerview);

        mArkeViewModel.refreshArkeCache();

        // Inflate the layout for this fragment
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view,
                              @Nullable @org.jetbrains.annotations
                                      .Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpFab();
        setUpRecyclerView();
        setUpLoadingOutcomeObserver();
        setUpEntryAddedObserver();
        setUpSwipeRefresh();

    }

    public void setUpFab() {
        mFab.setOnClickListener(view -> {
            // get a random colour, to initially display to the user in the color picker dialogue.
            mColourName = changeColour();
            // This will open a dialogue to enter a colour.
            openColourPickerDialogue();
        });
    }

    private void setUpRecyclerView() {

        // Create an adapter and supply the data
        mAdapter = new ArkeListAdapter(getActivity());
        // Connect adapter to RecyclerView
        mRecyclerView.setAdapter(mAdapter);
        // Give RecyclerView a LayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Observer for the entries to list in the recyclerview
        mArkeViewModel.getPublicEntries().observe(requireActivity(), entries -> {
            // update cached copy of words in adapter
            mAdapter.setEntries(entries);
            mEntryListSize = entries.size();
        });

    }

    private void setUpLoadingOutcomeObserver() {

        // Observer for whether loading has completed
        mArkeViewModel.getLoadingOutcome().observe(requireActivity(), loadingOutcome -> {

            mLoadingIndicator.setVisibility(View.GONE);
            mSwipeRefreshLayout.setEnabled(true);

            // This will display a message to say there are no entries to show only
            // after everything has loaded. The entry list variable is kept updated by the
            // public entries observer
            if (mEntryListSize < 1) {
                mConnectionError.setVisibility(View.VISIBLE);
            } else {
                mConnectionError.setVisibility(View.GONE);
            }

            mFab.setVisibility(View.VISIBLE);

            if (loadingOutcome.equals("complete")) {
                // If this is not the initial load, also update the Iris cache.
                if (!mInitialLoad) {
                    mIrisViewModel.refreshIrisCache(true);
                } else {
                    mInitialLoad = false;
                }
            } else { // loadingOutcome will equal "error"
                View tablayoutView = requireActivity().findViewById(R.id.tab_layout);
                Snackbar snackbar = Snackbar.make(mRootView, "Connection error...",
                        Snackbar.LENGTH_LONG);
                snackbar.setAnchorView(tablayoutView);
                snackbar.show();

                // Reset the connectionError LiveData to false
                mArkeViewModel.connectionErrorNotified();
            }
        });
    }

    private void setUpEntryAddedObserver() {
        mArkeViewModel.getEntryAdded().observe(requireActivity(), entryAdded -> {
            if (entryAdded) {
                Toast.makeText(requireActivity(),
                        getString(R.string.entry_added),
                        Toast.LENGTH_SHORT).show();
                mRecyclerView.smoothScrollToPosition(0);

                // Reset the entryAdded LiveData to false
                mArkeViewModel.entryAddedComplete();
            }
        });
    }

    private void setUpSwipeRefresh() {
        // disable swipe refresh until page loaded
        mSwipeRefreshLayout.setEnabled(false);

        // Refresh the fragment on swiping down.
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mArkeViewModel.refreshArkeCache();
            mSwipeRefreshLayout.setRefreshing(false);
        });
    }

    /**
     * Method for applying random colour for the colour picker
     *
     * @return the colour resources, colourRes
     */
    public int changeColour() {
        int colourResourceName = getResources()
                .getIdentifier(mArkeViewModel.randomColour(), "color",
                        requireActivity().getApplicationContext().getPackageName());
        // look up the string colorName in the
        // "color" resources
        // there are separate ints for both names and the values
        return ContextCompat.getColor(requireActivity(), colourResourceName);
    }

    /**
     * This will open the colour picker dialogue, to allow us to add a new entry
     * with our chosen colour.
     */
    public void openColourPickerDialogue() {

        ColorPickerDialogBuilder
                .with(requireActivity(), R.style.ColorPickerDialogTheme)
                .setTitle("Tell the world how you feel!")
                .initialColor(mColourName)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .showAlphaSlider(false)
                // what to do if they choose the colour
                .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                    // amend the colour to the chosen one
                    mColourName = selectedColor;
                    addRemoteEntry(mColourName);
                })
                // otherwise exit
                .setNegativeButton("cancel", (dialog, which) -> {
                })
                .build()
                .show();
    }

    /**
     * Add colour to the backend postgreSQL database
     */
    public void addRemoteEntry(int colour) {
        mArkeViewModel.addRemoteEntry(colour);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

}