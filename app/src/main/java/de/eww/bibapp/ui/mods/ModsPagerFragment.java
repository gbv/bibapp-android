package de.eww.bibapp.ui.mods;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.eww.bibapp.databinding.FragmentModsPagerBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModsPagerFragment extends Fragment {

    private FragmentModsPagerBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentModsPagerBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }








//    private FragmentStatePagerAdapter mPagerAdapter;
//    private ViewPager mViewPager;
//
//    private SearchManager searchManager;
//    private CompositeDisposable disposable = new CompositeDisposable();
//
//    private int mCurrentItem = 0;
//
//    private boolean mUseWatchlistSource = false;
//    private SearchManager.SEARCH_MODE mSearchMode;
//    private String mSearchString;
//
//    private SwipeLoadingDialogFragment mLoadingDialogFragment;
//
//    private boolean isLoadingMore = false;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        if (!mUseWatchlistSource) {
//            if (this.searchManager == null) {
//                this.searchManager = new SearchManager();
//
//                this.searchManager.setSearchQuery(mSearchString);
//                this.searchManager.setOffset(ModsSource.getLoadedItems(mSearchMode.toString()) + 1);
//                this.searchManager.setSearchMode(mSearchMode);
//            }
//
//            mPagerAdapter = new ModsPagerAdapter(this, getChildFragmentManager(), mSearchMode);
//        } else {
//            mPagerAdapter = new ModsWatchlistPagerAdapter(getChildFragmentManager());
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        this.disposable.dispose();
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//    }
//
//    public void setModsItem(int position) {
//        mCurrentItem = position;
//
//        if (mViewPager != null) {
//            mViewPager.setCurrentItem(position);
//        }
//    }
//
//    public int getCurrentItemPosition() {
//        return mViewPager.getCurrentItem();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_mods_pager, container, false);
//
//        mViewPager = view.findViewById(R.id.pager);
//        mViewPager.setAdapter(mPagerAdapter);
//
//        mViewPager.setCurrentItem(mCurrentItem);
//
//        return view;
//    }
//
//    public void useWatchlistSource() {
//        mUseWatchlistSource = true;
//    }
//
//    public void setSearchMode(SearchManager.SEARCH_MODE searchMode) {
//        mSearchMode = searchMode;
//    }
//
//    public void setSearchString(String searchString) {
//        mSearchString = searchString;
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 99) {
//            if (resultCode == getActivity().RESULT_OK) {
//                // Set navigation position
//                int navigationPosition = data.getIntExtra("navigationIndex", 0);
//                ((BaseActivity) getActivity()).selectItem(navigationPosition);
//            }
//        }
//    }
//
//    public void onLoadMore() {
//        if (this.isLoadingMore == false) {
//            // Create a dialog
//            mLoadingDialogFragment = new SwipeLoadingDialogFragment();
//            mLoadingDialogFragment.show(getChildFragmentManager(), "swipe_dialog");
//
//            this.searchManager.getSearchResults(
//                    this.disposable,
//                    this,
//                    getContext()
//            );
//
//            this.isLoadingMore = true;
//        }
//    }
//
//    @Override
//    public void onSearchRequestDone(SruResult sruResult)
//    {
//        // Add data
//        List<ModsItem> modsItems = (List<ModsItem>) sruResult.getResult().get("list");
//        ModsSource.addModsItems(mSearchMode.toString(), modsItems);
//
//        // Dismiss dialog
//        mLoadingDialogFragment.dismiss();
//
//        this.isLoadingMore = false;
//    }
//
//    @Override
//    public void onSearchRequestFailed()
//    {
//        Toast toast = Toast.makeText(getActivity(), R.string.toast_search_error, Toast.LENGTH_LONG);
//        toast.show();
//    }




















//
//    /**
//     * Activity that displays a particular location onscreen.
//     *
//     * This activity is started only when the screen in not large enough for a two-pane layout, in
//     * which case this separate activity is shown in order to display the location. This activity
//     * kills itself if the display is reconfigured into a shape that allows a two-pane layout, since
//     * in that case the location article will be displayed by the {@link de.eww.bibapp.activity.LocationsActivity}
//     * and this Activity becomes unnecessary.
//     */
//    public class ModsActivity extends BaseActivity implements
//            AsyncCanceledInterface {
//
//        // The mods item index we are to display
//        int mModsItemIndex;
//
//        de.eww.bibapp.fragment.search.ModsPagerFragment mModsPagerFragment;
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_mods);
////
////        mModsItemIndex = getIntent().getExtras().getInt("modsItemIndex", 0);
////
////        // Place a ModsFragment in our container
////        mModsPagerFragment = new ModsPagerFragment();
////        getSupportFragmentManager().beginTransaction().add(R.id.container, mModsPagerFragment).commit();
////
////        boolean isFromWatchlist = false;
////        if (getIntent().hasExtra("modsItemSource")) {
////            String modsItemSource = getIntent().getExtras().getString("modsItemSource");
////            if (modsItemSource.equals(WatchlistSource.class.getName())) {
////                mModsPagerFragment.useWatchlistSource();
////                isFromWatchlist = true;
////            }
////        }
////
////        if (getIntent().hasExtra("searchMode")) {
////            SearchManager.SEARCH_MODE searchMode = (SearchManager.SEARCH_MODE) getIntent().getSerializableExtra("searchMode");
////            mModsPagerFragment.setSearchMode(searchMode);
////        }
////
////        if (getIntent().hasExtra("searchString")) {
////            String searchString = getIntent().getExtras().getString("searchString");
////            mModsPagerFragment.setSearchString(searchString);
////        }
////
////        setActiveNavigationItem(isFromWatchlist ? 2 : 0);
////
////        // Display the correct mods item on the fragment
////        mModsPagerFragment.setModsItem(mModsItemIndex);
//        }
//
//        @Override
//        public void onBackPressed() {
//            Intent intent = new Intent();
//            intent.putExtra("pagerItemPosition", mModsPagerFragment.getCurrentItemPosition());
//            setResult(RESULT_OK, intent);
//
//            super.onBackPressed();
//        }
//
//        @Override
//        public void onAsyncCanceled() {
//            Toast toast = Toast.makeText(this, R.string.toast_account_error, Toast.LENGTH_LONG);
//            toast.show();
//        }
}