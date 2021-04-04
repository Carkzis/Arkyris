package com.example.arkyris;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class IrisListAdapter extends RecyclerView.Adapter<IrisListAdapter.IrisViewHolder> {

    private final LinkedList<IrisItem> mIrisColourList;
    private LayoutInflater mInflater;
    private Context context;

    public IrisListAdapter(Context context, LinkedList<IrisItem> wordList) {
        mInflater = LayoutInflater.from(context);
        this.mIrisColourList = wordList;
        this.context = context;
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
    public IrisListAdapter.IrisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.list_item_iris, parent, false);
        return new IrisViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull IrisListAdapter.IrisViewHolder holder, int position) {
        IrisItem mCurrent = mIrisColourList.get(position);
        holder.mImageView.setImageResource(mCurrent.getImage());
        holder.mImageView.setColorFilter(mCurrent.getColour());
        holder.mDateView.setText(mCurrent.getDate());
        holder.mTimeView.setText(mCurrent.getTime());
    }

    @Override
    public int getItemCount() { return mIrisColourList.size(); }

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
