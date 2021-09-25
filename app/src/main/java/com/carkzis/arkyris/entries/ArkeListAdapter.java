package com.carkzis.arkyris.entries;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arkyris.R;

import java.util.List;

/**
 * Adapter class for adding ArkeEntryItem data to the ArkeFragment RecyclerView.
 */
public class ArkeListAdapter extends RecyclerView.Adapter<ArkeListAdapter.ArkeViewHolder> {

    private final LayoutInflater mInflater;
    private List<ArkeEntryItem> mEntries; // Cached copy of words

    public ArkeListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    /**
     * This inflates the item layout and returns a ViewHolder with the layout and the adapter.
     * It is similar to the onCreate method.
     */
    @NonNull
    @Override
    public ArkeListAdapter.ArkeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.list_item_arke, parent, false);
        return new ArkeViewHolder(mItemView, this);
    }

    /**
     * Connects your data to the view holder.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ArkeListAdapter.ArkeViewHolder holder, int position) {
        ArkeEntryItem mCurrent = mEntries.get(position);
        holder.mImageView.setImageResource(R.drawable.colour_rectangle);
        holder.mImageView.setColorFilter(mCurrent.getColour());
        holder.mDateView.setText(mCurrent.getDate());
        holder.mTimeView.setText(mCurrent.getTime());
    }

    /**
     * Sets the LiveData entries, and notifies the adapter if the data set has changed.
     */
    void setEntries(List<ArkeEntryItem> entries){
        mEntries = entries;
        notifyDataSetChanged();
    }

    /**
     * Retrieves the amount of items in the RecyclerView.
     */
    @Override
    public int getItemCount() {
        if (mEntries != null) {
            return mEntries.size();
        }
            return 0;
    }

    /**
     * Describes the item view (it "holds" the item) and any associated data,
     * which is then contained within the RecyclerView.
     */
    static class ArkeViewHolder extends RecyclerView.ViewHolder {

        public final ImageView mImageView;
        public final TextView mDateView;
        public final TextView mTimeView;

        final ArkeListAdapter mAdapter;

        public ArkeViewHolder(@NonNull View itemView, ArkeListAdapter adapter) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.arke_colour);
            mDateView = itemView.findViewById(R.id.arke_date);
            mTimeView = itemView.findViewById(R.id.arke_time);

            this.mAdapter = adapter;
        }
    }
}
