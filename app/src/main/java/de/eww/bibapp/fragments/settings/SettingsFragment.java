package de.eww.bibapp.fragments.settings;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.R;
import de.eww.bibapp.constants.Constants;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * Settings Fragment class, manages user preferences configuration
 */
public class SettingsFragment extends Fragment implements
    View.OnClickListener,
	OnItemSelectedListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		this.setHasOptionsMenu(true);
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState)
	{
        super.onActivityCreated(savedInstanceState);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_settings_content, container, false);
		
		SharedPreferences settings = this.getActivity().getPreferences(0);
		
		// store login checkbox
		CheckBox checkbox = (CheckBox) v.findViewById(R.id.settings_save_checkbox);
		boolean isChecked = settings.getBoolean("store_login", false);

        checkbox.setOnClickListener(this);
        checkbox.setChecked(isChecked);
		
		// local catalog settings
		Spinner catalogSpinner = (Spinner) v.findViewById(R.id.settings_local_search_spinner);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item);
		
		for (int i=0; i < Constants.LOCAL_CATALOGS.length; i++) {
			String[] catalogEntry = Constants.LOCAL_CATALOGS[i];
			adapter.add(catalogEntry[1]);
			
		}
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		catalogSpinner.setAdapter(adapter);
		
		// select the correct item
		int spinnerValue = settings.getInt("local_catalog", Constants.LOCAL_CATALOG_DEFAULT);
		catalogSpinner.setSelection(spinnerValue);
		
		// set listener
		catalogSpinner.setOnItemSelectedListener(this);
		
		// data privacy
		if ( !Constants.DBS_COUNTING_URL.isEmpty() )
		{
			// show the layout
			LinearLayout dataPrivacyLayout = (LinearLayout) v.findViewById(R.id.settings_data_privacy_layout);
			dataPrivacyLayout.setVisibility(View.VISIBLE);
			
			// set the checkbox state
			CheckBox dbsCheckBox = (CheckBox) v.findViewById(R.id.settings_dbs_checkbox);
			boolean isDbsChecked = settings.getBoolean("allow_dbs", true);
			dbsCheckBox.setChecked(isDbsChecked);
			
			// set the change listener
            //dbsCheckBox.setOnClickListener(this);
		}
		
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
    public void onClick(View v)
	{
        boolean isChecked = ((CheckBox) v).isChecked();

		SharedPreferences settings = this.getActivity().getPreferences(0);
		SharedPreferences.Editor editor = settings.edit();
		
		if (v.getId() == R.id.settings_save_checkbox) {
			// store the login decision
			editor.putBoolean("store_login", isChecked);
			
			// if storing login credentials is disabled, clean old credentials data
			if (!isChecked) {
				editor.putString("store_login_username", null);
				editor.putString("store_login_password", null);
				
				// and remove stored login information
				PaiaHelper.reset();
			}
		} else {
			// store dbs decision
			editor.putBoolean("allow_dbs", isChecked);
		}
		
		editor.commit();
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		super.onPrepareOptionsMenu(menu);
		menu.clear();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		// take the position and store it as user preferences
		SharedPreferences settings = this.getActivity().getPreferences(0);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putInt("local_catalog", pos);
		
		editor.commit();
		
		PaiaHelper.reset();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// nothing to do here
	}
}