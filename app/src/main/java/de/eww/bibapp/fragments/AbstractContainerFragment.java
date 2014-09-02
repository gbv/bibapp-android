package de.eww.bibapp.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Stack;

abstract public class AbstractContainerFragment extends Fragment
{
	protected HashMap<String, Fragment> fragments = new HashMap<String, Fragment>();
	private Stack<Fragment> fragmentStack = new Stack<Fragment>();
	
	public Fragment getFragment(String tag)
	{
		return this.fragments.get(tag);
	}
	
	public void switchContent(int container, String className, String tag, boolean addToBackStack)
	{
		FragmentTransaction transaction = this.getChildFragmentManager().beginTransaction();
		
		if ( !this.fragmentStack.isEmpty() )
		{
			//transaction.remove(this.fragmentStack.peek());
			transaction.detach(this.fragmentStack.peek());
		}
		
		if ( !this.fragments.containsKey(className) )
		{	
			this.fragments.put(className, Fragment.instantiate(this.getActivity(), className));
			transaction.add(container, this.fragments.get(className));
			//transaction.replace(container, this.fragments.get(className), tag);
		}
		else
		{
			transaction.attach(this.fragments.get(className));
		}
		
		this.fragmentStack.push(this.fragments.get(className));
		
		if ( addToBackStack == true )
		{
			transaction.addToBackStack(null);
		}
		
		//this.fragments.clear();
		
		transaction.commit();
	}
	
	public void up()
	{
		this.getChildFragmentManager().popBackStack();
		
		this.fragments.remove(this.fragmentStack.peek().getClass().getName());
		
		this.fragmentStack.pop();
	}
	
	public void clearStack()
	{
		this.fragmentStack.clear();
	}
	
	public void clearFragments()
	{
		/*
		Collection<Fragment> fragmentCollection = this.fragments.values();
		Iterator<Fragment> it = fragmentCollection.iterator();
		while ( it.hasNext() )
		{
			Fragment currentFragment = it.next();
			currentFragment.de
		}*/
		
		this.fragments.clear();
	}
	
	public int getStackSize()
	{
		return this.fragmentStack.size();
	}
}