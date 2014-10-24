//package de.eww.bibapp.fragments.detail;
//
//import android.app.ActionBar;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.ViewPager;
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//
//import de.eww.bibapp.MainActivity;
//import de.eww.bibapp.R;
//import de.eww.bibapp.adapters.DetailPagerAdapter;
//import de.eww.bibapp.fragments.AbstractContainerFragment;
//import de.eww.bibapp.fragments.AbstractListFragment;
//
//public class DetailPagerFragment extends Fragment
//{
//	private DetailPagerAdapter adapter;
//	private ViewPager viewPager;
//
//	public static AbstractListFragment listFragment;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//
//		// enable option menu
//		this.setHasOptionsMenu(true);
//
//		this.adapter = new DetailPagerAdapter(this.getChildFragmentManager());
//	}
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState)
//	{
//		super.onActivityCreated(savedInstanceState);
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//	{
//		// inflate the layout for this fragment
//		View v = inflater.inflate(R.layout.fragment_detail_pager, container, false);
//
//		this.viewPager = (ViewPager) v.findViewById(R.id.pager);
//		this.viewPager.setAdapter(this.adapter);
//
//		if ( viewPager.getCurrentItem() != DetailPagerFragment.listFragment.getLastClickedPosition() )
//		{
//			this.viewPager.setCurrentItem(DetailPagerFragment.listFragment.getLastClickedPosition());
//		}
//
//		ActionBar actionBar = MainActivity.instance.getActionBar();
//
//		// enable up navigation
//		actionBar.setDisplayHomeAsUpEnabled(true);
//
//		return v;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item)
//	{
//	    switch ( item.getItemId() )
//	    {
//	        case android.R.id.home:
//	        	// app icon in action bar clicked; go up
//	        	AbstractContainerFragment containerFragment = (AbstractContainerFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.currentTabId);
//	        	containerFragment.up();
//
//	            return true;
//
//	        default:
//	            return super.onOptionsItemSelected(item);
//	    }
//	}
//}
