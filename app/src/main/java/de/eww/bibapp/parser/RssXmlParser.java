package de.eww.bibapp.parser;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.model.RssItem;

/**
 * Created by christoph on 09.12.14.
 */
public class RssXmlParser {
    // We don't use namespaces
    private static final String ns = null;

    public List<RssItem> parse(InputStream in) throws XmlPullParserException, IOException
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

    private List<RssItem> readRss(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        List<RssItem> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, RssXmlParser.ns, "rss");
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

    private List<RssItem> readChannel(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        List<RssItem> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, RssXmlParser.ns, "channel");
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

    private RssItem readItem(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        RssItem rssItem = new RssItem();

        parser.require(XmlPullParser.START_TAG, RssXmlParser.ns, "item");
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
                rssItem.setTitle(this.readTitle(parser));
            }
            else if ( name.equals("link") )
            {
                this.readLink(parser);
            }
            else if ( name.equals("description") )
            {
                rssItem.setDescription(this.readDescription(parser));
            }
            else if ( name.equals("content:encoded") )
            {
                rssItem.setContent(this.readContent(parser));
            }
            else
            {
                this.skip(parser);
            }
        }

        return rssItem;
    }

    private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, RssXmlParser.ns, "title");
        String title = this.readText(parser);
        parser.require(XmlPullParser.END_TAG, RssXmlParser.ns, "title");
        return title;
    }

    private String readLink(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, RssXmlParser.ns, "link");
        String link = this.readText(parser);
        parser.require(XmlPullParser.END_TAG, RssXmlParser.ns, "link");
        return link;
    }

    private String readDescription(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, RssXmlParser.ns, "description");
        String description = this.readText(parser);
        parser.require(XmlPullParser.END_TAG, RssXmlParser.ns, "description");
        return description;
    }

    private String readContent(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, RssXmlParser.ns, "content:encoded");
        String content = this.readText(parser);
        parser.require(XmlPullParser.END_TAG, RssXmlParser.ns, "content:encoded");
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