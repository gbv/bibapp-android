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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.network.model.ModsItem;
import de.eww.bibapp.util.ModsHelper;

public class WatchlistAdapter extends ListAdapter<ModsItem, WatchlistAdapter.ViewHolder> {

    private final Context mContext;
    private final View.OnClickListener mOnClickListener;
    private SelectionTracker<String> selectionTracker;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitle;
        public TextView mSub;
        public TextView mAuthor;
        public ImageView mImage;

        public ViewHolder(View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.title);
            mSub = itemView.findViewById(R.id.sub);
            mAuthor = itemView.findViewById(R.id.author);
            mImage = itemView.findViewById(R.id.image);
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
                    return WatchlistAdapter.this.getItem(getBindingAdapterPosition()).ppn;
                }
            };
        }
    }

    public static final DiffUtil.ItemCallback<ModsItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {

        @Override
        public boolean areItemsTheSame(@NonNull ModsItem oldItem, @NonNull ModsItem newItem) {
            return oldItem.ppn.equals(newItem.ppn);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ModsItem oldItem, @NonNull ModsItem newItem) {
            return oldItem.equals(newItem);
        }
    };

    // Suitable constructor for list type
    public WatchlistAdapter(Context context, View.OnClickListener onClickListener) {
        super(DIFF_CALLBACK);

        mContext = context;
        mOnClickListener = onClickListener;
    }

    @Override
    public void submitList(@Nullable List<ModsItem> list) {
        super.submitList(list != null ? new ArrayList<>(list) : null);
    }

    public void setSelectionTracker(SelectionTracker<String> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    public List<ModsItem> getSelectedItems() {
        List<ModsItem> selectedItems = new ArrayList<>();

        for (ModsItem item : getCurrentList()) {
            if (selectionTracker.getSelection().contains(item.ppn)) {
                selectedItems.add(item);
            }
        }

        return selectedItems;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mods_view, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ModsItem item = getItem(position);
        holder.itemView.setTag(item);

        holder.mTitle.setText(item.title);

        String subTitle = item.subTitle;
        if (!item.partName.isEmpty()) {
            subTitle = item.partName + "; " + subTitle;
        }
        if (!item.partNumber.isEmpty()) {
            subTitle = item.partNumber + "; " + subTitle;
        }
        holder.mSub.setText(subTitle);

        StringBuilder authorString = new StringBuilder();
        Iterator<String> it = item.authors.iterator();
        while (it.hasNext()) {
            String author = it.next();
            authorString.append(author);

            if (it.hasNext()) {
                authorString.append(", ");
            }
        }
        holder.mAuthor.setText(authorString.toString());

        holder.mImage.setImageDrawable(new IconicsDrawable(this.mContext)
                .icon(ModsHelper.getBeluginoFontIcon(item))
                .color(Color.LTGRAY)
                .sizeDp(36));

        boolean isSelected = selectionTracker.isSelected(item.ppn);
        holder.itemView.setActivated(isSelected);

        holder.itemView.setOnClickListener(mOnClickListener);
    }
}