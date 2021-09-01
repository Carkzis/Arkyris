package com.carkzis.arkyris.entries;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import com.example.arkyris.R;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

public class IrisFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private IrisListAdapter mAdapter;
    private FloatingActionButton mFab;
    private TextView mConnectionError;
    private ImageView mColourCircle;


    private int mColourName;
    private int mEntryListSize = 0;
    private ProgressBar mLoadingIndicator;
    private boolean mInitialLoad = true;

    private View mRootView;

    // all activity interactions are with the WordViewModel only
    private IrisViewModel mIrisViewModel;
    private ArkeViewModel mArkeViewModel;

    public IrisFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_iris, container, false);

        // associated the ViewModel with the controller, this persists through config changes
        mIrisViewModel = ViewModelProviders.of(requireActivity()).get(IrisViewModel.class);
        mArkeViewModel = ViewModelProviders.of(this).get(ArkeViewModel.class);

        mFab = mRootView.findViewById(R.id.iris_fab);
        mColourCircle = mRootView.findViewById(R.id.chosen_colour);
        // Get a handler for the RecyclerView
        mRecyclerView = mRootView.findViewById(R.id.iris_recyclerview);
        // loading indicator
        mLoadingIndicator = mRootView.findViewById(R.id.loading_indicator_iris);
        mConnectionError = mRootView.findViewById(R.id.text_no_entries);

        mIrisViewModel.refreshIrisCache(false);

        // Inflate the layout for this fragment
        return mRootView;

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view,
                              @Nullable @org.jetbrains.annotations
                                      .Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpFab();
        setUpColourCircleButton();
        setUpRecyclerView();
        setUpConnectionErrorObserver();
        setUpLoadingCompleteObserver();
        setUpEntryAddedObserver();
        setUpEntryClickListener();
        setUpEntryDeletedToast();
        setUpPublicityToast();

    }

    private void setUpFab() {
        mFab.setOnClickListener(view -> {
            // this will add the colour in the circle to the user's diary
            makePublicAlert();
        });
    }

    private void setUpColourCircleButton() {
        mColourName = changeColour();
        mColourCircle.setColorFilter(mColourName);

        mColourCircle.setOnClickListener(view -> {
            // get a random colour, to initially display to the user in the color picker dialogue.
            mColourName = changeColour();
            // This will open a dialogue to enter a colour.
            openColourPickerDialogue(mColourCircle);
        });
    }


    private void setUpEntryClickListener() {

        // delete an item on long click
        mAdapter.setOnItemClickListener((v, position) -> {
            IrisEntryItem entryItem = mAdapter.getEntryAtPosition(position);

            // call method to decide what to do with entry
            deleteOrPublicAlert(entryItem);
        });

    }

    private void setUpRecyclerView() {
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

        // Observer for the entries to list in the recyclerview
        mIrisViewModel.getAllEntries().observe(requireActivity(), entries -> {
            // update cached copy of words in adapter
            mAdapter.setEntries(entries);
            // We want the size, so that we can decide whether to provide a message
            // saying there are no entries after loading has finished
            mEntryListSize = entries.size();
        });
    }

    private void setUpConnectionErrorObserver() {
        // Observer for any connection error
        mIrisViewModel.getConnectionError().observe(requireActivity(), connectionError -> {
            // The connection error for IrisFragment should not occur when loading the
            // app, as this is already handled by ArkeFragment.
            if (connectionError) {
                if (!mInitialLoad) {
                    View tablayoutView = requireActivity().findViewById(R.id.tab_layout);
                    Snackbar snackbar = Snackbar.make(mRootView, R.string.connection_error,
                            Snackbar.LENGTH_LONG);
                    snackbar.setAnchorView(tablayoutView);
                    snackbar.show();
                }
                mIrisViewModel.connectionErrorNotified();
            }
            mInitialLoad = false;
        });
    }

    private void setUpLoadingCompleteObserver() {
        // Observer for whether loading has completed
        mIrisViewModel.getLoadingComplete().observe(requireActivity(), loadingComplete -> {
            if (loadingComplete) {
                mLoadingIndicator.setVisibility(View.GONE);
                mArkeViewModel.refreshArkeCache();
                // This will display a message to say there are no entries to show only
                // after everything has loaded (and there are no entries).
                if (mEntryListSize == 0) {
                    mConnectionError.setVisibility(View.VISIBLE);
                } else {
                    mConnectionError.setVisibility(View.GONE);
                }
            }
            // I have added this again here, to avoid concurrency issues with the
            // connectionError LiveData
            mInitialLoad = false;
        });
    }


    private void setUpEntryAddedObserver() {

        mIrisViewModel.getEntryAdded().observe(requireActivity(), entryAdded -> {
            if (entryAdded) {
                Toast.makeText(getActivity(),
                        getString(R.string.entry_added),
                        Toast.LENGTH_SHORT).show();
                mRecyclerView.smoothScrollToPosition(0);
                // Reset the entryAdded LiveData to false
                mIrisViewModel.entryAddedComplete();
            }
        });

    }

    public void setUpEntryDeletedToast() {
        mIrisViewModel.getEntryDeleted().observe(requireActivity(), entryDeleted -> {
            if (entryDeleted) {
                Toast.makeText(getActivity(),
                        getString(R.string.entry_deleted),
                        Toast.LENGTH_SHORT).show();
                mIrisViewModel.entryDeletedComplete();
            }
        });
    }

    public void setUpPublicityToast() {
        mIrisViewModel.getIsPublic().observe(requireActivity(), isPublic -> {
            if (isPublic.equals("public")) {
                Toast.makeText(getActivity(),
                        getString(R.string.entry_public),
                        Toast.LENGTH_SHORT).show();
            } else if (isPublic.equals("private")) {
                Toast.makeText(getActivity(),
                        getString(R.string.entry_private),
                        Toast.LENGTH_SHORT).show();
            }
            mIrisViewModel.changedEntryPublicity();
        });
    }

    /**
     * This will open the colour picker dialogue, to allow us to add a new entry
     * with our chosen colour.
     */
    public void openColourPickerDialogue(ImageView image) {

        ColorPickerDialogBuilder
                .with(requireActivity(), R.style.ColorPickerDialogTheme)
                .setTitle(getString(R.string.feeling_query))
                .initialColor(mColourName)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .showAlphaSlider(false)
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
        builder.setTitle(R.string.make_colour_public);
        builder.setItems(choices, (dialog, which) -> {
            if (getString(R.string.iris_dialogue_yes).equals(choices[which])) {
                addRemoteEntry(1);
            } else if (getString(R.string.iris_dialogue_no).equals(choices[which])) {
                addRemoteEntry(0);
            }
        });
        builder.show();
    }

    public void deleteOrPublicAlert(IrisEntryItem entryItem) {
        // initial choices
        String [] choices = {
                getString(R.string.make_entry_public),
                getString(R.string.delete_entry_query),
                getString(R.string.cancel_nothing)};
        // change the text for the first choice depending on whether the
        // item is currently public or private
        if (entryItem.getIsPublic() == 1) {
            choices[0] = getString(R.string.make_entry_private);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.like_to_do_query);
        builder.setItems(choices, (dialog, which) -> {
            if (getString(R.string.make_entry_public).equals(choices[which])) {
                // if entry private, make public
                updateRemoteEntryPublicity(entryItem,1);
            } else if (getString(R.string.make_entry_private).equals(choices[which])) {
                // if entry public, make private
                updateRemoteEntryPublicity(entryItem, 0);
            } else if (getString(R.string.delete_entry_query).equals(choices[which])) {
                // delete entry
                deleteRemoteEntry(entryItem);
            }
        });
        builder.show();
    }

    public void deleteRemoteEntry(IrisEntryItem entryItem) {
        mIrisViewModel.deleteRemoteEntry(entryItem);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    public void updateRemoteEntryPublicity(IrisEntryItem entryItem, int isPublic) {
        mIrisViewModel.updateRemoteEntryPublicity(entryItem, isPublic);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * Add colour to the backend postgreSQL database
     */
    public void addRemoteEntry(int isPublic) {
        mIrisViewModel.addRemoteEntry(mColourName, isPublic);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * Method for applying random colour for the colour picker
     */
    public int changeColour() {
        int colourResourceName = getResources().getIdentifier(mIrisViewModel.randomColour(), "color",
                requireActivity().getApplicationContext().getPackageName());
        // look up the string colorName in the
        // "color" resources
        // there are separate ints for both names and the values
        return ContextCompat.getColor(requireActivity(), colourResourceName);
    }

}