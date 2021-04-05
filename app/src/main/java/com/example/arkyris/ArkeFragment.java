package com.example.arkyris;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArkeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArkeFragment extends Fragment {

    private final LinkedList<ArkeItem> mArkeColourList = new LinkedList<>();
    private RecyclerView mRecyclerView;
    private ArkeListAdapter mAdapter;
    private int mColourName;

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

        final FloatingActionButton fab = rootView.findViewById(R.id.arke_fab);
        fab.setOnClickListener(view -> {
            // TODO: enter into a database
            // get a random colour, to initially display to the user in the color picker dialogue.
            mColourName = changeColour();
            // This will open a dialogue to enter a colour.
            openColourPickerDialogue();

        });

        // Create a placeholder list of words for RecycleView.
        // TODO: obtain items from database
        // TODO: show a different item for the end, "e.g. there are no more entries"
        for (int i = 0; i < 50; i++) {
            int colourName = changeColour();
            mArkeColourList.addLast(new ArkeItem(R.drawable.colour_rectangle, colourName, "31/03/2021", "21:21"));
        }

        // Get a handler for the RecyclerView
        mRecyclerView = rootView.findViewById(R.id.arke_recyclerview);
        // Create an adapter and supply the data
        mAdapter = new ArkeListAdapter(getActivity(), mArkeColourList);
        // Connect adapter to RecyclerView
        mRecyclerView.setAdapter(mAdapter);
        // Give RecyclerView a LayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
    public void openColourPickerDialogue() {

        ColorPickerDialogBuilder
                .with(getActivity(), R.style.ColorPickerDialogTheme)
                .setTitle("Tell the world how you feel!")
                .initialColor(mColourName)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
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
                    // create timestamps
                    // TODO: make these obey local formatting
                    String timeStampDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                    String timeStampTime = new SimpleDateFormat("HH:mm").format(new Date());
                    // add a new word to the List
                    mArkeColourList.addFirst(new ArkeItem(
                            R.drawable.colour_rectangle,
                            mColourName,
                            timeStampDate,
                            timeStampTime));
                    ;
                    // notify the adapter that data has changed
                    mRecyclerView.getAdapter().notifyItemInserted(0);
                    mRecyclerView.smoothScrollToPosition(0);
                })
                // otherwise exit
                .setNegativeButton("cancel", (dialog, which) -> {
                })
                .build()
                .show();

    }


}