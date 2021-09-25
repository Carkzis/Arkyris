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
 * Adapter class for adding IrisEntryItem data to the IrisFragment RecyclerView.
 */
public class IrisListAdapter extends RecyclerView.Adapter<IrisListAdapter.IrisViewHolder> {

    private final LayoutInflater mInflater;
    private List<IrisEntryItem> mEntries; // Cached copy of words
    private static ClickListener clickListener;

    public IrisListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    /**
     * This inflates the item layout and returns a ViewHolder with the layout and the adapter.
     * It is similar to the onCreate method.
     */
    @NonNull
    @Override
    public IrisListAdapter.IrisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.list_item_iris, parent, false);
        return new IrisViewHolder(mItemView, this);
    }

    /**
     * Connects your data to the view holder.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull IrisListAdapter.IrisViewHolder holder, int position) {
        IrisEntryItem mCurrent = mEntries.get(position);
        holder.mImageView.setImageResource(R.drawable.colour_circle);
        holder.mImageView.setColorFilter(mCurrent.getColour());
        holder.mDateView.setText(mCurrent.getDate());
        holder.mTimeView.setText(mCurrent.getTime());
    }

    /**
     * Sets the LiveData entries, and notifies the adapter if the data set has changed.
     */
    void setEntries(List<IrisEntryItem> entries){
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
    static class IrisViewHolder extends RecyclerView.ViewHolder {

        public final ImageView mImageView;
        public final TextView mDateView;
        public final TextView mTimeView;

        final IrisListAdapter mAdapter;

        public IrisViewHolder(@NonNull View itemView, IrisListAdapter adapter) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iris_colour);
            mDateView = itemView.findViewById(R.id.iris_date);
            mTimeView = itemView.findViewById(R.id.iris_time);

            // Sets a click listener to the ViewHolder, allowing the user to perform actions on it.
            itemView.setOnLongClickListener(v -> {
                clickListener.onItemClick(v, getAdapterPosition());
                return true;
            });

            this.mAdapter = adapter;
        }
    }

    /**
     * Returns the entry at a particular position.
     */
    public IrisEntryItem getEntryAtPosition(int position) {
        return mEntries.get(position);
    }

    /**
     * Sets a click listener to the adapter.
     */
    public void setOnItemClickListener(ClickListener clickListener) {
        IrisListAdapter.clickListener = clickListener;
    }

    /**
     * Interface for the click listener, taking in the view and position as arguments
     * so that we are listening on a particular item.
     */
    public interface ClickListener {
        void onItemClick(View v, int position);
    }
}
