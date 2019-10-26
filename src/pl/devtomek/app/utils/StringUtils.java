package pl.devtomek.app.utils;

/**
 * Utils methods for {@link String}.
 *
 * @author DevTomek.pl
 */
public class StringUtils {

    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String NEW_LINE = "\n";

    // TODO refactor me
    public static String normalizePolishDialect(String text) {
        if (text != null) {
            return text
                    .replaceAll("[żź]", "z")
                    .replaceAll("ć", "c")
                    .replaceAll("ń", "n")
                    .replaceAll("ó", "o")
                    .replaceAll("ą", "a")
                    .replaceAll("ę", "e")
                    .replaceAll("ł", "l")
                    .replaceAll("ś", "s")
                    .replaceAll("[ŻŹ]", "Z")
                    .replaceAll("Ć", "C")
                    .replaceAll("Ń", "N")
                    .replaceAll("Ó", "O")
                    .replaceAll("Ą", "A")
                    .replaceAll("Ę", "E")
                    .replaceAll("Ł", "L")
                    .replaceAll("Ś", "S");
        } else {
            return null;
        }
    }

    private StringUtils() {
        // prevents the creation of class instances
    }

}
