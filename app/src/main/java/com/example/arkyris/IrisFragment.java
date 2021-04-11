package com.example.arkyris;

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

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IrisFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IrisFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private IrisListAdapter mAdapter;
    private int mColourName;

    // all activity interactions are with the WordViewModel only
    private IrisViewModel mIrisViewModel;

    // Placeholder to test changing colours of entries
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_iris, container, false);

        /**
         * This will add a colour entry into the diary.
         * It currently just shows a toast as a placeholder.
         *
         * @param view is the ImageView that enters a particular colour to the diary.
         */

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

        // associated the ViewModel with the controller, this persists through config changes
        mIrisViewModel = ViewModelProviders.of(this).get(IrisViewModel.class);

        // an observer sees when the data is changed while the activity is open,
        // and updates the data in the adapter
        mIrisViewModel.getAllEntries().observe(getActivity(), new Observer<List<EntryItem>>() {
            @Override
            public void onChanged(List<EntryItem> entries) {
                // update cached copy of words in adapter
                mAdapter.setEntries(entries);
            }
        });

        // delete an item on long click
        mAdapter.setOnItemClickListener((v, position) -> {
            EntryItem entryItem = mAdapter.getEntryAtPosition(position);
            Toast.makeText(getActivity(), "Item Deleted!", Toast.LENGTH_SHORT).show();
            // delete
            //mIrisViewModel.deleteEntry(entryItem);

            deleteOrPublicAlert(entryItem);
        });

        // Inflate the layout for this fragment
        return rootView;

    }

    public int changeColour() {
        Random random = new Random();
        // pick a random colour (using an index)
        String colourName = mColourArray[random.nextInt(20)];
        Log.e("OOPS", String.valueOf(colourName));
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
                    Toast.makeText(getActivity(),
                            "To Arke it goes!",
                            Toast.LENGTH_SHORT).show();
                    postColour(1);
                } else if (getString(R.string.iris_dialogue_no).equals(choices[which])) {
                    postColour(0);
                } else {
                    // Do nothing
                }
            }
        });
        builder.show();
    }

    public void deleteOrPublicAlert(EntryItem entryItem) {
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
                    // if entry public, make private
                    entryItem.setIsPublic(1);
                    mIrisViewModel.updatePublic(entryItem);
                } else if ("Make this entry private?".equals(choices[which])) {
                    // if entry private, make public
                    entryItem.setIsPublic(0);
                    mIrisViewModel.updatePublic(entryItem);
                } else if ("Delete this entry?".equals(choices[which])) {
                    // delete entry
                    // TODO: create an extra alert for deletion
                    // TODO: change deletion to use deletion tag instead
                    mIrisViewModel.deleteEntry(entryItem);
                } else {
                    // Do nothing
                }
            }
        });
        builder.show();
    }

    public void postColour(int isPublic) {
        // create timestamps
        // TODO: make these obey local formatting
        String timeStampDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String timeStampTime = new SimpleDateFormat("HH:mm").format(new Date());
        // add a new item to the List
        EntryItem entryItem = new EntryItem(
                mColourName,
                timeStampDate,
                timeStampTime,
                isPublic); // 0 for private post, 1 for public post (appears on both Arke and Iris)
        // notify the adapter that data has changed
        mIrisViewModel.insert(entryItem);
        // smooth scroll to position
        mRecyclerView.smoothScrollToPosition(0);
    }

}