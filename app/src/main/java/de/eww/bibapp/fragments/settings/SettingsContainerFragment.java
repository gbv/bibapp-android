package de.eww.bibapp.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.eww.bibapp.R;
import de.eww.bibapp.fragments.AbstractContainerFragment;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * Settings Fragment class, providing a container for the configuration options
 */
public class SettingsContainerFragment extends AbstractContainerFragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_settings_main, container, false);
		
		if ( this.fragments.isEmpty() )
		{
			this.switchContent(R.id.settings_container, SettingsFragment.class.getName(), "settings_container", false);
		}
		
		return v;
	}
}