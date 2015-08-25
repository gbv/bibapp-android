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
     * The current offset index of data we have loaded.
     */
    private int mCurrentPage = 0;

    /**
     * The total number of items in the dataset after the last load.
     */
    private int mPreviousTotalItemCount = 0;

    /**
     * True if we are still waiting for the next set of data to load.
     */
    private boolean mLoading = true;

    /**
     * The starting page index
     */
    private int mStartingPageIndex = 0;

    public EndlessScrollListener() {

    }

    public EndlessScrollListener(int visibleThreshold) {
        mVisibleThreshold = visibleThreshold;
    }

    public EndlessScrollListener(int visibleThreshold, int startPage) {
        mVisibleThreshold = visibleThreshold;
        mStartingPageIndex = startPage;
        mCurrentPage = startPage;
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

        /**
         * If the total item count is lower than the previous, assume the
         * list is invalidated and should be reset back to initial state.
         */
        if (totalItemCount < mPreviousTotalItemCount) {
            mCurrentPage = mStartingPageIndex;
            mPreviousTotalItemCount = totalItemCount;

            if (totalItemCount == 0) {
                mLoading = true;
            }
        }

        /**
         * If it's still loading, we check to see if the dataset count has
         * changed, if so we conclude it has finished loading and update the
         * current page number and total item count.
         */
        if (mLoading && (totalItemCount > mPreviousTotalItemCount)) {
            mLoading = false;
            mPreviousTotalItemCount = totalItemCount;
            mCurrentPage++;
        }

        /**
         * If it isn't currently loading, we check to see if we have breached
         * the visibleThreshold and need to reload more data.
         * If we do need to reload some more data, we execute onLoadMore to fetch
         * the data.
         */
        if (!mLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + mVisibleThreshold)) {
            onLoadMore(mCurrentPage + 1, totalItemCount);
            mLoading = true;
        }
    }

    public abstract void onLoadMore(int page, int totalItemCount);

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

    }
}