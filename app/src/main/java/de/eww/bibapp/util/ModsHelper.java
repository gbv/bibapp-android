package de.eww.bibapp.util;

import de.eww.bibapp.network.model.ModsItem;
import de.eww.bibapp.typeface.BeluginoFont;

public class ModsHelper {
    public static BeluginoFont.Icon getBeluginoFontIcon(ModsItem modsItem) {
        String mediaType = modsItem.getMediaType().toLowerCase();

        switch (mediaType) {
            case "a":
                return BeluginoFont.Icon.bel_article;
            case "b":
                return BeluginoFont.Icon.bel_book;
            case "e":
                return BeluginoFont.Icon.bel_microform;
            case "ebook":
                return BeluginoFont.Icon.bel_ebook;
            case "h":
                return BeluginoFont.Icon.bel_manuscript;
            case "g":
                return BeluginoFont.Icon.bel_speaker;
            case "i":
                return BeluginoFont.Icon.bel_image;
            case "k":
                return BeluginoFont.Icon.bel_map;
            case "m":
                return BeluginoFont.Icon.bel_note;
            case "o":
            case "p":
                return BeluginoFont.Icon.bel_online;
            case "s":
                return BeluginoFont.Icon.bel_record;
            case "t":
                return BeluginoFont.Icon.bel_page;
            case "v":
                return BeluginoFont.Icon.bel_video;
            default:
                return BeluginoFont.Icon.bel_unknown;
        }
    }
}
