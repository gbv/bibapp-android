package de.eww.bibapp.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.model.DaiaItem;

/**
 * Created by christoph on 10.11.14.
 */
public class DaiaAdapter extends RecyclerView.Adapter<DaiaAdapter.ViewHolder> {

    private List<DaiaItem> mItemList;

    private boolean mIsLocalSearch = true;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mDistance;
        public TextView mDepartment;
        public TextView mLabel;
        public TextView mStatus;
        public TextView mStatusInfo;

        public ViewHolder(View itemView) {
            super(itemView);

            mDistance = (TextView) itemView.findViewById(R.id.distance);
            mDepartment = (TextView) itemView.findViewById(R.id.department);
            mLabel = (TextView) itemView.findViewById(R.id.label);
            mStatus = (TextView) itemView.findViewById(R.id.status);
            mStatusInfo = (TextView) itemView.findViewById(R.id.status_info);
        }
    }

    // Suitable constructor for list type
    public DaiaAdapter(List<DaiaItem> itemList) {
        mItemList = itemList;
    }

    public void setIsLocalSearch(boolean isLocalSearch) {
        mIsLocalSearch = isLocalSearch;
    }

    public DaiaItem getItem(int position) {
        return mItemList.get(position);
    }

    public void clear() {
        mItemList.clear();
    }

    public void addDaiaItems(List<DaiaItem> daiaItems) {
        mItemList.addAll(daiaItems);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daia_view, parent, false);

        // Set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DaiaItem item = mItemList.get(position);

        // department
        String departmentText = "";
        if (!Constants.EXEMPLAR_SHORT_DISPLAY) {
            if (item.department != null && !item.department.isEmpty()) {
                departmentText = item.department;
            }

            if (item.storage != null && !item.storage.isEmpty()) {
                if (!departmentText.isEmpty()) {
                    departmentText += ", ";
                }

                departmentText += item.storage;
            }
        } else {
            if (item.storage != null && !item.storage.isEmpty()) {
                departmentText = item.storage;
            } else if (item.department != null && !item.department.isEmpty()) {
                departmentText = item.department;
            }
        }

        if (!departmentText.isEmpty()) {
            holder.mDepartment.setText(departmentText);
        }

        // label
        holder.mLabel.setText(item.label);

        // status
        if (mIsLocalSearch) {
            holder.mStatus.setText(item.status);
            holder.mStatus.setTextColor(Color.parseColor(item.statusColor));
            holder.mStatus.setVisibility(View.VISIBLE);
            holder.mStatusInfo.setText(item.statusInfo);
            holder.mStatusInfo.setVisibility(View.VISIBLE);
        } else {
            holder.mStatus.setText("");
            holder.mStatusInfo.setText("");
        }

        // distance
        if (item.distance != null) {
            holder.mDistance.setText(String.format("%.2f km", item.distance));
            holder.mDistance.setVisibility(View.VISIBLE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (mItemList != null ? mItemList.size() : 0);
    }
}