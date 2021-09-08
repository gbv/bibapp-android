package de.eww.bibapp.ui;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.R;
import de.eww.bibapp.activity.BaseActivity;
import de.eww.bibapp.util.LocaleManager;
import de.eww.bibapp.util.PrefUtils;
import de.eww.bibapp.viewmodel.AccountViewModel;
import de.eww.bibapp.viewmodel.AccountViewModelFactory;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Get the local catalog list preference and check if we need to present it
        // to the user, which is only the case, if there is more than one local
        // catalog pre-configured
        ListPreference localCatalogPreference = findPreference(PrefUtils.PREF_LOCAL_CATALOG);

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

        // Get the version name and set it
        try {
            PackageInfo packageInfo = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), 0);
            EditTextPreference versionPreference = (EditTextPreference) findPreference("pref_version");
            versionPreference.setSummary("v" + packageInfo.versionName);
        } catch(PackageManager.NameNotFoundException e) {
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
            CheckBoxPreference storeLoginPref = findPreference(key);
            if (!storeLoginPref.isChecked()) {
                cleanStoredCredentials();
            }
        }

        if (key.equals(PrefUtils.PREF_LOCAL_CATALOG)) {
            // reset login
            cleanStoredCredentials();
        }

        if (key.equals(PrefUtils.PREF_USER_LANGUAGE)) {
            ListPreference languagePref = this.findPreference(key);
            String languagePrefValue = languagePref.getValue();
            if (!languagePrefValue.equals("device")) {
                LocaleManager.changeLanguage(getActivity(), languagePrefValue);
            }
        }
    }

    private void cleanStoredCredentials() {
        PrefUtils.unsetStoredCredentials(requireContext());

        AccountViewModel accountViewModel = new ViewModelProvider(requireActivity(), new AccountViewModelFactory(requireActivity().getApplication())).get(AccountViewModel.class);
        accountViewModel.logout();
    }
}
