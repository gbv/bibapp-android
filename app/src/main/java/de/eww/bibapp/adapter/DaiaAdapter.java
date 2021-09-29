package de.eww.bibapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.network.model.ModsItem;
import de.eww.bibapp.network.model.DaiaItem;
import de.eww.bibapp.network.model.daia.DaiaEntity;
import de.eww.bibapp.util.DaiaHelper;

public class DaiaAdapter extends ListAdapter<DaiaItem, DaiaAdapter.ViewHolder> {

    private ModsItem modsItem;
    private Context context;
    private final View.OnClickListener mOnClickListener;

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

    public static final DiffUtil.ItemCallback<DaiaItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {

        @Override
        public boolean areItemsTheSame(@NonNull DaiaItem oldItem, @NonNull DaiaItem newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DaiaItem oldItem, @NonNull DaiaItem newItem) {
            return oldItem.equals(newItem);
        }
    };

    // Suitable constructor for list type
    public DaiaAdapter(ModsItem modsItem, Context context, View.OnClickListener onClickListener) {
        super(DIFF_CALLBACK);

        this.modsItem = modsItem;
        this.context = context;
        mOnClickListener = onClickListener;
    }

    @Override
    public void submitList(@Nullable List<DaiaItem> list) {
        super.submitList(list != null ? new ArrayList<>(list) : null);
    }

    public void sortByDistance() {
        Collections.sort(getCurrentList());
        submitList(getCurrentList());
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daia_view, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DaiaItem item = getItem(position);
        holder.itemView.setTag(item);

        // department
        String departmentText = "";
        DaiaEntity storage = item.getStorage();
        if (!this.context.getResources().getBoolean(R.bool.use_exemplar_short_display)) {
            if (item.getDepartment() != null && !item.getDepartment().isEmpty()) {
                departmentText = item.getDepartment();
            }

            if (storage != null) {
                String storageContent = storage.getContent();

                if (storageContent != null && !storageContent.isEmpty()) {
                    if (!departmentText.isEmpty()) {
                        departmentText += ", ";
                    }

                    departmentText += storageContent;
                }
            }
        } else {
            if (storage != null && storage.getContent() != null && !storage.getContent().isEmpty()) {
                departmentText = storage.getContent();
            } else if (item.getDepartment() != null && !item.getDepartment().isEmpty()) {
                departmentText = item.getDepartment();
            }
        }

        if (!departmentText.isEmpty()) {
            if (modsItem.onlineUrl.isEmpty()) {
                holder.mDepartment.setText(departmentText);
                holder.mDepartment.setVisibility(View.VISIBLE);
            }
        }

        // label
        holder.mLabel.setText(item.getLabel());

        // status
        if (modsItem.isLocalSearch) {
            try {
                HashMap<String, String> daiaInformation = DaiaHelper.getInformation(item, modsItem, context);

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
        if (item.getDistance() != null) {
            holder.mDistance.setText(String.format("%.2f km", item.getDistance()));
            holder.mDistance.setVisibility(View.VISIBLE);
        } else {
            holder.mDistance.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(mOnClickListener);
    }
}