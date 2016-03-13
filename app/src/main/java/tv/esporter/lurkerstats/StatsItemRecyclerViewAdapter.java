package tv.esporter.lurkerstats;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import tv.esporter.lurkerstats.service.StatsItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link StatsItem} and makes a call to the
 * specified {@link OnStatsItemListFragmentInteractionListener}.
 */
public class StatsItemRecyclerViewAdapter extends RecyclerView.Adapter<StatsItemRecyclerViewAdapter.ViewHolder> {

    private final List<StatsItem> mValues;
    private final OnStatsItemListFragmentInteractionListener mListener;

    public StatsItemRecyclerViewAdapter(List<StatsItem> items, OnStatsItemListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void update(List<StatsItem> list) {
        mValues.clear();
        mValues.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stats_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).title);
        //holder.mImageView.setImageURI(mValues.get(position).image);
        holder.mProgressView.setProgress(mValues.get(position).value);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final ProgressBar mProgressView;
        public final ImageView mImageView;
        public StatsItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.stats_list_item_image);
            mTitleView = (TextView) view.findViewById(R.id.stats_list_item_title);
            mProgressView = (ProgressBar) view.findViewById(R.id.stats_list_item_progress);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
