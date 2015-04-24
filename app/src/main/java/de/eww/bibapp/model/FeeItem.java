package de.eww.bibapp.model;

import java.util.Date;

public class FeeItem
{
	public final String amount;
	public final String about;
	public final Date date;
	public final String sum;
	
	public FeeItem(String amount, String about, Date date, String sum)
	{
		this.amount = amount;
		this.about = about;
		this.date = date;
		this.sum = sum;
	}
}
