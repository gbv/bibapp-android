package de.eww.bibapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.iconics.IconicsDrawable;

import java.util.Iterator;
import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.util.ModsHelper;

public class ModsAdapter extends RecyclerView.Adapter<ModsAdapter.ViewHolder> {

    private Context mContext;

    private List<ModsItem> mItemList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitle;
        public TextView mSub;
        public TextView mAuthor;
        public TextView mPublication;
        public ImageView mImage;

        public ViewHolder(View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.title);
            mSub = itemView.findViewById(R.id.sub);
            mAuthor = itemView.findViewById(R.id.author);
            mPublication = itemView.findViewById(R.id.publication);
            mImage = itemView.findViewById(R.id.image);
        }
    }

    // Suitable constructor for list type
    public ModsAdapter(List<ModsItem> itemList, Context context) {
        mItemList = itemList;
        mContext = context;
    }

    public void addModsItems(List<ModsItem> itemList) {
        mItemList.addAll(itemList);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mods_view, parent, false);

        // Set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ModsItem item = mItemList.get(position);

        if (item != null) {
            holder.mTitle.setText(item.title);

            String subTitle = item.subTitle;
            if (!item.partName.isEmpty()) {
                subTitle = item.partName + "; " + subTitle;
            }
            if (!item.partNumber.isEmpty()) {
                subTitle = item.partNumber + "; " + subTitle;
            }
            holder.mSub.setText(subTitle);

            String authorString = "";
            Iterator<String> it = item.authors.iterator();
            while (it.hasNext()) {
                String author = it.next();
                authorString += author;

                if (it.hasNext()) {
                    authorString += ", ";
                }
            }
            holder.mAuthor.setText(authorString);

            if (item.issuedDate != null) {
                holder.mPublication.setText(item.issuedDate);
            }

            holder.mImage.setImageDrawable(new IconicsDrawable(this.mContext)
                .icon(ModsHelper.getBeluginoFontIcon(item))
                .color(Color.LTGRAY)
                .sizeDp(36));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (mItemList != null ? mItemList.size() : 0);
    }
}