package de.eww.bibapp.fragment.search;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.activity.BaseActivity;
import de.eww.bibapp.adapter.ModsPagerAdapter;
import de.eww.bibapp.adapter.ModsWatchlistPagerAdapter;
import de.eww.bibapp.fragment.dialog.SwipeLoadingDialogFragment;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.model.source.ModsSource;
import de.eww.bibapp.network.model.SruResult;
import de.eww.bibapp.network.search.SearchManager;
import de.eww.bibapp.util.SruHelper;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by christoph on 09.11.14.
 */
public class ModsPagerFragment extends Fragment implements
        SearchManager.SearchLoaderInterface {

    private FragmentStatePagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private SearchManager searchManager;
    private CompositeDisposable disposable = new CompositeDisposable();

    private int mCurrentItem = 0;

    private boolean mUseWatchlistSource = false;
    private SearchManager.SEARCH_MODE mSearchMode;
    private String mSearchString;

    private SwipeLoadingDialogFragment mLoadingDialogFragment;

    private boolean isLoadingMore = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.searchManager == null) {
            this.searchManager = new SearchManager();

            this.searchManager.setSearchQuery(mSearchString);
            this.searchManager.setOffset(ModsSource.getLoadedItems(mSearchMode.toString()) + 1);
            this.searchManager.setSearchMode(mSearchMode);
        }

        if (!mUseWatchlistSource) {
            mPagerAdapter = new ModsPagerAdapter(this, getChildFragmentManager(), mSearchMode);
        } else {
            mPagerAdapter = new ModsWatchlistPagerAdapter(getChildFragmentManager());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.disposable.dispose();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setModsItem(int position) {
        mCurrentItem = position;

        if (mViewPager != null) {
            mViewPager.setCurrentItem(position);
        }
    }

    public int getCurrentItemPosition() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mods_pager, container, false);

        mViewPager = view.findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.setCurrentItem(mCurrentItem);

        return view;
    }

    public void useWatchlistSource() {
        mUseWatchlistSource = true;
    }

    public void setSearchMode(SearchManager.SEARCH_MODE searchMode) {
        mSearchMode = searchMode;
    }

    public void setSearchString(String searchString) {
        mSearchString = searchString;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 99) {
            if (resultCode == getActivity().RESULT_OK) {
                // Set navigation position
                int navigationPosition = data.getIntExtra("navigationIndex", 0);
                ((BaseActivity) getActivity()).selectItem(navigationPosition);
            }
        }
    }

    public void onLoadMore() {
        if (this.isLoadingMore == false) {
            // Create a dialog
            mLoadingDialogFragment = new SwipeLoadingDialogFragment();
            mLoadingDialogFragment.show(getChildFragmentManager(), "swipe_dialog");

            this.searchManager.getSearchResults(
                    this.disposable,
                    this,
                    getContext()
            );

            this.isLoadingMore = true;
        }
    }

    @Override
    public void onSearchRequestDone(SruResult sruResult)
    {
        // Add data
        List<ModsItem> modsItems = (List<ModsItem>) sruResult.getResult().get("list");
        ModsSource.addModsItems(mSearchMode.toString(), modsItems);

        // Dismiss dialog
        mLoadingDialogFragment.dismiss();

        this.isLoadingMore = false;
    }

    @Override
    public void onSearchRequestFailed()
    {
        Toast toast = Toast.makeText(getActivity(), R.string.toast_search_error, Toast.LENGTH_LONG);
        toast.show();
    }
}