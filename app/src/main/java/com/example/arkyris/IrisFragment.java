package com.example.arkyris;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IrisFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IrisFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final LinkedList<IrisItem> mIrisColourList = new LinkedList<>();
    private RecyclerView mRecyclerView;
    private IrisListAdapter mAdapter;

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
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IrisFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IrisFragment newInstance(String param1, String param2) {
        IrisFragment fragment = new IrisFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
                int colourName = changeColour();
                // add a new word to the List
                mIrisColourList.addFirst(new IrisItem(R.drawable.colour_rectangle, colourName, "Testing", "Testing"));
                ;
                // notify the adapter that data has changed
                mRecyclerView.getAdapter().notifyItemInserted(0);
                mRecyclerView.smoothScrollToPosition(0);
        });

        final ImageView image = rootView.findViewById(R.id.chosen_colour);
        int loadColour = changeColour();
        image.setColorFilter(loadColour);

        // Create a placeholder list of words for RecycleView.
        for (int i = 0; i < 50; i++) {
            int colourName = changeColour();
            mIrisColourList.addLast(new IrisItem(R.drawable.colour_circle, colourName, "31/03/2021", "21:21"));
        }

        // Get a handler for the RecyclerView
        mRecyclerView = rootView.findViewById(R.id.iris_recyclerview);
        // Create an adapter and supply the data
        mAdapter = new IrisListAdapter(getActivity(), mIrisColourList);
        // Connect adapter to RecyclerView
        mRecyclerView.setAdapter(mAdapter);
        // Give RecyclerView a LayoutManager, make it horizontal
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        // Inflate the layout for this fragment
        return rootView;

    }

    public void displayToast(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
        Log.e("OOPS", String.valueOf(colourName));
        return colourRes;
    }

}