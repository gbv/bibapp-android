package de.eww.bibapp.tasks;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.URLConnectionHelper;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.data.SearchEntry;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 *
 * This file is part of the Android BibApp Project
 * =========================================================
 * Loader for unapi data
 */
public class UnApiLoader extends AsyncTaskLoader<String>
{
    private String entries;
    private String ppn;
    private SearchEntry searchEntry;
    private Fragment fragment;
    private boolean failure = false;

    private void raiseFailure()
    {
        this.failure = true;
    }

    public UnApiLoader(Context context, Fragment callingFragment)
    {
        super(context);

        this.fragment = callingFragment;
    }

    public void setPpn(String ppn)
    {
        this.ppn = ppn;
    }

    public void setSearchEntry(SearchEntry searchEntry)
    {
        this.searchEntry = searchEntry;
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading()
    {
        if ( this.entries != null )
        {
            // If we currently have a result available, deliver it immediately.
            this.deliverResult(this.entries);
        }

        if ( this.takeContentChanged() || this.entries == null )
        {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            this.forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading()
    {
        // Attempt to cancel the current load task if possible.
        this.cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(String data)
    {
        super.onCanceled(data);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset()
    {
        super.onReset();

        // Ensure the loader is stopped
        this.onStopLoading();

        this.entries = null;
    }

    /**
     * Called when there is new data to deliver to the client.
     * Also used to handle any failures while processing loadInBackground,
     * because OperationCanceledException unfortunately requires API Level 16.
     */
    @Override public void deliverResult(String data)
    {
        if ( this.failure == false )
        {
            super.deliverResult(data);
        }
        else
        {
            ((AsyncCanceledInterface) this.fragment).onAsyncCanceled();
        }
    }

    @Override
    public String loadInBackground()
    {
        String response = "";

        URLConnectionHelper urlConnectionHelper = new URLConnectionHelper(Constants.getUnApiUrl(this.ppn));

        try
        {
            urlConnectionHelper.configure();
            urlConnectionHelper.connect(null);

            InputStream input = new BufferedInputStream(urlConnectionHelper.getInputStream());

            String httpResponse = urlConnectionHelper.readStream(input);
            Log.v("UNAPI", httpResponse);

            String[] lines = httpResponse.split(System.getProperty("line.separator"));
            Pattern pattern;
            Matcher matcher;
            String[] searchSplit;
            int currentLine = 0;

            /**
             * 	Wenn aktuelle Zeile nur einen Eintrag mit Eckiger Klammer enthält ^\[.*\]$...
             */
            if ( lines.length > currentLine )
            {
                pattern = Pattern.compile("^\\[.*\\]$", Pattern.CASE_INSENSITIVE);
                matcher = pattern.matcher(lines[currentLine]);
                if ( matcher.find() )
                {
                    /**
                     * dann zur nächsten Zeile springen  (-> Hier stehen z.T. Angaben zum Typ des Dokuments wie [Periodical])
                     */
                    currentLine++;
                }
            }

            /**
             * 	Wenn aktuelle Zeile mit : endet :$
             */
            if ( lines.length > currentLine && lines[currentLine].endsWith(":") )
            {
                /**
                 * dann zur nächsten Zeile springen (-> Hier stehen manchmal Herausgeberangaben, die wir nicht benötigen.)
                 */
                currentLine++;
            }

            /**
             * 	Wenn die aktuelle Zeile mit eckigen Klammern beginnt ^\[.*\], diese entfernen (-> In diesen Klammern taucht oft ein _/_ auf, das alles durcheinander bringt.)
             */
            if ( lines.length > currentLine )
            {
                pattern = Pattern.compile("^\\[.*\\]", Pattern.CASE_INSENSITIVE);
                matcher = pattern.matcher(lines[currentLine]);
                if ( matcher.find() )
                {
                    searchSplit = lines[currentLine].split("\\]");
                    if ( searchSplit.length > 1 )
                    {
                        for (int i=1; i <= searchSplit.length-1; i++) {
                            lines[i] = "]" + searchSplit[i];
                        }
                    }
                    else
                    {
                        lines[currentLine] = "";
                    }
                }

                /**
                 * 	Wenn in der Zeile ein _/_ vorkommt, dann zeige alles danach an.
                 */
                searchSplit = lines[currentLine].split(" / ");
                if ( searchSplit.length > 1 )
                {
                    for (int i=1; i <= searchSplit.length-1; i++) {
                        if (!response.equals("")) {
                            response += " / ";
                        }

                        response += searchSplit[i];
                    }
                }
                else
                {
                    /**
                     * Wenn nicht, dann zeige alles nach dem ersten _-_ an, sofern vorhanden.
                     */
                    searchSplit = lines[currentLine].split(" \\- ");
                    if ( searchSplit.length > 1 )
                    {
                        response += searchSplit[1];
                    }
                    else
                    {
                        /**
                         * 	Wenn nicht, dann zeige die ganze Zeile an.
                         */
                        response += lines[currentLine];
                    }
                }
            }

            /**
             * 	Zur nächsten Zeile springen.
             */
            currentLine++;


            if ( lines.length > currentLine )
            {
                /**
                 * 	Wenn die Zeile mit "Congress:_" beginnt, alles nach "Congress:_" anzeigen. Den Rest der Daten verwerfen.
                 */
                searchSplit = lines[currentLine].split("Congress: ");
                if ( searchSplit.length > 1 )
                {
                    response += " " + searchSplit[currentLine];
                }
                else
                {
                    /**
                     * 	Wenn es ein Mehrbändiges Werk ist (f-Stufe) d.h. <partNumber> oder <partName> sind nicht null dann
                     */
                    if ( !this.searchEntry.partName.isEmpty() || !this.searchEntry.partNumber.isEmpty() )
                    {
                        /**
                         * Wenn in der Zeile ein _-_ vorkommt, dann zeige alles danach an.
                         */
                        searchSplit = lines[currentLine].split(" \\- ");
                        if ( searchSplit.length > 1 )
                        {
                            response += " " + searchSplit[1];
                        }
                    }

                    /**
                     * Wenn die Zeile mit In:_ beginnt, die ganze Zeile anzeigen. Den Rest der Daten verwerfen.
                     */
                    searchSplit = lines[currentLine].split("In: ");
                    if (searchSplit.length > 1) {
                        response += " " + searchSplit[currentLine];
                    }
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();

            this.raiseFailure();
        }

        finally
        {
            urlConnectionHelper.disconnect();
        }

        return response;
    }
}