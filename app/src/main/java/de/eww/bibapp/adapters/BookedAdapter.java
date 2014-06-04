package de.eww.bibapp.adapters;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import de.eww.bibapp.R;
import de.eww.bibapp.data.PaiaDocument;

public class BookedAdapter extends ArrayAdapter<PaiaDocument>
{
	private final Context context;
    private final boolean isRequestPermitted;
	
	public BookedAdapter(Context context, int textViewResourceId, boolean isRequestPermitted)
	{
		super(context, textViewResourceId);
		
		this.context = context;
        this.isRequestPermitted = isRequestPermitted;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.fragment_booked_item_view, parent, false);
		
		TextView aboutView = (TextView) v.findViewById(R.id.booked_item_about);
		TextView signatureView = (TextView) v.findViewById(R.id.booked_item_signature);
		TextView dateView = (TextView) v.findViewById(R.id.booked_item_date);

        PaiaDocument document = this.getItem(position);
		
		aboutView.setText(document.getAbout());
		signatureView.setText(document.getLabel());

        if (document.getDueDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
            dateView.setText(dateFormat.format(document.getDueDate()));
        }

        // checkbox
        CheckBox checkbox = (CheckBox) v.findViewById(R.id.booked_item_checkbox);
        if (document.isCanCancel() && this.isRequestPermitted) {
            checkbox.setVisibility(View.VISIBLE);
        }
		
		return v;
	}
}
