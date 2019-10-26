package pl.devtomek.app.avriplatvbox.constant;

/**
 * List of the remote pilot code.
 *
 * @author DevTomek.pl
 */
public class ButtonConstant {

    public static final String UP = "1E108";
    public static final String RIGHT = "DE108";
    public static final String DOWN = "9E108";
    public static final String LEFT = "5E108";

    public static final String OK = "3E108";
    public static final String TOP_MENU = "98108";
    public static final String POP_UP = "58108";
    public static final String OPTIONS = "CE108";
    public static final String RETURN = "BE108";

    public static final String PLAY = "200B";
    public static final String PAUSE = "400B";
    public static final String STOP = "B";
    public static final String PRESET_PLUS = "8C108";
    public static final String PRESET_MINUS = "C108";
    public static final String TUNING_MINUS = "CC108";
    public static final String TUNING_PLUS = "2C108";

    public static final String RED = "A010C";
    public static final String GREEN = "6010C";
    public static final String YELLOW = "E010C";
    public static final String BLUE = "2010C";

    public static final String TV_VOL_UP = "490";
    public static final String TV_VOL_DOWN = "C90";
    public static final String TV_VOL_MUTE = "A50";

    public static final String DISPLAY = "18108";
    public static final String SLEEP = "30A";
    public static final String AUDIO = "48108";
    public static final String SUBTITLE = "88108";
    public static final String THREE_D = "3210C";

    public static final String SPEAKERS_TV_AUDIO = "A210C";
    public static final String POWER_ON_OFF = "540A";
    public static final String TVI_ON_OFF = "A90";

    private ButtonConstant() {
        // prevents the creation of instances for a class responsible only for storing constant values
    }

}
