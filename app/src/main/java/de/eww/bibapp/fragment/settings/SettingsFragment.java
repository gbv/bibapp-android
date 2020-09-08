package de.eww.bibapp.fragment.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.R;
import de.eww.bibapp.activity.BaseActivity;
import de.eww.bibapp.util.LocaleManager;
import de.eww.bibapp.util.PrefUtils;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        // Get the local catalog list preference and check if we need to present it
        // to the user, which is only the case, if there is more than one local
        // catalog pre-configured
        ListPreference localCatalogPreference = (ListPreference) findPreference(PrefUtils.PREF_LOCAL_CATALOG);

        String[] localCatalogs = getResources().getStringArray(R.array.bibapp_local_catalogs);

        if (localCatalogs.length > 1) {
            // Set entries and values for the local catalog list
            CharSequence[] localCatalogEntries = new CharSequence[localCatalogs.length];
            CharSequence[] localCatalogValues = new CharSequence[localCatalogs.length];
            int index = 0;
            for (String localCatalog : localCatalogs) {
                localCatalogEntries[index] = localCatalog;
                localCatalogValues[index] = Integer.toString(index);
                index++;
            }

            localCatalogPreference.setEntries(localCatalogEntries);
            localCatalogPreference.setEntryValues(localCatalogValues);
        } else {
            // Remove the local catalog list preference
            getPreferenceScreen().removePreference(localCatalogPreference);
        }

        // Check if we need to present the dbs privacy checkbox preference
        // to the user, which is only the case, if there is an url pre-configured
        if (getResources().getString(R.string.bibapp_tracking_url).isEmpty()) {
            getPreferenceScreen().removePreference(findPreference(PrefUtils.PREF_DATA_PRIVACY));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PrefUtils.PREF_STORE_LOGIN)) {
            // If storing login credentials is disabled, clean old credentials data
            CheckBoxPreference storeLoginPref = (CheckBoxPreference) findPreference(key);
            if (!storeLoginPref.isChecked()) {
                cleanStoredCredentials();
            }
        }

        if (key.equals(PrefUtils.PREF_LOCAL_CATALOG)) {
            // reset login
            cleanStoredCredentials();
        }

        if (key.equals(PrefUtils.PREF_USER_LANGUAGE)) {
            ListPreference languagePref = (ListPreference) this.findPreference(key);
            String languagePrefValue = languagePref.getValue();
            if (!languagePrefValue.equals("device")) {
                LocaleManager.changeLanguage(getActivity(), languagePrefValue);
            }
        }
    }

    private void cleanStoredCredentials() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("store_login_username", null);
        editor.putString("store_login_password", null);

        editor.commit();

        // and remove stored login information
        PaiaHelper.getInstance().reset();

        BaseActivity instance = BaseActivity.instance;
        if (instance != null) {
            instance.selectSearch();
        }
    }
}
