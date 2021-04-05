package com.example.arkyris;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

import eltos.simpledialogfragment.color.SimpleColorDialog;


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
            // TODO: This needs to do add to th
            openColourPickerDialogue();
            // get a random colour
            // TODO: obtain a colour of the member's choosing
            // TODO: enter into a database
            mColourName = changeColour();
            // create timestamps
            // TODO: make these obey local formatting
            String timeStampDate = new SimpleDateFormat("dd/mm/yyyy").format(new Date());
            String timeStampTime = new SimpleDateFormat("HH:mm").format(new Date());
            // add a new word to the List
            mArkeColourList.addFirst(new ArkeItem(R.drawable.colour_rectangle, mColourName, timeStampDate, timeStampTime));
            ;
            // notify the adapter that data has changed
            mRecyclerView.getAdapter().notifyItemInserted(0);
            mRecyclerView.smoothScrollToPosition(0);

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

    public void openColourPickerDialogue() {
        // TODO: This needs to do something.
        SimpleColorDialog.build()
                .title("Choose your colour!")
                .colorPreset(mColourName)
                .allowCustom(true)
                .show(getActivity(), "Hello");
    }


}