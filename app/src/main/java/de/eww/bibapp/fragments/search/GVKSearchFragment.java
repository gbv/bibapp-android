//public class GVKSearchFragment extends AbstractListFragment implements
//	LoaderManager.LoaderCallbacks<HashMap<String, Object>>,
//	OnEditorActionListener,
//	OnScrollListener,
//	AsyncCanceledInterface,
//	SearchAdapterInterface
//{
//	// This is the Adapter being used to display the list's data.
//    SearchAdapter mAdapter;
//
//    private boolean isExpandLoading = false;
//    private int hits = 0;
//    private ArrayList<SearchEntry> previousResults = new ArrayList<SearchEntry>();
//
//	@Override
//    public void onActivityCreated(Bundle savedInstanceState)
//	{
//        super.onActivityCreated(savedInstanceState);
//
//        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
//        getLoaderManager().initLoader(0, null, this);
//
//        this.mAdapter = new SearchAdapter(getActivity(), R.layout.item_mods_view);
//
//        this.setListAdapter(mAdapter);
//
//        this.getListView().setOnScrollListener(this);
//    }
//
//	@Override
//	public void onListItemClick(ListView l, View v, int position, long id)
//	{
//		this.lastClickedPosition = position;
//		DetailPagerFragment.listFragment = this;
//
//		if ( !MainActivity.isPadVersion )
//		{
//			SearchContainerFragment containerFragment = (SearchContainerFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag("search");
//	    	containerFragment.switchContent(R.id.search_container, DetailPagerFragment.class.getName(), "search_pager", true);
//		}
//		else
//		{
//			DetailFragment detailFragment = (DetailFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag("large_detail");
//			detailFragment.setSearchEntry(this.mAdapter.getItem(position));
//		}
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//	{
//		// inflate the layout for this fragment
//		View v = inflater.inflate(R.layout.fragment_local_search, container, false);
//
//		EditText searchText = (EditText) v.findViewById(R.id.search_local_query);
//		searchText.setOnEditorActionListener(this);
//
//		this.setHits(0);
//
//		return v;
//	}
//
//	@Override
//	public Loader<HashMap<String, Object>> onCreateLoader(int arg0, Bundle arg1)
//	{
//		Loader<HashMap<String, Object>> loader = new SearchXmlLoader(getActivity().getApplicationContext(), this);
//		((SearchXmlLoader) loader).setIsLocalSearch(false);
//
//		return loader;
//	}
//
//	@Override
//	public ArrayList<SearchEntry> getResults()
//	{
//		return this.previousResults;
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public void onLoadFinished(Loader<HashMap<String, Object>> loader, HashMap<String, Object> data)
//	{
//		Collection<SearchEntry> searchResults = (Collection<SearchEntry>) data.get("list");
//		int newOffset = ((SearchXmlLoader) loader).getOffset();
//
//		if ( (newOffset - Constants.SEARCH_HITS_PER_REQUEST > this.previousResults.size()) )
//		{
//			this.previousResults.addAll(searchResults);
//		}
//
//		this.mAdapter.clear();
//		this.mAdapter.addAll(this.previousResults);
//		this.mAdapter.notifyDataSetChanged();
//		this.setListShown(true);
//
//		if ( this.isExpandLoading )
//		{
//			ProgressBar smallProgressBar = (ProgressBar) this.getView().findViewById(R.id.search_local_progressExtend);
//			smallProgressBar.setVisibility(View.GONE);
//
//			if ( newOffset - Constants.SEARCH_HITS_PER_REQUEST - 2 > 0)
//			{
//				this.setSelection(newOffset - Constants.SEARCH_HITS_PER_REQUEST - 2);
//			}
//		}
//
//		this.isExpandLoading = false;
//
//		// if we are on the pad version and this is the first search result, display the first entry in detail view
//		if ( MainActivity.isPadVersion && this.hits == 0 && !this.mAdapter.isEmpty() )
//		{
//			DetailFragment detailFragment = (DetailFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag("large_detail");
//			detailFragment.setSearchEntry(this.mAdapter.getItem(0));
//		}
//
//		// show the number of results in action bar
//		this.hits = (Integer) data.get("numberOfRecords");
//		this.setHits(this.hits);
//	}
//
//	private void setHits(int hits)
//	{
//		ActionBar actionBar = this.getActivity().getActionBar();
//		Resources resources = this.getActivity().getResources();
//
//		actionBar.setSubtitle(String.valueOf(hits) + " " + resources.getString(R.string.search_hits));
//	}
//
//	@Override
//	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
//	{
//		if ( actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT )
//		{
//			this.setListShown(false);
//			String searchText = v.getText().toString();
//
//			this.mAdapter.clear();
//
//			Loader<HashMap<String, Object>> loader = this.getLoaderManager().getLoader(0);
//			SearchXmlLoader searchXmlLoader = (SearchXmlLoader) loader;
//
//			searchXmlLoader.setSearchString(searchText);
//			searchXmlLoader.resetOffset();
//			searchXmlLoader.forceLoad();
//			this.previousResults.clear();
//
//			// force soft keyboard to hide
//			InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//		}
//
//		return false;
//	}
//
//	@Override
//	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
//	{
//		if ( totalItemCount > visibleItemCount & !this.isExpandLoading && totalItemCount > 0 && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 0) && totalItemCount < this.hits )
//		{
//			if ( this.isVisible() )
//			{
//				this.isExpandLoading = true;
//
//				ProgressBar smallProgressBar = (ProgressBar) this.getView().findViewById(R.id.search_local_progressExtend);
//				smallProgressBar.setVisibility(View.VISIBLE);
//
//				Loader<HashMap<String, Object>> loader = this.getLoaderManager().getLoader(0);
//				SearchXmlLoader searchXmlLoader = (SearchXmlLoader) loader;
//
//				searchXmlLoader.forceLoad();
//			}
//		}
//	}
//}