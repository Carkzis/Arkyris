package com.example.arkyris;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class ArkeListAdapter extends RecyclerView.Adapter<ArkeListAdapter.ArkeViewHolder> {

    private final LinkedList<String> mWordList;
    private LayoutInflater mInflater;

    public ArkeListAdapter(Context context, LinkedList<String> wordList) {
        mInflater = LayoutInflater.from(context);
        this.mWordList = wordList;
    }

    /**
     * This inflates the item layout and returns a ViewHolder with the layout and the adapter.
     * It is similar to the onCreate method.
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ArkeListAdapter.ArkeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.list_item_arke, parent, false);
        return new ArkeViewHolder(mItemView, this);
    }

    /**
     * Connects your data to the view holder.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ArkeListAdapter.ArkeViewHolder holder, int position) {
        String mCurrent = mWordList.get(position);
        holder.arkeItemView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return mWordList.size();
    }

    class ArkeViewHolder extends RecyclerView.ViewHolder {

        public final TextView arkeItemView;
        final ArkeListAdapter mAdapter;

        public ArkeViewHolder(@NonNull View itemView, ArkeListAdapter adapter) {
            super(itemView);
            arkeItemView = itemView.findViewById(R.id.colour_arke);
            this.mAdapter = adapter;
        }
    }
}
