package de.eww.bibapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
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
    private Context mContext;
    private boolean mIsRequestPermitted;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mAbout;
        public TextView mSignature;
        public TextView mDate;
        public CheckBox mCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);

            mAbout = (TextView) itemView.findViewById(R.id.about);
            mSignature = (TextView) itemView.findViewById(R.id.signature);
            mDate = (TextView) itemView.findViewById(R.id.date);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }

    // Suitable constructor for list type
    public BookedAdapter(List<PaiaItem> itemList, Context context, boolean isRequestPermitted) {
        mItemList = itemList;
        mContext = context;
        mIsRequestPermitted = isRequestPermitted;
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

        if (item.isCanCancel() && mIsRequestPermitted) {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (mItemList != null ? mItemList.size() : 0);
    }
}
