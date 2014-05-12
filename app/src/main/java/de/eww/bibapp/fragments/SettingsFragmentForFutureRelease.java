/*package de.eww.bibapp.fragments;

import de.eww.bibapp.R;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class SettingsFragmentForFutureRelease extends PreferenceFragment implements
	OnSharedPreferenceChangeListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        this.getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		// if storing login credentials is disabled, clean old credentials data
		boolean storeLogin = sharedPreferences.getBoolean("store_login", false);
		if ( storeLogin == false )
		{
			SharedPreferences settings = this.getActivity().getPreferences(0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("store_login_username", null);
			editor.putString("store_login_password", null);
			editor.commit();
		}
	}
}*/