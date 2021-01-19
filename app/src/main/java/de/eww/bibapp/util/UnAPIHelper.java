package de.eww.bibapp.util;

import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.eww.bibapp.R;
import de.eww.bibapp.model.ModsItem;

public class UnAPIHelper {

    /**
     * This determs the URL for search requests
     */
    public static String getUnAPIUrl(Context context, ModsItem modsItem, String format)
    {
        return String.format(context.getResources().getString(R.string.bibapp_unapi_gvk_url), modsItem.ppn, format);
    }

    public static String convert(String[] lines, ModsItem modsItem) {
        String response = "";

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
                response += " " + searchSplit[1];
            }
            else
            {
                /**
                 * 	Wenn es ein Mehrbändiges Werk ist (f-Stufe) d.h. <partNumber> oder <partName> sind nicht null dann
                 */
                if ( !modsItem.partName.isEmpty() || !modsItem.partNumber.isEmpty() )
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
            }
        }

        currentLine++;
        if (lines.length > currentLine) {
            /**
             * Iteriere alle verbleibenden Zeilen
             */
            for (int i = currentLine; i < lines.length; i++) {
                /**
                 * Wenn die Zeile mit In:_ / Enth.:_ / Enthalten in:_ beginnt, die ganze Zeile anzeigen. Den Rest der Daten verwerfen.
                 */
                if (UnAPIHelper.lineContainsPartOfInformation(lines[i])) {
                    response += " " + lines[i];
                    break;
                }
            }
        }

        return response;
    }

    public static boolean lineContainsPartOfInformation(String line) {
        Pattern pattern = Pattern.compile("^(In: |Enth\\.: |Enthalten in: ).*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        return matcher.matches();
    }
}
