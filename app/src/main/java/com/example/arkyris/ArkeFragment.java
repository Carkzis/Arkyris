package com.example.arkyris;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArkeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArkeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final LinkedList<ArkeItem> mArkeColourList = new LinkedList<>();
    private RecyclerView mRecyclerView;
    private ArkeListAdapter mAdapter;

    public ArkeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArkeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArkeFragment newInstance(String param1, String param2) {
        ArkeFragment fragment = new ArkeFragment();
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

    /**
     * Note to self: for fragments, treat this as onCreate.
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
            int wordListSize = mArkeColourList.size();
            // add a new word to the List
            mArkeColourList.addFirst(new ArkeItem(R.drawable.colour_rectangle, "Testing", "Testing"));;
            // notify the adapter that data has changed
            mRecyclerView.getAdapter().notifyItemInserted(0);
            mRecyclerView.smoothScrollToPosition(0);

        });

        // Create a placeholder list of words for RecycleView.
        for (int i = 0; i < 50; i++) {
            mArkeColourList.addLast(new ArkeItem(R.drawable.colour_rectangle, "31/03/21", "21:21"));
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
}