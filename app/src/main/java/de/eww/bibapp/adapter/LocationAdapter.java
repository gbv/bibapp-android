package de.eww.bibapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.model.LocationItem;

/**
 * Created by christoph on 30.10.14.
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> implements
        View.OnClickListener {

    private List<LocationItem> mItemList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.title);
        }
    }

    // Suitable constructor for list type
    public LocationAdapter(List<LocationItem> itemList) {
        mItemList = itemList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rss_item_view, parent, false);

        // Set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LocationItem item = mItemList.get(position);

        holder.mTitle.setText(item.listName);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (mItemList != null ? mItemList.size() : 0);
    }

    @Override
    public void onClick(View view) {
//        ViewHolder holder = (ViewHolder) view.getTag();
//        if (view.getId() == holder.mNameTextView.getId()) {
//            Toast.makeText(sContext, holder.mNameTextView.getText(), Toast.LENGTH_SHORT).show();
//        }
    }
}
