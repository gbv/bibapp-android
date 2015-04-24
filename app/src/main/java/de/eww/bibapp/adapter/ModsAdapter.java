package de.eww.bibapp.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import de.eww.bibapp.R;
import de.eww.bibapp.model.ModsItem;

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
        public ImageView mImage;

        public ViewHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.title);
            mSub = (TextView) itemView.findViewById(R.id.sub);
            mAuthor = (TextView) itemView.findViewById(R.id.author);
            mImage = (ImageView) itemView.findViewById(R.id.image);
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

            Resources res = mContext.getResources();
            holder.mImage.setImageResource(res.getIdentifier("mediaicon_" + item.mediaType.toLowerCase(Locale.GERMANY), "drawable", mContext.getPackageName()));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (mItemList != null ? mItemList.size() : 0);
    }
}