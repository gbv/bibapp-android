//
//public class SearchFragment extends AbstractContainerFragment implements
//	SearchAdapterInterface
//{
//	private CustomFragmentTabHost mTabHost;
//	private DetailFragment padDetailFragment;
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//	{
//		// inflate the layout for this fragment
//		View view = inflater.inflate(R.layout.fragment_search_content, container, false);
//
//		this.mTabHost = (CustomFragmentTabHost) view.findViewById(R.id.search_tabhost);
//	    this.mTabHost.setup(getActivity(), this.getChildFragmentManager(), R.id.search_realtabcontent);
//
//	    Resources resources = this.getResources();
//	    String localTitle = resources.getString(R.string.search_local);
//
//	    // if our current local catalog contains a short title, we append it to the basic local tab title
//	    SharedPreferences settings = this.getActivity().getPreferences(0);
//		int spinnerValue = settings.getInt("local_catalog", Constants.LOCAL_CATALOG_DEFAULT);
//		if (Constants.LOCAL_CATALOGS[spinnerValue].length > 2) {
//			localTitle += " " + Constants.LOCAL_CATALOGS[spinnerValue][2];
//		}
/*
SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(fragment.getActivity());
        String localCatalogPreference = sharedPreferences.getString(SettingsActivity.KEY_PREF_LOCAL_CATALOG, "");
        int localCatalogIndex = 0;
        if (!localCatalogPreference.isEmpty()) {
            localCatalogIndex = Integer.valueOf(localCatalogPreference);
        }
 */
//
//	    this.addTab(LocalSearchFragment.class, "local", localTitle);
//	    this.addTab(GVKSearchFragment.class, "gvk", resources.getString(R.string.search_gvk));
//
//
//	    // set title
//  		ActionBar actionBar = this.getActivity().getActionBar();
//  		actionBar.setTitle(R.string.actionbar_search);
//  		actionBar.setSubtitle(null);
//		actionBar.setDisplayHomeAsUpEnabled(false);
//
//		return view;
//	}
//}