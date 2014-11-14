package de.eww.bibapp.fragment.account;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONObject;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.R;
import de.eww.bibapp.activity.MainActivity;
import de.eww.bibapp.tasks.paia.PaiaPatronTask;
import de.eww.bibapp.view.SlidingTabLayout;
import roboguice.fragment.RoboFragment;

/**
 * Created by christoph on 05.11.14.
 */
public class AccountFragment extends RoboFragment implements
    PaiaHelper.PaiaListener,
    AsyncCanceledInterface {

    // Whether or not we are in dual-pane mode
    boolean mIsDualPane = false;

    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    ViewPager mViewPager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Determine whether we are in single-pane or dual-pane mode by testing the visibility
        // of the container view
        mViewPager = (ViewPager) getView().findViewById(R.id.viewpager);
        mIsDualPane = mViewPager == null || mViewPager.getVisibility() != View.VISIBLE;

        PaiaHelper.getInstance().ensureConnection(this);
    }

    /**
     * Inflates the {@link View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    private void addSlidingTabs() {
        // Set the ViewPagers's PagerAdapter so that it can display items
        mViewPager.setAdapter(new AccountPagerAdapter(getChildFragmentManager()));

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) getView().findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setSelectedIndicatorColors(R.color.colorAccent);
    }

    /**
     * The {@link android.support.v4.app.FragmentPagerAdapter} used to display pages in this sample.
     * The individual pages are simple and just display two lines of text. The important section of
     * this class is the {@link #getPageTitle(int)} method which controls what is displayed in the
     * {@link SlidingTabLayout}.
     */
    class AccountPagerAdapter extends FragmentPagerAdapter {

        public AccountPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 2:
                    return new AccountFeesFragment();
                case 1:
                    return new AccountBookedFragment();
                default:
                    return new AccountBorrowedFragment();
            }
        }

        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            Resources resources = getResources();

            switch (position) {
                case 2:
                    return resources.getString(R.string.account_fees);
                case 1:
                    return resources.getString(R.string.account_booked);
                default:
                    return resources.getString(R.string.account_borrowed);
            }
        }
    }

    private void addFragments() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        transaction.add(R.id.container, Fragment.instantiate(getActivity(), AccountBorrowedFragment.class.getName()));
        transaction.add(R.id.container, Fragment.instantiate(getActivity(), AccountBookedFragment.class.getName()));
        transaction.add(R.id.container, Fragment.instantiate(getActivity(), AccountFeesFragment.class.getName()));

        transaction.commit();
    }

	public void onPatronLoaded(JSONObject response) {
        // Check if the fragment is still added to its activity
        if (isAdded()) {
            // Set action bar sub title
            ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();

            try {
                String name = response.getString("name");

                if (response.has("status")) {
                    int status = response.getInt("status");

                    if (status > 0) {
                        name += " " + getResources().getText(R.string.account_inactive);
                    }
                }

                actionBar.setSubtitle(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

    @Override
    public void onPaiaConnected() {
        if (!mIsDualPane) {
            addSlidingTabs();
        } else {
            addFragments();
        }

        // Perform a paia request to get the users name, if we have the scope to do this
        if (PaiaHelper.getInstance().hasScope(PaiaHelper.SCOPES.READ_PATRON)) {
            AsyncTask<String, Void, JSONObject> paiaPatronTask = new PaiaPatronTask(this);
            paiaPatronTask.execute(PaiaHelper.getInstance().getAccessToken(), PaiaHelper.getInstance().getUsername());
        }
    }

	@Override
	public void onAsyncCanceled() {
        Toast toast = Toast.makeText(getActivity(), R.string.toast_account_error, Toast.LENGTH_LONG);
        toast.show();
	}
}
