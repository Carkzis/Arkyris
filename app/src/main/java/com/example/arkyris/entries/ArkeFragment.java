package com.example.arkyris.entries;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.arkyris.APIUtils;
import com.example.arkyris.R;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ArkeFragment extends Fragment {

    private static final String LOG_TAG = ArkeFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ArkeListAdapter mAdapter;
    private int mColourName;
    EntryService entryService;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mAccountName;
    private int entryListSize;

    // textview for when there is a connection error
    private TextView mConnectionError;

    // all activity interactions are with the WordViewModel only
    private IrisViewModel mIrisViewModel;
    private ArkeViewModel mArkeViewModel;

    public ArkeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Currently no arguments here.
        }
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

        // associated the ViewModel with the controller, this persists through config changes
        mIrisViewModel = ViewModelProviders.of(this).get(IrisViewModel.class);
        mArkeViewModel = ViewModelProviders.of(this).get(ArkeViewModel.class);

        mArkeViewModel.getAccountName().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String username) {
                // update cached copy of words in adapter
                mAccountName = username;
            }
        });

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

        mArkeViewModel.refreshArkeCache();

        // Observer for the entries to list in the recyclerview
        mArkeViewModel.getPublicEntries().observe(getActivity(), entries -> {
            // update cached copy of words in adapter
            mAdapter.setEntries(entries);
            entryListSize = entries.size();

        });

        // Observer for any connection error
        mArkeViewModel.getConnectionError().observe(getActivity(), connectionError -> {
            if (connectionError) {
                Toast.makeText(getActivity(),
                        "Connection error...",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Observer for whether loading has completed
        mArkeViewModel.getLoadingComplete().observe(getActivity(), loadingComplete -> {
            if (loadingComplete) {
                rootView.findViewById(R.id.loading_indicator).setVisibility(View.GONE);
                mSwipeRefreshLayout.setEnabled(true);
                fab.setVisibility(View.VISIBLE);
                // This will display a message to say there are entries to show on only
                // after everything has loaded.
                if (entryListSize < 1) {
                    rootView.findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
                } else {
                    rootView.findViewById(R.id.connection_error).setVisibility(View.GONE);
                    mRecyclerView.smoothScrollToPosition(0);
                }
                // refresh Iris Cache while we are at it
                mIrisViewModel.refreshIrisCache();
            }
        });

        // Observer for any connection error
        mArkeViewModel.getEntryAdded().observe(getActivity(), entryAdded -> {
            if (entryAdded) {
                Toast.makeText(getActivity(),
                        "Entry added!",
                        Toast.LENGTH_SHORT).show();
            }
        });


        /**
         * Refresh the fragment on swiping down
         */
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rootView.findViewById(R.id.loading_indicator).setVisibility(View.VISIBLE);
                mArkeViewModel.refreshArkeCache();
                mIrisViewModel.refreshIrisCache();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    /**
     * Method for applying random colour for the colour picker
     * @return
     */
    public int changeColour() {
        int colourResourceName = getResources().getIdentifier(mArkeViewModel.randomColour(), "color",
                getActivity().getApplicationContext().getPackageName());
        // look up the string colorName in the
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
        getActivity().findViewById(R.id.loading_indicator).setVisibility(View.VISIBLE);
    }

}