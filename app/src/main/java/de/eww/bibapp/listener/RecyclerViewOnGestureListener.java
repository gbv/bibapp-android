package de.eww.bibapp.listener;

import android.content.Context;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by christoph on 03.11.14.
 */
public class RecyclerViewOnGestureListener extends GestureDetector.SimpleOnGestureListener implements
        RecyclerView.OnItemTouchListener {

    GestureDetectorCompat mGestureDetector;
    RecyclerView mRecyclerView;

    private OnGestureListener mListener;

    public interface OnGestureListener {
        public void onClick(View view, int position);
        public void onLongPress(View view, int position);
    }

    public RecyclerViewOnGestureListener(Context context, RecyclerView recyclerView) {
        mGestureDetector = new GestureDetectorCompat(context, this);
        mRecyclerView = recyclerView;
    }

    public void setOnGestureListener(OnGestureListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null) {
            mGestureDetector.onTouchEvent(e);
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        // empty
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)  {
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
        mListener.onClick(view, mRecyclerView.getChildPosition(view));

        return super.onSingleTapConfirmed(e);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
        mListener.onLongPress(view, mRecyclerView.getChildPosition(view));

        super.onLongPress(e);
    }
}
