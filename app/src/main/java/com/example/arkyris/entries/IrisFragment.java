package com.example.arkyris.entries;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arkyris.APIUtils;
import com.example.arkyris.R;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class IrisFragment extends Fragment {

    private static final String LOG_TAG = IrisFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private IrisListAdapter mAdapter;
    private int mColourName;
    EntryService entryService;
    List<IrisEntryItem> entriesList = new ArrayList<IrisEntryItem>();
    private String mAccountName;
    private int entryListSize = 0;
    // TODO: Do something about this to prevent multiple loads!
    private boolean initialLoad = true;

    // all activity interactions are with the WordViewModel only
    private IrisViewModel mIrisViewModel;
    private ArkeViewModel mArkeViewModel;

    // Random colours
    private static final String[] mColourArray = {"red", "pink", "purple", "deep_purple",
            "indigo", "blue", "light_blue", "cyan", "teal", "green",
            "light_green", "lime", "yellow", "amber", "orange", "deep_orange",
            "brown", "grey", "blue_grey", "pink_dark"};

    public IrisFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Currently no arguments here.
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_iris, container, false);
        entryService = APIUtils.getEntryService();

        // associated the ViewModel with the controller, this persists through config changes
        mIrisViewModel = ViewModelProviders.of(getActivity()).get(IrisViewModel.class);
        mArkeViewModel = ViewModelProviders.of(this).get(ArkeViewModel.class);

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

        mIrisViewModel.refreshIrisCache(false);

        // Observer for the entries to list in the recyclerview
        mIrisViewModel.getAllEntries().observe(getActivity(), entries -> {
            // update cached copy of words in adapter
            mAdapter.setEntries(entries);
            // We want the size, so that we can decide whether to provide a message
            // saying there are no entries after loading has finished
            entryListSize = entries.size();
        });

        // Observer for any connection error
        mIrisViewModel.getConnectionError().observe(getActivity(), connectionError -> {
            if (connectionError) {
                View tablayoutView = getActivity().findViewById(R.id.tab_layout);
                Snackbar snackbar = Snackbar.make(rootView, "Connection error...",
                        Snackbar.LENGTH_LONG);
                snackbar.setAnchorView(tablayoutView);
                snackbar.show();
                mIrisViewModel.connectionErrorNotified();
            }
        });

        // Observer for whether loading has completed
        // TODO: Combine this with Connection error, like with ArkeFragment
        mIrisViewModel.getLoadingComplete().observe(getActivity(), loadingComplete -> {
            Log.e(LOG_TAG, String.valueOf(entryListSize));
            if (loadingComplete) {
                rootView.findViewById(R.id.loading_indicator_iris).setVisibility(View.GONE);
                mArkeViewModel.refreshArkeCache();
                // This will display a message to say there are entries to show on only
                // after everything has loaded.
                if (entryListSize == 0) {
                    rootView.findViewById(R.id.text_no_entries).setVisibility(View.VISIBLE);
                } else {
                    rootView.findViewById(R.id.text_no_entries).setVisibility(View.GONE);
                }
            }
        });

        mIrisViewModel.getEntryAdded().observe(getActivity(), entryAdded -> {
            if (entryAdded) {
                Toast.makeText(getActivity(),
                        "Entry added!",
                        Toast.LENGTH_SHORT).show();
                // Reset the entryAdded LiveData to false
                mIrisViewModel.entryAddedComplete();
            }
        });

        mIrisViewModel.getEntryDeleted().observe(getActivity(), entryDeleted -> {
            if (entryDeleted) {
                Toast.makeText(getActivity(),
                        "Entry deleted!",
                        Toast.LENGTH_SHORT).show();
                mIrisViewModel.entryDeletedComplete();
            }
        });

        mIrisViewModel.getIsPublic().observe(getActivity(), isPublic -> {
            if (isPublic.equals("public")) {
                Toast.makeText(getActivity(),
                        "Entry is now public!",
                        Toast.LENGTH_SHORT).show();
            } else if (isPublic.equals("private")) {
                Toast.makeText(getActivity(),
                        "Entry is now private!",
                        Toast.LENGTH_SHORT).show();
            }
            mIrisViewModel.changedEntryPublicity();
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
                    updateRemoteEntryPublicity(entryItem,1);
                } else if ("Make this entry private?".equals(choices[which])) {
                    // if entry public, make private
                    updateRemoteEntryPublicity(entryItem, 0);
                } else if ("Delete this entry?".equals(choices[which])) {
                    // delete entry
                    deleteRemoteEntry(entryItem);
                } else {
                    // Do nothing
                }
            }
        });
        builder.show();
    }

    public void deleteRemoteEntry(IrisEntryItem entryItem) {
        mIrisViewModel.deleteRemoteEntry(entryItem);
        getActivity().findViewById(R.id.loading_indicator_iris).setVisibility(View.VISIBLE);
    }

    public void updateRemoteEntryPublicity(IrisEntryItem entryItem, int isPublic) {
        mIrisViewModel.updateRemoteEntryPublicity(entryItem, isPublic);
        getActivity().findViewById(R.id.loading_indicator_iris).setVisibility(View.VISIBLE);
    }

    /**
     * Add colour to the backend postgreSQL database
     */
    public void addRemoteEntry(int isPublic) {
        mIrisViewModel.addRemoteEntry(mColourName, isPublic);
        getActivity().findViewById(R.id.loading_indicator_iris).setVisibility(View.VISIBLE);
    }

    /**
     * Method for applying random colour for the colour picker
     * @return
     */
    public int changeColour() {
        int colourResourceName = getResources().getIdentifier(mIrisViewModel.randomColour(), "color",
                getActivity().getApplicationContext().getPackageName());
        // look up the string colorName in the
        // "color" resources
        // there are separate ints for both names and the values
        int colourRes = ContextCompat.getColor(getActivity(), colourResourceName);
        return colourRes;
    }

}