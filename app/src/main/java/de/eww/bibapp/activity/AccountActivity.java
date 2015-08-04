package de.eww.bibapp.activity;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.R;
import de.eww.bibapp.fragment.account.AccountBookedFragment;
import de.eww.bibapp.fragment.account.AccountBorrowedFragment;
import de.eww.bibapp.fragment.account.AccountFeesFragment;
import de.eww.bibapp.tasks.paia.PaiaPatronTask;
import de.eww.bibapp.view.SlidingTabLayout;

public class AccountActivity extends BaseActivity implements
        PaiaHelper.PaiaListener,
        AsyncCanceledInterface {

    /**
     * This class represents a tab to be displayed by {@link ViewPager} and it's associated
     * {@link SlidingTabLayout}
     */
    private static class AccountPagerItem {

        private final CharSequence mTitle;
        private final int mIndicatorColor;
        private final int mDividerColor;

        public AccountPagerItem(CharSequence title, int indicatorColor, int dividerColor) {
            mTitle = title;
            mIndicatorColor = indicatorColor;
            mDividerColor = dividerColor;
        }

        /**
         * @param position The tab position
         *
         * @return A new {@link Fragment} to be displayed by a {@link ViewPager}
         */
        public Fragment createFragment(int position) {
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
         * @return The title which represents this tab.
         */
        public CharSequence getTitle() {
            return mTitle;
        }

        /**
         * @return The color to be used for indicator on the {@link SlidingTabLayout}
         */
        public int getIndicatorColor() {
            return mIndicatorColor;
        }

        /**
         * @return The color to be used for right divider on the {@link SlidingTabLayout}
         */
        public int getDividerColor() {
            return mDividerColor;
        }
    }

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

    /**
     * List of {@link AccountPagerItem} which represents the tabs
     */
    private List<AccountPagerItem> mTabs = new ArrayList<AccountPagerItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Determine whether we are in single-pane or dual-pane mode by testing the visibility
        // of the container view
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mIsDualPane = mViewPager == null || mViewPager.getVisibility() != View.VISIBLE;

        PaiaHelper.getInstance().ensureConnection(this, this, this);
    }

    private void addSlidingTabs() {
        Resources resources = getResources();

        // Populate our tab list with tabs. Each item contains a title, indicator color and divider
        // color, which are used by {@link SlidingTabLayout}.
        mTabs.add(new AccountPagerItem(
                getString(R.string.account_borrowed),
                resources.getColor(R.color.colorHighlight),
                Color.GRAY
        ));

        mTabs.add(new AccountPagerItem(
                getString(R.string.account_booked),
                resources.getColor(R.color.colorHighlight),
                Color.GRAY
        ));

        mTabs.add(new AccountPagerItem(
                getString(R.string.account_fees),
                resources.getColor(R.color.colorHighlight),
                Color.GRAY
        ));

        // Set the ViewPagers's PagerAdapter so that it can display items
        mViewPager.setAdapter(new AccountPagerAdapter(getSupportFragmentManager()));

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        // Set a TabColorizer to customize the indicator and divider colors. Here we just retrieve
        // the tab at the position, and return it's set color
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                return mTabs.get(position).getDividerColor();
            }
        });
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
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mTabs.get(position).createFragment(position);
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
            return mTabs.get(position).getTitle();
        }
    }

    private void addFragments() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.container, Fragment.instantiate(this, AccountBorrowedFragment.class.getName()));
        transaction.add(R.id.container, Fragment.instantiate(this, AccountBookedFragment.class.getName()));
        transaction.add(R.id.container, Fragment.instantiate(this, AccountFeesFragment.class.getName()));

        transaction.commit();
    }

    public void onPatronLoaded(JSONObject response) {
        // Set action bar sub title
        ActionBar actionBar = ((BaseActivity) this).getSupportActionBar();

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

    @Override
    public void onPaiaConnected() {
        if (!mIsDualPane) {
            addSlidingTabs();
        } else {
            addFragments();
        }

        // Perform a paia request to get the users name, if we have the scope to do this
        if (PaiaHelper.getInstance().hasScope(PaiaHelper.SCOPES.READ_PATRON)) {
            AsyncTask<String, Void, JSONObject> paiaPatronTask = new PaiaPatronTask(this, this);
            paiaPatronTask.execute(PaiaHelper.getInstance().getAccessToken(), PaiaHelper.getInstance().getUsername());
        }
    }

    @Override
    public void onAsyncCanceled() {
        Toast toast = Toast.makeText(this, R.string.toast_account_error, Toast.LENGTH_LONG);
        toast.show();
    }
}
