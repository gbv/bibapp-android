package de.eww.bibapp.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Locale;

import de.eww.bibapp.R;
import de.eww.bibapp.data.SearchEntry;
import de.eww.bibapp.fragments.watchlist.WatchlistFragment;

public class SearchAdapter extends ArrayAdapter<SearchEntry>
{
	private final Context context;
	private final int textViewResourceId;
	private WatchlistFragment watchlistFragment;
	
	public SearchAdapter(Context context, int textViewResourceId)
	{
		super(context, textViewResourceId);
		
		this.context = context;
		this.textViewResourceId = textViewResourceId;
	}
	
	public void setWatchlistFragment(WatchlistFragment watchlistFragment)
	{
		this.watchlistFragment = watchlistFragment;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(this.textViewResourceId, parent, false);
		
		TextView titleView = (TextView) v.findViewById(R.id.search_item_title);
		TextView subView = (TextView) v.findViewById(R.id.search_item_sub);
		TextView authorView = (TextView) v.findViewById(R.id.search_item_author);
		ImageView imageView = (ImageView) v.findViewById(R.id.search_item_image);
		CheckBox checkboxView = (CheckBox) v.findViewById(R.id.detail_item_checkbox);
		
		if ( this.textViewResourceId == R.layout.fragment_watchlist_item_view )
		{
			checkboxView.setVisibility(View.VISIBLE);
			
			final int finalPostion = position;
			checkboxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					// update checked items
					SearchEntry searchItem = (SearchEntry) SearchAdapter.this.watchlistFragment.getListAdapter().getItem(finalPostion);
					if ( isChecked )
					{
						SearchAdapter.this.watchlistFragment.checkedItems.add(searchItem);
					}
					else
					{
						SearchAdapter.this.watchlistFragment.checkedItems.remove(searchItem);
					}
					
					// enable / disable menu item
					SearchAdapter.this.watchlistFragment.menuItem.setEnabled(!SearchAdapter.this.watchlistFragment.checkedItems.isEmpty());
				}
			});
		}
		
		SearchEntry entry = this.getItem(position);

        if (entry != null) {
            titleView.setText(entry.title);

            String subTitle = entry.subTitle;
            if ( !entry.partName.isEmpty() )
            {
                subTitle = entry.partName + "; " + subTitle;
            }
            if ( !entry.partNumber.isEmpty() )
            {
                subTitle = entry.partNumber + "; " + subTitle;
            }
            subView.setText(subTitle);

            String authorString = "";

            Iterator<String> it = entry.authors.iterator();
            while ( it.hasNext() )
            {
                String author = it.next();
                authorString += author;

                if ( it.hasNext() )
                {
                    authorString += ", ";
                }
            }

            authorView.setText(authorString);

            Resources res = this.context.getResources();
            imageView.setImageResource(res.getIdentifier("mediaicon_" + entry.mediaType.toLowerCase(Locale.GERMANY), "drawable", this.context.getPackageName()));
        }

		return v;
	}
}
