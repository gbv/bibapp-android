package de.eww.bibapp.fragment.account;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.eww.bibapp.fragments.DummyFragment;
import roboguice.fragment.RoboFragment;

/**
 * Created by christoph on 05.11.14.
 */
public class AccountFragment extends RoboFragment {

    private FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabhost);

        mTabHost.addTab(mTabHost.newTabSpec("dummy").setIndicator("Dummy"), DummyFragment.class, null);

        return mTabHost;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mTabHost = null;
    }
}
