package de.eww.bibapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.eww.bibapp.R;
import de.eww.bibapp.model.FeeItem;

/**
 * Created by christoph on 07.11.14.
 */
public class FeeAdapter extends RecyclerView.Adapter<FeeAdapter.ViewHolder> {

    private List<FeeItem> mItemList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mAmount;
        public TextView mAbout;
        public TextView mDate;

        public ViewHolder(View itemView) {
            super(itemView);

            mAmount = (TextView) itemView.findViewById(R.id.amount);
            mAbout = (TextView) itemView.findViewById(R.id.about);
            mDate = (TextView) itemView.findViewById(R.id.date);
        }
    }

    // Suitable constructor for list type
    public FeeAdapter(List<FeeItem> itemList) {
        mItemList = itemList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fee_view, parent, false);

        // Set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FeeItem item = mItemList.get(position);

        holder.mAmount.setText(item.amount);
        holder.mAbout.setText(item.about);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        holder.mDate.setText(dateFormat.format(item.date));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (mItemList != null ? mItemList.size() : 0);
    }
}
