package de.eww.bibapp.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.Html;
import android.util.Xml;

public class NewsXmlParser
{
	// We don't use namespaces
    private static final String ns = null;
    
	public List<NewsEntry> parse(InputStream in) throws XmlPullParserException, IOException
	{
		try
		{
			XmlPullParser parser = Xml.newPullParser();
			
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			
			return this.readRss(parser);
		}
		finally
		{
			in.close();
		}
	}
	
	private List<NewsEntry> readRss(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		List<NewsEntry> entries = new ArrayList<NewsEntry>();
		
		parser.require(XmlPullParser.START_TAG, NewsXmlParser.ns, "rss");
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			// get records
			if ( name.equals("channel") )
			{
				entries = this.readChannel(parser);
			}
			else
			{
				this.skip(parser);
			}
		}
		
		return entries;
	}
	
	private List<NewsEntry> readChannel(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		List<NewsEntry> entries = new ArrayList<NewsEntry>();
		
		parser.require(XmlPullParser.START_TAG, NewsXmlParser.ns, "channel");
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			// get records
			if ( name.equals("item") )
			{
				entries.add(this.readItem(parser));
			}
			else
			{
				this.skip(parser);
			}
		}
		
		return entries;
	}
	
	private NewsEntry readItem(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String title = null;
		String link = null;
		String description = null;
		String content = null;
		
		parser.require(XmlPullParser.START_TAG, NewsXmlParser.ns, "item");
		while ( parser.next() != XmlPullParser.END_TAG )
		{
			if ( parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = parser.getName();
			
			// get records
			if ( name.equals("title") )
			{
				title = this.readTitle(parser);
			}
			else if ( name.equals("link") )
			{
				link = this.readLink(parser);
			}
			else if ( name.equals("description") )
			{
				description = this.readDescription(parser);
			}
			else if ( name.equals("content:encoded") )
			{
				content = this.readContent(parser);
			}
			else
			{
				this.skip(parser);
			}
		}
		
		String entryDescription;
		if ( !content.isEmpty() )
		{
			entryDescription = content;
		}
		else
		{
			entryDescription = description;
		}
		
		// filter html
		entryDescription = Html.fromHtml(entryDescription).toString();
		
		return new NewsEntry(title, link, entryDescription);
	}
	
	private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, NewsXmlParser.ns, "title");
	    String title = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, NewsXmlParser.ns, "title");
	    return title;
	}
	
	private String readLink(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, NewsXmlParser.ns, "link");
	    String link = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, NewsXmlParser.ns, "link");
	    return link;
	}
	
	private String readDescription(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, NewsXmlParser.ns, "description");
	    String description = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, NewsXmlParser.ns, "description");
	    return description;
	}
	
	private String readContent(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, NewsXmlParser.ns, "content:encoded");
	    String content = this.readText(parser);
	    parser.require(XmlPullParser.END_TAG, NewsXmlParser.ns, "content:encoded");
	    return content;
	}
	
	private String readText(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String result = "";
		
		if ( parser.next() == XmlPullParser.TEXT )
		{
			result = parser.getText();
			parser.nextTag();
		}
		
		return result;
	}
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		if ( parser.getEventType() != XmlPullParser.START_TAG )
		{
			throw new IllegalStateException();
		}
		
		int depth = 1;
		while ( depth != 0)
		{
			switch ( parser.next() )
			{
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
			}
		}
	}
}