package de.eww.bibapp.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.eww.bibapp.R;

public class LocaleManager {
    public static void changeLanguage(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration conf = new Configuration();
        if (Build.VERSION.SDK_INT >= 17) {
            conf.setLocale(locale);
        } else {
            conf.locale = locale;
        }

        context.getResources().updateConfiguration(conf, context.getResources().getDisplayMetrics());
    }

    public static String getDefaultLanguage(Context context) {
        String deviceLanguage = Locale.getDefault().getLanguage();

        List<String> supportedLanguages = Arrays.asList(context.getResources().getStringArray(R.array.settings_languages_entry_values));
        if (supportedLanguages.contains(deviceLanguage)) {
            return deviceLanguage;
        } else {
            return "de";
        }
    }
}
