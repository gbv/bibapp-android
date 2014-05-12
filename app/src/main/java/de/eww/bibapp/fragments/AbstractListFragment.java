package de.eww.bibapp.fragments;

import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.animation.AnimationUtils;
import de.eww.bibapp.R;

abstract public class AbstractListFragment extends ListFragment
{
	protected boolean isListShown = true;
	protected int lastClickedPosition;
	
	public Object getItem(int position)
	{
		return this.getListAdapter().getItem(position);
	}
	
	public int getLastClickedPosition()
	{
		return this.lastClickedPosition;
	}
	
	@Override
	public void setListShown(boolean shown)
	{
		this.setListShown(shown, true);
	}
	
	@Override
	public void setListShownNoAnimation(boolean shown)
	{
		this.setListShown(shown, false);
	}
	
	public void setListShown(boolean shown, boolean animate)
	{
		if ( this.isListShown == shown)
		{
			return;
		}
		
		this.isListShown = shown;
		View v = this.getView();
		
		if ( v != null )
		{
			View progressContainer = v.findViewById(R.id.progressContainer);
			View listContainer = v.findViewById(android.R.id.list);
			
			if ( shown == true )
			{
				if ( animate == true )
				{
					progressContainer.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), android.R.anim.fade_out));
					listContainer.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), android.R.anim.fade_in));
				}
				
				progressContainer.setVisibility(View.GONE);
				listContainer.setVisibility(View.VISIBLE);
			}
			else
			{
				if ( animate == true )
				{
					progressContainer.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), android.R.anim.fade_in));
					listContainer.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), android.R.anim.fade_out));
				}
				
				progressContainer.setVisibility(View.VISIBLE);
				listContainer.setVisibility(View.INVISIBLE);
			}
		}
	}
}
