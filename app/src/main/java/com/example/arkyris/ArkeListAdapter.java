package com.example.arkyris;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ArkeListAdapter extends RecyclerView.Adapter<ArkeListAdapter.ArkeViewHolder> {

    private static final String LOG_TAG = ArkeListAdapter.class.getSimpleName();

    private final LayoutInflater mInflater;
    private Context context;
    private List<ArkeItem> mEntries; // Cached copy of words

    public ArkeListAdapter(Context context) { mInflater = LayoutInflater.from(context);
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
        if (mEntries != null) {
            ArkeItem mCurrent = mEntries.get(position);
            holder.mImageView.setImageResource(R.drawable.colour_rectangle);
            holder.mImageView.setColorFilter(mCurrent.getColour());
            // holder.mImageView.setColorFilter(context.getResources().getColor(R.color.green));
            holder.mDateView.setText(mCurrent.getDate());
            holder.mTimeView.setText(mCurrent.getTime());
        } else {
            Log.e(LOG_TAG, "Not working!");
        }
    }

    void setEntries(List<ArkeItem> entries){
        mEntries = entries;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mEntries != null) {
            return mEntries.size();
        }
            return 0;
    }

    class ArkeViewHolder extends RecyclerView.ViewHolder {

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
