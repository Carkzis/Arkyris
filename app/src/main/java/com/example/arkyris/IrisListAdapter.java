package com.example.arkyris;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IrisListAdapter extends RecyclerView.Adapter<IrisListAdapter.IrisViewHolder> {

    private static final String LOG_TAG = IrisListAdapter.class.getSimpleName();

    private final LayoutInflater mInflater;
    private List<EntryItem> mEntries; // Cached copy of words

    public IrisListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    /**
     * This inflates the item layout and returns a ViewHolder with the layout and the adapter.
     * It is similar to the onCreate method.
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public IrisListAdapter.IrisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.list_item_iris, parent, false);
        return new IrisViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull IrisListAdapter.IrisViewHolder holder, int position) {
        EntryItem mCurrent = mEntries.get(position);
        holder.mImageView.setImageResource(R.drawable.colour_circle);
        holder.mImageView.setColorFilter(mCurrent.getColour());
        holder.mDateView.setText(mCurrent.getDate());
        holder.mTimeView.setText(mCurrent.getTime());
    }

    void setEntries(List<EntryItem> entries){
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

    class IrisViewHolder extends RecyclerView.ViewHolder {

        public final ImageView mImageView;
        public final TextView mDateView;
        public final TextView mTimeView;

        final IrisListAdapter mAdapter;

        public IrisViewHolder(@NonNull View itemView, IrisListAdapter adapter) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iris_colour);
            mDateView = itemView.findViewById(R.id.iris_date);
            mTimeView = itemView.findViewById(R.id.iris_time);

            this.mAdapter = adapter;
        }
    }
}
