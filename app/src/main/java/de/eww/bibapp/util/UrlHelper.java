package de.eww.bibapp.util;

import android.content.Context;

import de.eww.bibapp.R;

public class UrlHelper {
    public static String getInterlendingUrl(Context context, String ppn) {
        return String.format(context.getResources().getString(R.string.bibapp_interlanding_url), ppn);
    }

    public static String getLocationUrl(Context context, String format) {
        String[] locationUrls = context.getResources().getStringArray(R.array.bibapp_location_urls);
        int localCatalogIndex = PrefUtils.getLocalCatalogIndex(context);

        return String.format(locationUrls[localCatalogIndex], format);
    }

    public static String getPaiaUrl(Context context) {
        String[] paiaUrls = context.getResources().getStringArray(R.array.bibapp_paia_urls);
        int localCatalogIndex = PrefUtils.getLocalCatalogIndex(context);

        return paiaUrls[localCatalogIndex];
    }
}
