package pl.devtomek.app.utils;

import java.util.Collection;

/**
 * Utils methods for {@link Collection}.
 *
 * @author DevTomek.pl
 */
public class CollectionUtils {

    private CollectionUtils() {
        // prevents the creation of class instances
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }

}
