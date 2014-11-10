package de.eww.bibapp;

/**
 * Created by christoph on 07.11.14.
 */
public class DrawerItem {

    private String mItemName;
    private String mItemHeading;
    private int mImgResID;

    public DrawerItem(String itemName, int imgResID) {
        mItemName = itemName;
        mImgResID = imgResID;
    }

    public DrawerItem(String heading) {
        mItemHeading = heading;
    }

    public String getItemName() {
        return mItemName;
    }

    public void setItemName(String itemName) {
        mItemName = itemName;
    }

    public int getImgResID() {
        return mImgResID;
    }

    public void setImgResID(int imgResID) {
        mImgResID = imgResID;
    }

    public void setItemHeading(String heading) {
        mItemHeading = heading;
    }

    public String getItemHeading() {
        return mItemHeading;
    }
}
