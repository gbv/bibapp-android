package de.eww.bibapp.data;

import java.util.Date;

public class BookedEntry
{
	public final String about;
	public final String	signature;
	public final Date date;
	public final String item;
	public final String edition;
	public final String barcode;
	
	public BookedEntry(String about, String signature, Date date, String item, String edition, String barcode)
	{
		this.about = about;
		this.signature = signature;
		this.date = date;
		this.item = item;
		this.edition = edition;
		this.barcode = barcode;
	}
}
