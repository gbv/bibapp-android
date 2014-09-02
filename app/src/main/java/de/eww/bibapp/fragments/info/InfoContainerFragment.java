package de.eww.bibapp.fragments.info;

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
 * Info Fragment class, providing a container for informative content and news feeds
 */
public class InfoContainerFragment extends AbstractContainerFragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_info_main, container, false);
		
		if ( this.fragments.isEmpty() )
		{
			this.switchContent(R.id.info_container, InfoFragment.class.getName(), "info_content", false);
		}
		
		return v;
	}
}