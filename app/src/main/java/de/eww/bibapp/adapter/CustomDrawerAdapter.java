package de.eww.bibapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.eww.bibapp.model.DrawerItem;
import de.eww.bibapp.R;

/**
 * Created by christoph on 07.11.14.
 */
public class CustomDrawerAdapter extends ArrayAdapter<DrawerItem> {

    Context mContext;
    List<DrawerItem> mDrawerItemList;
    int mLayoutResID;

    public CustomDrawerAdapter(Context context, int layoutResourceID, List<DrawerItem> listItems) {
        super(context, layoutResourceID, listItems);
        mContext = context;
        mDrawerItemList = listItems;
        mLayoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DrawerItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            drawerHolder = new DrawerItemHolder();

            view = inflater.inflate(mLayoutResID, parent, false);
            drawerHolder.itemName = (TextView) view.findViewById(R.id.drawer_name);
            drawerHolder.icon = (ImageView) view.findViewById(R.id.drawer_icon);
            drawerHolder.itemHeading = (TextView) view.findViewById(R.id.drawer_heading);

            drawerHolder.headerLayout = (LinearLayout) view.findViewById(R.id.headerLayout);
            drawerHolder.itemLayout = (LinearLayout) view.findViewById(R.id.itemLayout);

            view.setTag(drawerHolder);
        } else {
            drawerHolder = (DrawerItemHolder) view.getTag();
        }

        DrawerItem dItem = mDrawerItemList.get(position);

        if (dItem.getItemHeading() != null) {
            drawerHolder.headerLayout.setVisibility(View.VISIBLE);
            drawerHolder.itemLayout.setVisibility(View.GONE);
            drawerHolder.itemHeading.setText(dItem.getItemHeading());
        } else {
            drawerHolder.headerLayout.setVisibility(View.GONE);
            drawerHolder.itemLayout.setVisibility(View.VISIBLE);
            drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(dItem.getImgResID()));
            drawerHolder.itemName.setText(dItem.getItemName());
        }

        return view;
    }

    private static class DrawerItemHolder {
        TextView itemName;
        TextView itemHeading;
        ImageView icon;
        LinearLayout headerLayout;
        LinearLayout itemLayout;
    }
}
