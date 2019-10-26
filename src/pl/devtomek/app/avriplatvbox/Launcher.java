package pl.devtomek.app.avriplatvbox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.devtomek.app.avriplatvbox.constant.FxConstant;

/**
 * Launcher for AVR Ipla TV Box application.
 *
 * @author DevTomek.pl
 */
public class Launcher {

    private static final Logger LOG = LogManager.getLogger(Launcher.class);

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        LOG.info("Starting launcher for {} application...", FxConstant.APP_TITLE);
        AvrIplaTvBoxApp.main(args);
    }

}