package de.eww.bibapp.data;

import java.util.Date;

public class FeeEntry
{
	public final String amount;
	public final String about;
	public final Date date;
	public final String sum;
	
	public FeeEntry(String amount, String about, Date date, String sum)
	{
		this.amount = amount;
		this.about = about;
		this.date = date;
		this.sum = sum;
	}
}
