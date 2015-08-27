package de.eww.bibapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.eww.bibapp.R;
import de.eww.bibapp.model.PaiaItem;

/**
 * Created by christoph on 07.11.14.
 */
public class BookedAdapter extends RecyclerView.Adapter<BookedAdapter.ViewHolder> {

    private List<PaiaItem> mItemList;
    private SparseBooleanArray mSelectedItems;
    private boolean mSelectionMode;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mAbout;
        public TextView mSignature;
        public TextView mDate;

        public ViewHolder(View itemView) {
            super(itemView);

            mAbout = (TextView) itemView.findViewById(R.id.about);
            mSignature = (TextView) itemView.findViewById(R.id.signature);
            mDate = (TextView) itemView.findViewById(R.id.date);
        }
    }

    // Suitable constructor for list type
    public BookedAdapter(List<PaiaItem> itemList) {
        mItemList = itemList;
        mSelectedItems = new SparseBooleanArray();
    }

    public void toggleSelection(int position) {
        if (mSelectedItems.get(position, false)) {
            mSelectedItems.delete(position);
        } else {
            mSelectedItems.put(position, true);
        }

        notifyDataSetChanged();
    }

    public void setSelectionMode(boolean selectionMode) {
        mSelectionMode = selectionMode;
    }

    public void clearSelection() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<Integer>(mSelectedItems.size());
        for (int i=0; i < mSelectedItems.size(); i++) {
            items.add(mSelectedItems.keyAt(i));
        }

        return items;
    }

    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    public PaiaItem getPaiaItem(int position) {
        return mItemList.get(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booked_view, parent, false);

        // Set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaiaItem item = mItemList.get(position);

        holder.mAbout.setText(item.getAbout());
        holder.mSignature.setText(item.getLabel());

        if (item.getStartDate() != null) {
            SimpleDateFormat dateFormatWithoutTime = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
            SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY);

            Date startDate = item.getStartDate();
            String startDateString = dateFormatWithTime.format(startDate);

            if (startDateString.contains("00:00")) {
                startDateString = dateFormatWithoutTime.format(startDate);
            }

            holder.mDate.setText(startDateString);
        }

        holder.itemView.setSelected(mSelectedItems.get(position, false) && item.isCanCancel());

        if (mSelectionMode && !item.isCanCancel()) {
            holder.itemView.setAlpha(0.3f);
        } else {
            holder.itemView.setAlpha(1.0f);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (mItemList != null ? mItemList.size() : 0);
    }
}
