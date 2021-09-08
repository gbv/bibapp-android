package de.eww.bibapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.eww.bibapp.R;
import de.eww.bibapp.network.model.paia.PaiaItem;

public class BookedAdapter extends ListAdapter<PaiaItem, BookedAdapter.ViewHolder> {

    private SelectionTracker<String> selectionTracker;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mAbout;
        public TextView mSignature;
        public TextView mDate;

        public ViewHolder(View itemView) {
            super(itemView);

            mAbout = itemView.findViewById(R.id.about);
            mSignature = itemView.findViewById(R.id.signature);
            mDate = itemView.findViewById(R.id.date);
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
                    return BookedAdapter.this.getItem(getBindingAdapterPosition()).getItem();
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

    public BookedAdapter() {
        super(DIFF_CALLBACK);
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

        for (PaiaItem item : getCurrentList()) {
            if (selectionTracker.getSelection().contains(item.getItem())) {
                selectedItems.add(item);
            }
        }

        return selectedItems;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booked_view, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaiaItem item = getItem(position);

        holder.mAbout.setText(item.getAbout());
        holder.mSignature.setText(item.getLabel());

        if (item.getStartTime() != null) {
            SimpleDateFormat dateFormatWithoutTime = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
            SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY);

            Date startDate = item.getStartTime();
            String startDateString = dateFormatWithTime.format(startDate);

            if (startDateString.contains("00:00")) {
                startDateString = dateFormatWithoutTime.format(startDate);
            }

            holder.mDate.setText(startDateString);
        }

        boolean isSelected = selectionTracker.isSelected(item.getItem());
        holder.itemView.setActivated(isSelected);
        holder.itemView.setEnabled(item.isCanCancel());
    }
}
