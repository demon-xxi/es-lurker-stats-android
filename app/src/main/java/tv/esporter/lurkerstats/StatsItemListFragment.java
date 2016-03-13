package tv.esporter.lurkerstats;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import tv.esporter.lurkerstats.service.StatsItem;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnStatsItemListFragmentInteractionListener}
 * interface.
 */
public class StatsItemListFragment extends Fragment {

    private OnStatsItemListFragmentInteractionListener mListener;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private StatsItemRecyclerViewAdapter mRecyclerViewAdapter;
    private List<StatsItem> mData = null;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StatsItemListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.stats_item_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        Context context = view.getContext();

        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        mRecyclerViewAdapter = new StatsItemRecyclerViewAdapter(mData, mListener);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        if (mData != null) dataReady();
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
        super.onAttach(context);
        if (context instanceof OnStatsItemListFragmentInteractionListener) {
            mListener = (OnStatsItemListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStatsItemListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        mData = null;
    }
}
