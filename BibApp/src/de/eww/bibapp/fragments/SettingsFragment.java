package de.eww.bibapp.fragments;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.R;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * Settings Fragment class, manages user preferences configuration
 */
public class SettingsFragment extends AbstractContainerFragment implements CompoundButton.OnCheckedChangeListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// enable option menu
		this.setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_settings_main, container, false);
		
		CheckBox checkbox = (CheckBox) v.findViewById(R.id.settings_save_checkbox);
		
		SharedPreferences settings = this.getActivity().getPreferences(0);
		boolean isChecked = settings.getBoolean("store_login", false);
		
		checkbox.setChecked(isChecked);
		
		checkbox.setOnCheckedChangeListener(this);
		
		// get the version number and set it in the layout
		try
		{
			PackageInfo packageInfo = this.getActivity().getPackageManager().getPackageInfo(this.getActivity().getPackageName(), 0);
			TextView versionView = (TextView) v.findViewById(R.id.settings_version_name);
			versionView.setText('v' + packageInfo.versionName);
		}
		catch (NameNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// set title
		ActionBar actionBar = this.getActivity().getActionBar();
		actionBar.setTitle(R.string.actionbar_settings);
		actionBar.setSubtitle(null);
		actionBar.setDisplayHomeAsUpEnabled(false);
		
		return v; 
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		SharedPreferences settings = this.getActivity().getPreferences(0);
		
		// store decision
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("store_login", isChecked);
		
		// if storing login credentials is disabled, clean old credentials data
		if ( !isChecked )
		{
			editor.putString("store_login_username", null);
			editor.putString("store_login_password", null);
			
			// and remove stored login information
			PaiaHelper.reset();
		}
		
		editor.commit();
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		super.onPrepareOptionsMenu(menu);
		menu.clear();
	}
}