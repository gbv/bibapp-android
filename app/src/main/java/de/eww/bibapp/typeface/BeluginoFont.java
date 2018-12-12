package de.eww.bibapp.typeface;

import android.content.Context;
import android.graphics.Typeface;

import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.iconics.typeface.ITypeface;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class BeluginoFont implements ITypeface {

    private static final String TTF_FILE = "belugino.ttf";

    private static Typeface typeface = null;
    private static HashMap<String, Character> characterMap;

    @Override
    public IIcon getIcon(String key) {
        return Icon.valueOf(key);
    }

    @Override
    public HashMap<String, Character> getCharacters() {
        if (characterMap == null) {
            HashMap<String, Character> characters = new HashMap<>();
            for (Icon v: Icon.values()) {
                characters.put(v.name(), v.character);
            }

            characterMap = characters;
        }

        return characterMap;
    }

    @Override
    public String getMappingPrefix() {
        return "bel";
    }

    @Override
    public String getFontName() {
        return "Belugino";
    }

    @Override
    public String getVersion() {
        return "2.0.2";
    }

    @Override
    public int getIconCount() {
        return 265;
    }

    @Override
    public Collection<String> getIcons() {
        Collection<String> icons = new LinkedList<>();

        for (Icon value: Icon.values()) {
            icons.add(value.name());
        }

        return icons;
    }

    @Override
    public String getAuthor() {
        return "";
    }

    @Override
    public String getUrl() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getLicense() {
        return "CC BY-SA 4.0";
    }

    @Override
    public String getLicenseUrl() {
        return "";
    }

    @Override
    public Typeface getTypeface(Context context) {
        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + TTF_FILE);
            } catch (Exception e) {
                return null;
            }
        }

        return typeface;
    }

    public enum Icon implements IIcon {
        bel_magnifier('\ue919'),
        bel_account('\ue903'),
        bel_content('\ue91b'),
        bel_info('\ue916'),
        bel_cog('\ue940'),
        bel_world('\ue901'),
        bel_book('\ue969'),
        bel_ebook('\ue96a'),
        bel_unknown('\ue960'),
        bel_video('\ue98c'),
        bel_note('\ue98b'),
        bel_map('\ue9ae'),
        bel_speaker('\ue98a'),
        bel_image('\ue981'),
        bel_online('\ue979'),
        bel_record('\ue987'),
        bel_page('\ue962'),
        bel_microform('\ue98e'),
        bel_manuscript('\ue9a3'),
        bel_article('\ue9aa'),
        bel_warning('\ue915'),
        bel_idcard('\ue95a'),
        bel_arrow_right('\ue928');

        char character;

        Icon(char character) {
            this.character = character;
        }

        public String getFormattedName() {
            return "{" + name() + "}";
        }

        public char getCharacter() {
            return character;
        }

        public String getName() {
            return name();
        }

        private static ITypeface typeface;

        public ITypeface getTypeface() {
            if (typeface == null) {
                typeface = new BeluginoFont();
            }
            return typeface;
        }
    }
}
