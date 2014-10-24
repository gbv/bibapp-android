//package de.eww.bibapp.fragments.settings;
//
//import android.app.ActionBar;
//import android.content.SharedPreferences;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager.NameNotFoundException;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.ArrayAdapter;
//import android.widget.CheckBox;
//import android.widget.LinearLayout;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import de.eww.bibapp.PaiaHelper;
//import de.eww.bibapp.R;
//import de.eww.bibapp.constants.Constants;
//
///**
//* @author Christoph Schönfeld - effective WEBWORK GmbH
//*
//* This file is part of the Android BibApp Project
//* =========================================================
//* Settings Fragment class, manages user preferences configuration
//*/
//public class SettingsFragment extends Fragment
//{
//	@Override
//	public void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//
//		this.setHasOptionsMenu(true);
//	}
//
//	@Override
//    public void onActivityCreated(Bundle savedInstanceState)
//	{
//        super.onActivityCreated(savedInstanceState);
//    }
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//	{
//		// inflate the layout for this fragment
//		View v = inflater.inflate(R.layout.fragment_settings_content, container, false);
//
//		SharedPreferences settings = this.getActivity().getPreferences(0);
//
//		// get the version number and set it in the layout
//		try
//		{
//			PackageInfo packageInfo = this.getActivity().getPackageManager().getPackageInfo(this.getActivity().getPackageName(), 0);
//			TextView versionView = (TextView) v.findViewById(R.id.settings_version_name);
//			versionView.setText('v' + packageInfo.versionName);
//		}
//		catch (NameNotFoundException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//
//		// set title
//		ActionBar actionBar = this.getActivity().getActionBar();
//		actionBar.setTitle(R.string.actionbar_settings);
//		actionBar.setSubtitle(null);
//		actionBar.setDisplayHomeAsUpEnabled(false);
//
//		return v;
//	}
//}