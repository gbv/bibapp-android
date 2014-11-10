//public class SettingsFragment extends Fragment
//{

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
//
//		return v;
//	}
//}