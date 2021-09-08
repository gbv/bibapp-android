package de.eww.bibapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Locale;

import de.eww.bibapp.R;

/**
 * Created by christoph on 25.08.15.
 */
public class PrefUtils {

    public static final String PREF_LOCAL_CATALOG = "pref_localCatalog";
    public static final String PREF_STORE_LOGIN = "pref_storeLogin";
    public static final String PREF_STORE_LOGIN_USERNAME = "pref_storeLoginUsername";
    public static final String PREF_STORE_LOGIN_PASSWORD = "pref_storeLoginPassword";
    public static final String PREF_DATA_PRIVACY = "pref_dataPrivacy";
    public static final String PREF_USER_LANGUAGE = "pref_userLanguage";

    public static void init(final Context context) {
        // Set default values for our preferences
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
    }

    public static int getLocalCatalogIndex(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String localCatalogPreference = sp.getString(PREF_LOCAL_CATALOG, "");

        int localCatalogIndex = 0;
        if (!localCatalogPreference.isEmpty()) {
            localCatalogIndex = Integer.valueOf(localCatalogPreference);
        }

        return localCatalogIndex;
    }

    public static boolean isDbsChecked(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_DATA_PRIVACY, true);
    }

    public static boolean isLoginStored(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_STORE_LOGIN, false);
    }

    public static void setLoginStored(final Context context, final boolean loginStored) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_STORE_LOGIN, loginStored).commit();
    }

    public static String getStoredUsername(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_STORE_LOGIN_USERNAME, null);
    }

    public static void setStoredUsername(final Context context, final String username) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_STORE_LOGIN_USERNAME, username).commit();
    }

    public static String getStoredPassword(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_STORE_LOGIN_PASSWORD, null);
    }

    public static void setStoredPassword(final Context context, final String password) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_STORE_LOGIN_PASSWORD, password).commit();
    }

    public static void unsetStoredCredentials(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(PREF_STORE_LOGIN_USERNAME, null);
        editor.putString(PREF_STORE_LOGIN_PASSWORD, null);

        editor.commit();
    }

    public static String getUserLanguage(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_USER_LANGUAGE, "device");
    }

    public void setUserLanguage(final Context context, final Locale locale) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_USER_LANGUAGE, locale.toString());
    }
}
