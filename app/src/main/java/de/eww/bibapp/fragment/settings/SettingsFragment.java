package de.eww.bibapp.fragment.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceManager;

import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.R;
import de.eww.bibapp.activity.BaseActivity;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.util.PrefUtils;
import roboguice.fragment.provided.RoboPreferenceFragment;

public class SettingsFragment extends RoboPreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        // Get the local catalog list preference and check if we need to present it
        // to the user, which is only the case, if there is more than one local
        // catalog pre-configured
        ListPreference localCatalogPreference = (ListPreference) findPreference(PrefUtils.PREF_LOCAL_CATALOG);

        if (Constants.LOCAL_CATALOGS.length > 1) {
            // Set entries and values for the local catalog list
            CharSequence[] localCatalogEntries = new CharSequence[Constants.LOCAL_CATALOGS.length];
            CharSequence[] localCatalogValues = new CharSequence[Constants.LOCAL_CATALOGS.length];
            int index = 0;
            for (String[] localCatalog : Constants.LOCAL_CATALOGS) {
                localCatalogEntries[index] = localCatalog[1];
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
        if (Constants.DBS_COUNTING_URL.isEmpty()) {
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
