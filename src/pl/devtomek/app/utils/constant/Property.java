package pl.devtomek.app.utils.constant;

/**
 * List of available keys for properties files.
 *
 * NOTE: The '.' char (dot) in key property is represented by '_' char (underscore).
 *
 * @author DevTomek.pl
 */
public enum Property {

    LOGIN,
    PASSWORD,
    MODE,
    OFFSET_X,
    OFFSET_Y,
    SERIAL_BAUD_RATE,
    SERIAL_PORT,
    HEADER_BIG_FONT_SIZE,
    HEADER_SMALL_FONT_SIZE,
    OPACITY;

    @Override
    public String toString() {
        return this.name()
                .toLowerCase()
                .replaceAll("_", ".");
    }

}
