package de.eww.bibapp.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by christoph on 25.08.15.
 */
public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {
    /**
     * The minimum amount of items to have below current scroll position
     * before loading more.
     */
    private int mVisibleThreshold = 3;

    /**
     * The total number of items in the dataset after the last load.
     */
    private int mPreviousTotalItemCount = 0;

    /**
     * True if we are still waiting for the next set of data to load.
     */
    private boolean mLoading = true;

    public EndlessScrollListener() {

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        LinearLayoutManager layoutManager;
        try {
            layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        } catch (ClassCastException e) {
            throw new ClassCastException(recyclerView.getLayoutManager().toString() + " must be of type LinearLayoutManager");
        }

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        if (mLoading) {
            if (totalItemCount > mPreviousTotalItemCount) {
                mLoading = false;
                mPreviousTotalItemCount = totalItemCount;
            }
        }

        if (!mLoading) {
            // Did we reach the end of the list?
            if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + mVisibleThreshold)) {
                mLoading = true;

                onLoadMore();
            }
        }
    }

    public abstract void onLoadMore();

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

    }
}