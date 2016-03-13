package tv.esporter.lurkerstats;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import tv.esporter.lurkerstats.service.StatsItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link StatsItem} and makes a call to the
 * specified {@link OnStatsItemListFragmentInteractionListener}.
 */
public class StatsItemRecyclerViewAdapter extends RecyclerView.Adapter<StatsItemRecyclerViewAdapter.ViewHolder> {

    private final List<StatsItem> mValues = new ArrayList<>();
    private final OnStatsItemListFragmentInteractionListener mListener;

    private long maxDuration = 0L;

    public StatsItemRecyclerViewAdapter(List<StatsItem> items, OnStatsItemListFragmentInteractionListener listener) {
        mListener = listener;
        if (items != null) {
            update(items);
        }
    }

    public void update(List<StatsItem> list) {
        mValues.clear();
        mValues.addAll(list);

        long max = 0;
        for(StatsItem itm : mValues){
            if (itm.value < max) continue;
            max = itm.value;
        }
        maxDuration = max;

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stats_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        Glide.clear(holder.mImageView);
        holder.mImageView.setImageDrawable(null);
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).title);
        Context context = holder.mImageView.getContext();
        final float scale = context.getResources().getDisplayMetrics().density;
        int radius = (int) (5 * scale);
        if (mValues.get(position).image != null) {
            Glide.with(context)
                    .load(mValues.get(position).image)
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .fitCenter()
//                    .bitmapTransform(new RoundedCornersTransformation(context, radius, 0) )
                    .into(holder.mImageView);
        }
        int percent = (int) (1.0 * mValues.get(position).value.intValue()/maxDuration * holder.mProgressView.getMax());
        holder.mProgressView.setProgress(percent);

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(holder.mItem);
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
