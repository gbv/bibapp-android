package de.eww.bibapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.network.model.DaiaItem;
import de.eww.bibapp.util.DaiaHelper;

/**
 * Created by christoph on 10.11.14.
 */
public class DaiaAdapter extends RecyclerView.Adapter<DaiaAdapter.ViewHolder> {

    private List<DaiaItem> mItemList;
    private ModsItem modsItem;
    private Context context;

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

            mDistance = itemView.findViewById(R.id.distance);
            mDepartment = itemView.findViewById(R.id.department);
            mLabel = itemView.findViewById(R.id.label);
            mStatus = itemView.findViewById(R.id.status);
            mStatusInfo = itemView.findViewById(R.id.status_info);
        }
    }

    // Suitable constructor for list type
    public DaiaAdapter(List<DaiaItem> itemList, ModsItem modsItem, Context context) {
        mItemList = itemList;
        this.modsItem = modsItem;
        this.context = context;
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

    public void sortByDistance() {
        Collections.sort(mItemList);
        notifyDataSetChanged();
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
            try {
                HashMap<String, String> daiaInformation = DaiaHelper.getInformation(item, this.modsItem, this.context);

                if (daiaInformation.get("actions").contains("no_barcode_reset")) {
                    holder.mStatus.setText(R.string.daia_no_barcode);
                    holder.mStatus.setTextColor(Color.RED);
                    holder.mStatusInfo.setText("");
                } else {
                    holder.mStatus.setText(daiaInformation.get("status"));
                    holder.mStatus.setTextColor(Color.parseColor(daiaInformation.get("statusColor")));
                    holder.mStatusInfo.setText(daiaInformation.get("statusInfo"));
                }

                holder.mStatus.setVisibility(View.VISIBLE);
                holder.mStatusInfo.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            holder.mStatus.setText("");
            holder.mStatusInfo.setText("");
        }

        // distance
        if (item.distance != null) {
            holder.mDistance.setText(String.format("%.2f km", item.distance));
            holder.mDistance.setVisibility(View.VISIBLE);
        } else {
            holder.mDistance.setVisibility(View.GONE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (mItemList != null ? mItemList.size() : 0);
    }
}