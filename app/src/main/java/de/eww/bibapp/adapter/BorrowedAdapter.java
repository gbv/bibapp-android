package de.eww.bibapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.iconics.IconicsDrawable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.eww.bibapp.R;
import de.eww.bibapp.network.model.paia.PaiaItem;
import de.eww.bibapp.typeface.BeluginoFont;

public class BorrowedAdapter extends ListAdapter<PaiaItem, BorrowedAdapter.ViewHolder> {

    private Context mContext;
    private SelectionTracker<String> selectionTracker;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mAbout;
        public TextView mSignature;

        private TextView dueDateLabel;
        public TextView mDate;
        private ImageView dueDateWarning;

        public TextView mQueue;
        public TextView mRenewals;
        public TextView mStatus;

        public ViewHolder(View itemView) {
            super(itemView);

            mAbout = itemView.findViewById(R.id.about);
            mSignature = itemView.findViewById(R.id.signature);
            this.dueDateLabel = itemView.findViewById(R.id.label_duedate);
            mDate = itemView.findViewById(R.id.duedate);
            this.dueDateWarning = itemView.findViewById(R.id.duedate_warning);
            mQueue = itemView.findViewById(R.id.queue);
            mRenewals = itemView.findViewById(R.id.renewals);
            mStatus = itemView.findViewById(R.id.status);
        }

        public ItemDetailsLookup.ItemDetails<String> getItemDetails() {
            return new ItemDetailsLookup.ItemDetails<>() {
                @Override
                public int getPosition() {
                    return getBindingAdapterPosition();
                }

                @Nullable
                @Override
                public String getSelectionKey() {
                    return BorrowedAdapter.this.getItem(getBindingAdapterPosition()).getItem();
                }
            };
        }
    }

    public static final DiffUtil.ItemCallback<PaiaItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<PaiaItem>() {

        @Override
        public boolean areItemsTheSame(@NonNull PaiaItem oldItem, @NonNull PaiaItem newItem) {
            return oldItem.getItem().equals(newItem.getItem());
        }

        @Override
        public boolean areContentsTheSame(@NonNull PaiaItem oldItem, @NonNull PaiaItem newItem) {
            return oldItem.equals(newItem);
        }
    };

    public BorrowedAdapter(Context context) {
        super(DIFF_CALLBACK);

        mContext = context;
    }

    @Override
    public void submitList(@Nullable List<PaiaItem> list) {
        super.submitList(list != null ? new ArrayList<>(list) : null);
    }

    public void setSelectionTracker(SelectionTracker<String> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    public List<PaiaItem> getSelectedItems() {
        List<PaiaItem> selectedItems = new ArrayList<>();

        for (PaiaItem item: getCurrentList()) {
            if (selectionTracker.getSelection().contains(item.getItem())) {
                selectedItems.add(item);
            }
        }

        return selectedItems;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_borrowed_view, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaiaItem item = getItem(position);

        holder.mAbout.setText(item.getAbout());
        holder.mSignature.setText(item.getLabel());

        SimpleDateFormat dateFormatWithoutTime = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY);

        if (item.getEndTime() != null) {
            Date endDate = item.getEndTime();
            String endDateString = dateFormatWithTime.format(endDate);

            if (endDateString.contains("00:00")) {
                endDateString = dateFormatWithoutTime.format(endDate);
            }

            if (endDate.before(new Date())) {
                this.makeDueDateWarning(holder);
            }

            holder.mDate.setText(endDateString);
        } else if (item.getDueDate() != null) {
            Date dueDate = item.getDueDate();

            String dueDateString = dateFormatWithTime.format(dueDate);

            if (dueDateString.contains("00:00")) {
                dueDateString = dateFormatWithoutTime.format(dueDate);
            }

            if (dueDate.before(new Date())) {
                this.makeDueDateWarning(holder);
            }

            holder.mDate.setText(dueDateString);
        }

        holder.mQueue.setText(String.valueOf(item.getQueue()));
        holder.mRenewals.setText(String.valueOf(item.getRenewals()));

        int statusCode = item.getStatus();
        String[] statusTranslations = mContext.getResources().getStringArray(R.array.paia_service_status);
        holder.mStatus.setText(statusTranslations[statusCode]);

        boolean isSelected = selectionTracker.isSelected(item.getItem());
        holder.itemView.setActivated(isSelected);
        holder.itemView.setEnabled(item.isCanRenew());
    }

    private void makeDueDateWarning(ViewHolder viewHolder) {
        viewHolder.dueDateLabel.setTextColor(Color.RED);
        viewHolder.mDate.setTextColor(Color.RED);
        viewHolder.dueDateWarning.setVisibility(View.VISIBLE);
        viewHolder.dueDateWarning.setImageDrawable(new IconicsDrawable(this.mContext)
            .icon(BeluginoFont.Icon.bel_warning)
            .color(Color.RED)
            .sizeDp(16));
    }
}
