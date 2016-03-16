package tv.esporter.lurkerstats;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tv.esporter.lurkerstats.service.DataServiceHelper;
import tv.esporter.lurkerstats.service.StatsItem;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnStatsItemListFragmentInteractionListener}
 * interface.
 */
public class StatsListFragment extends Fragment {

//    private final int count;
    private ViewerActivity mActivity;

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String STATE_POSITION_INDEX = "state_position_index";
    private static final String STATE_POSITION_OFFSET = "state_position_offset";

    private int mColumnCount = 1;
    private StatsRecyclerViewAdapter mRecyclerViewAdapter;
    private List<StatsItem> mData = null;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private RecyclerView.LayoutManager mLayoutManager;

//    static int CNT = 1;

//    private ViewPreloadSizeProvider<StatsItem> preloadSizeProvider;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StatsListFragment() {
//        this.count = CNT++;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
//        Log.v(">>>> StatsListFragment", "onCreate " + count);
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }


        if (getArguments() != null && getArguments().containsKey(DataServiceHelper.EXTRA_STATS_TYPE)){
            mActivity.subscibeForData(
                    (StatsItem.Type)getArguments().getSerializable(DataServiceHelper.EXTRA_STATS_TYPE),
                    this);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        Log.v(">>>> StatsListFragment", "onCreateView " + count);
        View view = inflater.inflate(R.layout.stats_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        Context context = view.getContext();

        mLayoutManager = mColumnCount > 1
                ? new GridLayoutManager(context, mColumnCount)
                : new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerViewAdapter = new StatsRecyclerViewAdapter(mData, mActivity);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        if (mData != null) setData(mData);

        if (savedInstanceState != null && mLayoutManager instanceof LinearLayoutManager) {
            // TODO: Add logic for GridLayoutManager
            int index = savedInstanceState.getInt(STATE_POSITION_INDEX);
            int offset = savedInstanceState.getInt(STATE_POSITION_OFFSET);
            ((LinearLayoutManager)mLayoutManager).scrollToPositionWithOffset(index, offset);
        }

        return view;
    }

    void dataReady(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    public void setData(List<StatsItem> list) {
        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.update(list);
            dataReady();
        } else {
            mData = list;
        }
    }

    @Override
    public void onAttach(Context context) {
//        Log.v(">>>> StatsListFragment", "onAttach " + count);
        super.onAttach(context);
        if (context instanceof ViewerActivity) {
            mActivity = (ViewerActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStatsItemListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
//        Log.v(">>>> StatsListFragment", "onDetach " + count);
        super.onDetach();
        mActivity.unSubscibeForData(this);
        mActivity = null;
    }

//    @Override
//    public void onPause() {
//        Log.v(">>>> StatsListFragment", "onPause " + count);
//        super.onPause();
//        mData = null;
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        Log.v(">>>> StatsListFragment", "onSaveInstanceState " + count).;
        super.onSaveInstanceState(outState);
        if (mRecyclerView != null && mLayoutManager instanceof LinearLayoutManager) {
            int index = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
            View topView = mRecyclerView.getChildAt(0);
            int offset = topView != null ? topView.getTop() : 0;
            outState.putInt(STATE_POSITION_INDEX, index);
            outState.putInt(STATE_POSITION_OFFSET, offset);
        }
    }

    /**
     * {@link RecyclerView.Adapter} that can display a {@link StatsItem} and makes a call to the
     * specified {@link OnStatsItemListFragmentInteractionListener}.
     */
    private class StatsRecyclerViewAdapter extends RecyclerView.Adapter<StatsRecyclerViewAdapter.ViewHolder> {

        private int mBackground;
        private final TypedValue mTypedValue = new TypedValue();
        private final List<StatsItem> mValues = new ArrayList<>();
        private final ViewerActivity mListener;

        private long maxDuration = 0L;

        public StatsRecyclerViewAdapter(List<StatsItem> items, ViewerActivity listener) {
            mListener = listener;
            if (items != null) {
                update(items);
            }

            StatsListFragment.this.getContext().getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
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
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            Picasso.with(StatsListFragment.this.getContext()).cancelRequest(holder.mImageView);
            holder.mImageView.setImageDrawable(null);
            super.onViewRecycled(holder);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            StatsItem current = mValues.get(position);
            holder.mItem = current;
            holder.mTitleView.setText(current.title);
            String label = DateUtils.formatElapsedTime(current.value);
            holder.mLabelView.setText(label);
    //        Context context = holder.mImageView.getContext();
    //        final float scale = context.getResources().getDisplayMetrics().density;
    //        int radius = (int) (5 * scale);
            holder.mImageView.setImageDrawable(null);
            if (current.image != null && !current.image.isEmpty()) {
                Picasso.with(StatsListFragment.this.getContext())
                        .load(current.image)
                        .placeholder(R.drawable.noavatar)
                        .fit()
                        .centerInside()
                        .into(holder.mImageView);
            } else  {
                Picasso.with(StatsListFragment.this.getContext())
                        .load(R.drawable.noavatar)
                        .fit()
                        .centerInside()
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
            public final TextView mLabelView;
            public final ProgressBar mProgressView;
            public final ImageView mImageView;
            public StatsItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.stats_list_item_image);
                mTitleView = (TextView) view.findViewById(R.id.stats_list_item_title);
                mLabelView = (TextView) view.findViewById(R.id.stats_list_item_label);
                mProgressView = (ProgressBar) view.findViewById(R.id.stats_list_item_progress);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTitleView.getText() + "'";
            }
        }
    }
}
