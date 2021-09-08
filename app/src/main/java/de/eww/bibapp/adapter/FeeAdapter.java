package de.eww.bibapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.eww.bibapp.R;
import de.eww.bibapp.network.model.paia.PaiaFee;
import de.eww.bibapp.network.model.paia.PaiaFees;
import de.eww.bibapp.network.model.paia.PaiaItem;

public class FeeAdapter extends ListAdapter<PaiaFee, FeeAdapter.ViewHolder> {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mAmount;
        public TextView mAbout;
        public TextView mDate;

        public ViewHolder(View itemView) {
            super(itemView);

            mAmount = itemView.findViewById(R.id.amount);
            mAbout = itemView.findViewById(R.id.about);
            mDate = itemView.findViewById(R.id.date);
        }
    }

    public static final DiffUtil.ItemCallback<PaiaFee> DIFF_CALLBACK = new DiffUtil.ItemCallback<PaiaFee>() {

        @Override
        public boolean areItemsTheSame(@NonNull PaiaFee oldItem, @NonNull PaiaFee newItem) {
            return oldItem.getItem().equals(newItem.getItem());
        }

        @Override
        public boolean areContentsTheSame(@NonNull PaiaFee oldItem, @NonNull PaiaFee newItem) {
            return oldItem.getItem().equals(newItem.getItem());
        }
    };

    public FeeAdapter() {
        super(DIFF_CALLBACK);
    }

    @Override
    public void submitList(@Nullable List<PaiaFee> list) {
        super.submitList(list != null ? new ArrayList<>(list) : null);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fee_view, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaiaFee item = getItem(position);

        holder.mAmount.setText(item.getAmount());
        holder.mAbout.setText(item.getAbout());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        holder.mDate.setText(dateFormat.format(item.getDate()));
    }
}
