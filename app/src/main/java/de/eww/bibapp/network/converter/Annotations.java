package de.eww.bibapp.network.converter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Annotations {
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DAIA {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ISBD {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface RSS {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface SRU {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface URI {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Json {
    }
}
